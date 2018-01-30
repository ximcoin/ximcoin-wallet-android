package tech.duchess.luminawallet.model.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;
import tech.duchess.luminawallet.model.persistence.account.Account;
import tech.duchess.luminawallet.model.persistence.account.Balance;
import tech.duchess.luminawallet.view.util.TextUtils;

public class AccountUtil {
    public static final String PUBLIC_PREFIX = "G";
    public static final String SECRET_PREFIX = "S";
    public static final int KEY_LENGTH = 56;

    public static boolean publicKeyOfProperLength(@Nullable String publicKey) {
        return !TextUtils.isEmpty(publicKey) && publicKey.length() == KEY_LENGTH;
    }

    public static boolean publicKeyOfProperPrefix(@Nullable String publicKey) {
        return publicKey != null && publicKey.startsWith(PUBLIC_PREFIX);
    }

    public static boolean secretSeedOfProperLength(@Nullable String seed) {
        return !TextUtils.isEmpty(seed) && seed.length() == KEY_LENGTH;
    }

    public static boolean secretSeedOfProperPrefix(@Nullable String seed) {
        return seed != null && seed.startsWith(SECRET_PREFIX);
    }

    // TODO: Verify the presence of a balance infers a trustline having been set.
    public static boolean trustsAsset(@NonNull Account account,
                                      @NonNull String assetCode,
                                      @NonNull String assetIssuer) {
        if (assetCode.equals(AssetUtil.LUMEN_ASSET_CODE)) {
            return true;
        }

        List<Balance> balanceList = account.getBalances();

        if (balanceList == null || balanceList.isEmpty()) {
            return false;
        }

        return Observable.fromIterable(balanceList)
                .filter(balance ->
                        assetIssuer.equals(balance.getAsset_issuer())
                                && assetCode.equals(balance.getAsset_code()))
                .map(balance -> true)
                .blockingFirst(false);
    }
}
