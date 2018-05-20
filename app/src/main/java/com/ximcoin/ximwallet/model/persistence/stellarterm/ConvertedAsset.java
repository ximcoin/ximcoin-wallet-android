package com.ximcoin.ximwallet.model.persistence.stellarterm;

public class ConvertedAsset {
    private String id;
    private String price_USD;
    private String price_XLM;

    public ConvertedAsset(String id, String price_USD, String price_XLM) {
        this.id = id;
        this.price_USD = price_USD;
        this.price_XLM = price_XLM;
    }

    public String getId() {
        return id;
    }

    public String getPrice_USD() {
        return price_USD;
    }

    public String getPrice_XLM() {
        return price_XLM;
    }
}
