package com.jansmoneymachine.currencyconverter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ExchangeRateListView extends AppCompatActivity {

    // Attributes
    private ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    private ExchangeRate[] CURRENCIES = exchangeRateDatabase.getRATES();
    private ListView exchangeRateListView;
    private CustomCurrencyAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate_list_view);

        exchangeRateListView = (ListView) findViewById(R.id.exchangeRateListView);
        customAdapter = new CustomCurrencyAdapter(this, exchangeRateDatabase);
        exchangeRateListView.setAdapter(customAdapter);

        // Opens Google Maps on the currencies capital
        exchangeRateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Intent intent_gmaps = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + CURRENCIES[position].getCapital()));
                startActivity(intent_gmaps);
                Toast.makeText(ExchangeRateListView.this, "The capital of "
                        + CURRENCIES[position].getCurrencyName()
                        + " is: "
                        + CURRENCIES[position].getCapital(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

