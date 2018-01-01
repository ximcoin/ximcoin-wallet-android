package tech.duchess.luminawallet.model.persistence.account;

public class Balance {
    private String asset_type;
    private String asset_code;
    private String asset_issuer;
    private double balance;
    private double limit;

    public String getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(String asset_type) {
        this.asset_type = asset_type;
    }

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getAsset_issuer() {
        return asset_issuer;
    }

    public void setAsset_issuer(String asset_issuer) {
        this.asset_issuer = asset_issuer;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }
}
