package com.ximcoin.ximwallet.model.persistence.coinmarketcap;

import com.squareup.moshi.FromJson;

import java.util.Map;

public class ConversionRateAdapter {
    @FromJson
    ConversionRate fromJson(Map<String, String>[] jsonMaps) {
        Map<String, String> jsonMap = jsonMaps[0];
        ConversionRate conversionRate = new ConversionRate();
        conversionRate.setLastUpdated(jsonMap.get("last_updated"));
        conversionRate.setPriceUSD(jsonMap.get("price_usd"));
        conversionRate.setPriceBTC(jsonMap.get("price_btc"));
        conversionRate.setConvertedPrice(jsonMap.get(ConversionRate.CONVERSION_PREFIX +
                ConversionRate.TARGET_CONVERSION.toLowerCase()));
        return conversionRate;
    }
}
