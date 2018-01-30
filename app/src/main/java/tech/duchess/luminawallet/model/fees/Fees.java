package tech.duchess.luminawallet.model.fees;

/**
 * A class that can help calculate fees dynamically. This is actually just a subset of the GET
 * ledgers response. Ideally this should only be updated every few years. Being that you have to
 * have network connection to make a transaction to begin with, we can rely on grabbing the latest
 * fees before every transaction to give confidence to our fee estimates prior to transaction
 * execution.
 *
 * https://www.stellar.org/developers/guides/concepts/fees.html
 */
public class Fees {
    private int base_fee = 100;
    private String base_reserve = "0.5000000";

    public double getBase_fee() {
        return base_fee;
    }

    public void setBase_fee(int base_fee) {
        this.base_fee = base_fee;
    }

    public String getBase_reserve() {
        return base_reserve;
    }

    public void setBase_reserve(String base_reserve) {
        this.base_reserve = base_reserve;
    }
}
