package com.ximcoin.ximwallet.model.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.stellar.sdk.KeyPair;

import java.util.List;

import io.reactivex.Observable;

import com.ximcoin.ximwallet.EnvironmentConstants;
import com.ximcoin.ximwallet.model.fees.Fees;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.persistence.account.Balance;
import com.ximcoin.ximwallet.view.util.TextUtils;

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

    public static boolean publicKeyCanBeDecoded(@Nullable String publicKey) {
        if (publicKey == null) {
            return false;
        }

        try {
            KeyPair.fromAccountId(publicKey);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean secretSeedOfProperLength(@Nullable String seed) {
        return !TextUtils.isEmpty(seed) && seed.length() == KEY_LENGTH;
    }

    public static boolean secretSeedOfProperPrefix(@Nullable String seed) {
        return seed != null && seed.startsWith(SECRET_PREFIX);
    }

    public static boolean secretSeedCanBeDecoded(@Nullable String secretSeed) {
        if (secretSeed == null) {
            return false;
        }

        try {
            KeyPair.fromSecretSeed(secretSeed);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static double getTransactionsRemaining(@NonNull Account account, @NonNull Fees fees) {
        double lumenBalance = account.getLumens().getBalance();
        double minAccountBalance = FeesUtil.getMinimumAccountBalance(fees, account);

        if (lumenBalance - minAccountBalance <= 0) {
            return 0;
        } else {
            return Math.round((lumenBalance - minAccountBalance)/FeesUtil.getTransactionFee(fees, 1));
        }
    }

    public static boolean hasFundsToAddATrustline(@NonNull Fees fees, @NonNull Account account) {
        double curMinBalance = FeesUtil.getMinimumAccountBalance(fees, account);
        double curLumens = account.getLumens().getBalance();
        return curLumens - curMinBalance - Double.parseDouble(fees.getBase_reserve())
                - FeesUtil.getTransactionFee(fees, 1) >= 0;
    }

    public static boolean trustsXim(@NonNull Account account) {
        return trustsAsset(account, AssetUtil.XIM_ASSET_CODE, EnvironmentConstants.XIM_ISSUER);
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
