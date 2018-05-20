package com.ximcoin.ximwallet.model.persistence.stellarterm;

public class ConversionRate {
    private ConversionMetaData _meta;
    private ConvertedAsset[] assets;

    public ConversionRate(ConversionMetaData _meta, ConvertedAsset[] assets) {
        this._meta = _meta;
        this.assets = assets;
    }

    public long getLastUpdated() {
        return _meta.getStart();
    }

    public String getPriceUSD() {
        for (ConvertedAsset asset : assets) {
            if (asset.getId().equals("XIM-GBZ35ZJRIKJGYH5PBKLKOZ5L6EXCNTO7BKIL7DAVVDFQ2ODJEEHHJXIM")) {
                return asset.getPrice_USD();
            }
        }

        return "";
    }
}
