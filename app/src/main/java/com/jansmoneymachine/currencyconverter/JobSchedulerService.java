package com.jansmoneymachine.currencyconverter;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class JobSchedulerService extends JobService {
    private static final String TAG = "Example";
    public boolean jobCancelled = false;

    private ExchangeRateDatabase exchangeRateDatabase;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        loadData();   // Loads initial exchangeRateDatabase
        doBackgroundWork(params); // Updates it
        saveData(); // Store it back, so it can be loaded again from MainActivity with its updated currency rates
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating started.");
                updateCurrencies();
                //if (jobCancelled = true) {
                //  return;
                //}

                Log.d(TAG, "Updating finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    private void updateCurrencies() {
        // Web XML of the "Europäische Zentralbank" - gets updated daily
        String urlString = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

        try {
            // Build up connection
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(connection.getInputStream(), connection.getContentEncoding());

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if ("Cube".equals(parser.getName())) {
                        if (parser.getAttributeCount() > 1) {
                            String rateName = parser.getAttributeValue(null, "currency");
                            double rateValue = Double.parseDouble(parser.getAttributeValue(null, "rate"));
                            exchangeRateDatabase.setExchangeRate(rateName, rateValue);
                        }
                    }
                }
                eventType = parser.next();
            }

            // Catching errors
        } catch (XmlPullParserException e) {
            //Toast.makeText(MainActivity.this, "Problem with XML", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (MalformedURLException e) {
            //Toast.makeText(MainActivity.this, "Problem with URL", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            //Toast.makeText(MainActivity.this, "IO Exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void saveData() {
        //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(exchangeRateDatabase);
        sharedPrefEditor.putString("ExchangeRateDatabase", json).apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("ExchangeRateDatabase", "");
        exchangeRateDatabase = gson.fromJson(json, ExchangeRateDatabase.class);
    }
}
