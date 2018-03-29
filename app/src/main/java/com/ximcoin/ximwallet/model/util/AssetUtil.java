package com.ximcoin.ximwallet.model.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.ximcoin.ximwallet.EnvironmentConstants;
import com.ximcoin.ximwallet.model.persistence.account.Balance;

import org.stellar.sdk.Asset;
import org.stellar.sdk.KeyPair;

public class AssetUtil {
    public static NumberFormat XIM_FORMAT = DecimalFormat.getInstance();
    public static DecimalFormat ASSET_FORMAT = new DecimalFormat("#,##0.#");
    public static final String LUMEN_ASSET_TYPE = "native";
    public static final String LUMEN_ASSET_CODE = "XLM";
    public static final String LUMENS_FULL_NAME = "Lumens";
    public static final String XIM_ASSET_CODE = "XIM";
    public static final Asset XIM_ASSET = Asset.createNonNativeAsset(XIM_ASSET_CODE,
            KeyPair.fromAccountId(EnvironmentConstants.XIM_ISSUER));
    // 10 Billion
    public static final String XIM_ASSET_LIMIT = "10000000000";

    static {
        ASSET_FORMAT.setMaximumFractionDigits(7);
        XIM_FORMAT.setMaximumFractionDigits(0);
    }

    public static String getAssetCode(@Nullable String assetType,
                                      @Nullable String assetCode) {
        return LUMEN_ASSET_TYPE.equals(assetType) ? LUMEN_ASSET_CODE : assetCode;
    }

    public static String getAssetIssuer(@Nullable String assetType,
                                        @Nullable String assetIssuer) {
        return LUMEN_ASSET_TYPE.equals(assetType) ? LUMEN_ASSET_TYPE : assetIssuer;
    }

    public static boolean isLumenBalance(@NonNull Balance balance) {
        return LUMEN_ASSET_TYPE.equals(balance.getAsset_type());
    }

    public static boolean isXimBalance(@NonNull Balance balance) {
        return XIM_ASSET_CODE.equals(balance.getAsset_code())
                && EnvironmentConstants.XIM_ISSUER.equals(balance.getAsset_issuer());
    }

    public static String getAssetAmountString(double amount) {
        return ASSET_FORMAT.format(amount);
    }

    public static String getXimAmountString(double ximAmount) {
        return XIM_FORMAT.format(ximAmount);
    }
}
