package tech.duchess.luminawallet.model.persistence.account;

public class Flags {
    private boolean auth_required;
    private boolean auth_revocable;

    public boolean isAuth_required() {
        return auth_required;
    }

    public void setAuth_required(boolean auth_required) {
        this.auth_required = auth_required;
    }

    public boolean isAuth_revocable() {
        return auth_revocable;
    }

    public void setAuth_revocable(boolean auth_revocable) {
        this.auth_revocable = auth_revocable;
    }
}
