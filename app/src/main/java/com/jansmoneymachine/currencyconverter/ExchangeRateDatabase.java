package com.jansmoneymachine.currencyconverter;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRateDatabase {

    public ExchangeRateDatabase() {
        updateExchangeRate();
    }

    // Exchange rates to EURO - price for 1 Euro
    private final static ExchangeRate[] RATES = {
            new ExchangeRate("EUR","Bruxelles",    1.0,         R.drawable.flag_eur),
            new ExchangeRate("USD","Washington",   1.0845,      R.drawable.flag_usd),
            new ExchangeRate("JPY","Tokyo",        130.02,      R.drawable.flag_jpy),
            new ExchangeRate("BGN","Sofia",        1.9558,      R.drawable.flag_bgn),
            new ExchangeRate("CZK","Prague",       27.473,      R.drawable.flag_czk),
            new ExchangeRate("DKK","Copenhagen",   7.4690,      R.drawable.flag_dkk),
            new ExchangeRate("GBP","London",       0.73280,     R.drawable.flag_gbp),
            new ExchangeRate("HUF","Budapest",     299.83,      R.drawable.flag_huf),
            new ExchangeRate("PLN","Warsaw",       4.0938,      R.drawable.flag_pln),
            new ExchangeRate("RON","Bucharest",    4.4050,      R.drawable.flag_ron),
            new ExchangeRate("SEK","Stockholm",    9.3207,      R.drawable.flag_sek),
            new ExchangeRate("CHF","Bern",         1.0439,      R.drawable.flag_chf),
            new ExchangeRate("NOK","Oslo",         8.6545,      R.drawable.flag_nok),
            new ExchangeRate("HRK","Zagreb",       7.6448,      R.drawable.flag_hrk),
            new ExchangeRate("RUB","Moscow",       62.5595,     R.drawable.flag_rub),
            new ExchangeRate("TRY","Ankara",       2.8265,      R.drawable.flag_try),
            new ExchangeRate("AUD","Canberra",     1.4158,      R.drawable.flag_aud),
            new ExchangeRate("BRL","Brasilia",     3.5616,      R.drawable.flag_brl),
            new ExchangeRate("CAD","Ottawa",       1.3709,      R.drawable.flag_cad),
            new ExchangeRate("CNY","Beijing",      6.7324,      R.drawable.flag_cny),
            new ExchangeRate("HKD","Hong Kong",    8.4100,      R.drawable.flag_hkd),
            new ExchangeRate("IDR","Jakarta",      14172.71,    R.drawable.flag_idr),
            new ExchangeRate("ILS","Jerusalem",    4.3019,      R.drawable.flag_ils),
            new ExchangeRate("INR","New Delhi",    67.9180,     R.drawable.flag_inr),
            new ExchangeRate("KRW","Seoul",        1201.04,     R.drawable.flag_krw),
            new ExchangeRate("MXN","Mexico City",  16.5321,     R.drawable.flag_mxn),
            new ExchangeRate("MYR","Kuala Lumpur", 4.0246,      R.drawable.flag_myr),
            new ExchangeRate("NZD","Wellington",   1.4417,      R.drawable.flag_nzd),
            new ExchangeRate("PHP","Manila",       48.527,      R.drawable.flag_php),
            new ExchangeRate("SGD","Singapore",    1.4898,      R.drawable.flag_sgd),
            new ExchangeRate("THB","Bangkok",      35.328,      R.drawable.flag_thb),
            new ExchangeRate("ZAR","Cape Town",    13.1446,     R.drawable.flag_zar)
    };

    private final static Map<String, Double> CURRENCIES_MAP = new HashMap<>();

    static void updateExchangeRate() {
        for (ExchangeRate r : RATES) {
            CURRENCIES_MAP.put(r.getCurrencyName(), r.getRateForOneEuro());
        }
        /* Removed some parts of this method.
        ** Reason: Copied map with currency rate values didn't update, when rates got refreshed
         */
    }

    /**
     * Gets exchange rate for currency (equivalent for one Euro)
     */

    public double getExchangeRate(String currency) {
        return CURRENCIES_MAP.get(currency);
    }

    /**
     * Converts a value from a currency to another one
     *
     * @return converted value
     */
    public double convert(double value, String currencyFrom, String currencyTo) {
        return value / CURRENCIES_MAP.get(currencyFrom) * CURRENCIES_MAP.get(currencyTo);
    }

    public ExchangeRate[] getRATES() {
        return RATES;
    }

    public void setExchangeRate(String currencyName, Double currencyRate) {
        for (int i = 0; i < RATES.length; i++) {
            if (RATES[i].getCurrencyName().equals(currencyName)) {
                RATES[i].setRateForOneEuro(currencyRate);
            }
        }
    }
}

