package tech.duchess.luminawallet.model.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.model.persistence.account.Balance;

public class AssetUtil {
    public static final String LUMEN_ASSET_TYPE = "native";
    public static final String LUMEN_ASSET_CODE = "XLM";

    public static String getAssetCode(@Nullable String assetType,
                               @Nullable String assetCode) {
        return LUMEN_ASSET_TYPE.equals(assetType) ? LUMEN_ASSET_CODE : assetCode;
    }

    public static boolean isLumenBalance(@NonNull Balance balance) {
        return LUMEN_ASSET_TYPE.equals(balance.getAsset_type());
    }
}
