package com.jansmoneymachine.currencyconverter;

// Zusammenarbeit mit Michael Rotärmel

import com.google.gson.*;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    // Attributes
    private String stringForShare = "No values to share!"; //Fills the shareButton later
    private ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    private Spinner spinner_fromValue;
    private Spinner spinner_toValue;
    private EditText txt_input;
    private EditText txt_result;
    private Button btn_calculate;
    private Toolbar myToolbar;
    private CustomCurrencyAdapter customCurrencyAdapter;
    private DecimalFormat fourDForm = new DecimalFormat("#.####");

    // Saving data
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXCHANGE_DATA_BASE = "exchangeDataBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        ** Would update the currency rates, when app gets started.
        ** Not activated to show function of the "Refresh Rates" menu item.
        *
        //updateCurrencies();
         */


        // Initially saves the exchangeDatabase
        saveData();

        // Starts background job for updating the currency rates
        scheduleUpdateJob();


        // Fix for network access --- NOT USED ANYMORE
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        /*
         ** Initializing elements
         */

        // Spinner elements
        spinner_fromValue = (Spinner) findViewById(R.id.spinner_fromValue);
        spinner_toValue = (Spinner) findViewById(R.id.spinner_toValue);

        // Button elements
        btn_calculate = (Button) findViewById(R.id.btn_calculate);

        // Text field elements
        txt_input = (EditText) findViewById(R.id.txt_input);
        txt_result = (EditText) findViewById(R.id.txt_result);

        // Toolbar
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        /*
         ** End of elements
         */

        // Spinner click listener
        spinner_fromValue.setOnItemSelectedListener(this);
        spinner_toValue.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        customCurrencyAdapter = new CustomCurrencyAdapter(this, exchangeRateDatabase);

        // Attaching data adapter to spinner
        spinner_fromValue.setAdapter(customCurrencyAdapter);
        spinner_toValue.setAdapter(customCurrencyAdapter);


        // Button listener | btn_calculate clicked
        btn_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    // ActionBar Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_currencylist_button:
                Intent intent_ExchangeRateListView = new Intent(this, ExchangeRateListView.class);
                startActivity(intent_ExchangeRateListView);
                return true;

            case R.id.item_share_button:
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intentShare.putExtra(Intent.EXTRA_TEXT, stringForShare);
                startActivity(Intent.createChooser(intentShare, "")); // No really nice input text for now
                //startActivity(intentShare); Works also, looks different
                return true;

            case R.id.item_currencylist_button2:
                Intent intent_ExchangeRateListView2 = new Intent(this, ExchangeRateListView.class);
                startActivity(intent_ExchangeRateListView2);
                return true;

            case R.id.item_refresh_rates:
                // NOT USED ANYMORE
                //ExchangeRateDatabase.updateExchangeRate();
                //customCurrencyAdapter.notifyDataSetChanged();
                //updateCurrencies();
                //calculate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void calculate() {
        // Gets value from txt_input and converts into double
        String input = txt_input.getText().toString();

        loadData();


        // Catches missing input with error message & also prevents the app from crashing
        if (TextUtils.isEmpty(input)) {
            txt_input.setError("Input missing!");
            return;

        } else {
            double valueInput = Double.parseDouble(input);

            // Gets position from selected spinner item, which comes from custom adapter
            // Position von Spinner-Item, mit Hilfe der Position das Item holen, über das Item (von Klasse ExchangeRate) den Namen als String abrufen
            int positionFrom = spinner_fromValue.getSelectedItemPosition();
            ExchangeRate objectFrom = (ExchangeRate) customCurrencyAdapter.getItem(positionFrom);
            String currencyNameFrom = objectFrom.getCurrencyName();

            int positionTo = spinner_toValue.getSelectedItemPosition();
            ExchangeRate objectTo = (ExchangeRate) customCurrencyAdapter.getItem(positionTo);
            String currencyNameTo = objectTo.getCurrencyName();

            // Calculates the result
            String result = String.valueOf((fourDForm.format(exchangeRateDatabase.convert(valueInput, currencyNameFrom, currencyNameTo))));

            // Set result
            txt_result.setText(result);


            // Create filling for SHARE BUTTON --- Perhaps not beautiful, but works
            stringForShare = "The Currency Converter says: " + input + " " + currencyNameFrom + " are" + " " + result + " " + currencyNameTo;
        }
    }

    public void scheduleUpdateJob() {
        ComponentName componentName = new ComponentName(this, JobSchedulerService.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // Starts only if connected with WIFI
                .setPersisted(true) // Survives a reboot
                .setPeriodic(24 * 60 * 60 * 1000L) // One-Day-Interval
                .build();

        JobScheduler schedulerService = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = schedulerService.schedule(info);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler schedulerService = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        schedulerService.cancel(1);
        Log.d(TAG, "Job cancelled");
    }


    // Store Data
    public void saveData() {
        //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(exchangeRateDatabase);
        sharedPrefEditor.putString("ExchangeRateDatabase",json).apply();

    }

    // Load Data
    public void loadData() {
        //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("ExchangeRateDatabase", "");
        exchangeRateDatabase = gson.fromJson(json, ExchangeRateDatabase.class);
    }

    // NOT USED ANYMORE
    /* private void updateCurrencies() {
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
            Toast.makeText(MainActivity.this, "Problem with XML", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Toast.makeText(MainActivity.this, "Problem with URL", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "IO Exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Updating ExchangeRateDatabase
        ExchangeRateDatabase.updateExchangeRate();
        // Updating the exchange rate which is shown in spinner, when rates get updated
        customCurrencyAdapter.notifyDataSetChanged();
        // Do the calculation again, when rates get updated, to update the result "automatically"
        calculate();
    }
    */
}


