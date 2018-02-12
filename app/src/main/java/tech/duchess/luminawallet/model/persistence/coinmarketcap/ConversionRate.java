package tech.duchess.luminawallet.model.persistence.coinmarketcap;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ConversionRate {
    @NonNull
    public static String TARGET_CONVERSION = "USD";
    public static final String CONVERSION_PREFIX = "price_";

    private String lastUpdated;
    private String priceUSD;
    private String priceBTC;
    private String convertedPrice;

    @Nullable
    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(@Nullable String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Nullable
    public String getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(@Nullable String priceUSD) {
        this.priceUSD = priceUSD;
    }

    @Nullable
    public String getPriceBTC() {
        return priceBTC;
    }

    public void setPriceBTC(@Nullable String priceBTC) {
        this.priceBTC = priceBTC;
    }

    @Nullable
    public String getConvertedPrice() {
        return convertedPrice;
    }

    public void setConvertedPrice(@Nullable String convertedPrice) {
        this.convertedPrice = convertedPrice;
    }
}
