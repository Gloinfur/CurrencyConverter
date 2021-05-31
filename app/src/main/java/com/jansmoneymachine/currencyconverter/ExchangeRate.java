package com.jansmoneymachine.currencyconverter;

public class ExchangeRate {
    private String currencyName;
    private double rateForOneEuro;
    private int flag;
    private String capital;

    public ExchangeRate(String currencyName, String capital, double rateForOneEuro, int flag) {
        this.currencyName = currencyName;
        this.rateForOneEuro = rateForOneEuro;
        this.flag = flag;
        this.capital = capital;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCapital() {
        return capital;
    }

    public double getRateForOneEuro() {
        return rateForOneEuro;
    }

    public int getFlag() {
        return flag;
    }

    public void setRateForOneEuro(double rateForOneEuro) {
        this.rateForOneEuro = rateForOneEuro;
    }
}
