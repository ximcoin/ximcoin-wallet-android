package tech.duchess.luminawallet.model.util;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.EnvironmentConstants;
import tech.duchess.luminawallet.model.fees.Fees;
import tech.duchess.luminawallet.model.persistence.account.Account;

public class FeesUtil {

    /**
     * ((2 + # of Entries) * Base Reserve). An entry can be one of the following:
     *
     * Trustline
     * Offer
     * Signer
     * Data Entry
     *
     * @return The minimum balance the account can have, in Lumens.
     */
    public static double getMinimumAccountBalance(@NonNull Fees fees,
                                                  @NonNull Account account) {
        // TODO: Figure out which equation is proper.
        return (2 + account.getSubentry_count()) * Double.parseDouble(fees.getBase_reserve());
        // return (2+ account.getSubentry_count() + account.getSigners().size()) * fees.getBase_reserve();
    }

    /**
     * (# of Operations * Base Fee). An operation can be one of the following:
     *
     * Create Account
     * Payment
     * Path Payment
     * Manage Offer
     * Create Passive Offer
     * Set Options
     * Change Trust
     * Allow Trust
     * Account Merge
     * Inflation
     * Manage Data
     *
     * https://www.stellar.org/developers/guides/concepts/list-of-operations.html
     *
     * @return The cost of making a transaction, with respect to the number of operations occurring
     * within the transaction.
     */
    public static double getTransactionFee(@NonNull Fees fees,
                                           int operationCount) {
        return operationCount * fees.getBase_fee() * EnvironmentConstants.BASE_FEE_PRECISION;
    }
}
