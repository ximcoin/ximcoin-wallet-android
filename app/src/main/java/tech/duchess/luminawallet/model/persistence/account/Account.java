package tech.duchess.luminawallet.model.persistence.account;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.duchess.luminawallet.LuminaWalletApp;

@Entity
@TypeConverters(Account.AccountTypeConverters.class)
public class Account {
    @NonNull
    @PrimaryKey
    private String id;
    private String paging_token;
    private String account_id;
    private long sequence;
    private long subentry_count;
    @Embedded
    private Thresholds thresholds;
    @Embedded
    private Flags flags;
    private List<Balance> balances;
    private List<Signer> signers;
    private Map<String, String> data;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPaging_token() {
        return paging_token;
    }

    public void setPaging_token(String paging_token) {
        this.paging_token = paging_token;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getSubentry_count() {
        return subentry_count;
    }

    public void setSubentry_count(long subentry_count) {
        this.subentry_count = subentry_count;
    }

    public Thresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(Thresholds thresholds) {
        this.thresholds = thresholds;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public List<Signer> getSigners() {
        return signers;
    }

    public void setSigners(List<Signer> signers) {
        this.signers = signers;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public static class AccountTypeConverters {
        private static final JsonAdapter<List<Balance>> balanceAdapter;
        private static final JsonAdapter<List<Signer>> signerAdapter;
        private static final JsonAdapter<Map<String, String>> dataAdapter;

        static {
            Moshi moshi = LuminaWalletApp.getInstance().getMoshi();
            balanceAdapter = moshi.adapter(Types.newParameterizedType(List.class, Balance.class));
            signerAdapter = moshi.adapter(Types.newParameterizedType(List.class, Signer.class));
            dataAdapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, String.class));
        }

        @TypeConverter
        public static List<Balance> stringToBalance(String json) {
            if (isEmpty(json)) {
                return new ArrayList<>();
            }

            try {
                return balanceAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for balance", e);
            }
        }

        @TypeConverter
        public static String balanceToString(List<Balance> list) {
            return balanceAdapter.toJson(list);
        }

        @TypeConverter
        public static List<Signer> stringToSigner(String json) {
            if (isEmpty(json)) {
                return new ArrayList<>();
            }

            try {
                return signerAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for signer", e);
            }
        }

        @TypeConverter
        public static String signerToString(List<Signer> list) {
            return signerAdapter.toJson(list);
        }

        @TypeConverter
        public static Map<String, String> stringToData(String json) {
            if (isEmpty(json)) {
                return new HashMap<>();
            }

            try {
                return dataAdapter.fromJson(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse json for data", e);
            }
        }

        @TypeConverter
        public static String dataToString(Map<String, String> data) {
            return dataAdapter.toJson(data);
        }

        private static boolean isEmpty(String json) {
            return json == null || json.isEmpty();
        }
    }
}
