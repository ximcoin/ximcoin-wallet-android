package tech.duchess.luminawallet.model.fees;

import tech.duchess.luminawallet.EnvironmentConstants;

/**
 * A class that can help calculate fees dynamically. This is actually just a subset of the GET
 * ledgers response. Ideally this should only be updated every few years. It's best however to let
 * the user know that we don't know what the current fee is if we can't make the network request
 * for these details at runtime. Otherwise, being able to make this request, we can let the user
 * know their anticipated fees prior to the transaction with confidence.
 *
 * It might be in our best interest to cache and display the last known fees (and hardcode the set
 * of current fees at build time), with a big disclaimer that not being able to make the network
 * request means we have zero confidence.
 *
 * https://www.stellar.org/developers/guides/concepts/fees.html
 */
public class Fees {
    private double base_fee;
    private double base_reserve;

    public double getBaseFee() {
        return base_fee;
    }

    public double getBaseReserve() {
        return base_reserve;
    }

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
    public double getMinimumAccountBalance(int trustlines, int offers, int signers, int dataEntries) {
        return (2 + trustlines + offers + signers + dataEntries) * base_reserve;
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
    public double getTransactionFee(int operationCount) {
        return operationCount * base_fee * EnvironmentConstants.BASE_FEE_PRECISION;
    }
}
