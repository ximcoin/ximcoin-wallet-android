package tech.duchess.luminawallet.model.persistence.appflag;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class AppFlag {
    @NonNull
    @PrimaryKey
    private String flagKey = "";

    private boolean value;

    @NonNull
    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(@NonNull String flagKey) {
        this.flagKey = flagKey;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
