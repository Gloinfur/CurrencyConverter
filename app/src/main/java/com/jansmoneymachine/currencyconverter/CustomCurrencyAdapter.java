package com.jansmoneymachine.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomCurrencyAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private ExchangeRateDatabase exchangeRateDatabase;
    private LayoutInflater inflater;
    private ImageView imageView;
    private TextView txtView_currencyName;
    private TextView txtView_currencyRate;


    public CustomCurrencyAdapter(Context context, ExchangeRateDatabase exchangeRateDatabase) {
        this.context = context;
        this.exchangeRateDatabase = exchangeRateDatabase;
    }

    @Override
    public int getCount() {
        return exchangeRateDatabase.getRATES().length;
    }

    @Override
    public Object getItem(int position) {
        return exchangeRateDatabase.getRATES()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ExchangeRate[] CURRENCIES = exchangeRateDatabase.getRATES();
        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_custom_currency_adapter, parent, false);
        }

        imageView = (ImageView) view.findViewById(R.id.imageView_flag);
        txtView_currencyName = (TextView) view.findViewById(R.id.txtView_currencyName);
        txtView_currencyRate = (TextView) view.findViewById(R.id.txtView_currencyRate);

        if (CURRENCIES[position] != null) {
            imageView.setImageResource(CURRENCIES[position].getFlag());
            txtView_currencyName.setText(CURRENCIES[position].getCurrencyName());
            txtView_currencyRate.setText(String.valueOf(CURRENCIES[position].getRateForOneEuro()));
        }
        return view;
    }
}
