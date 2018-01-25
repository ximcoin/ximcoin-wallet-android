package tech.duchess.luminawallet.model.persistence.transaction;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.SparseArray;

import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.Link;
import tech.duchess.luminawallet.model.util.AssetUtil;

public class Operation {
    public enum OperationType {
        /**
         * Creates new account in Stellar Network. Fields to look for:
         * <p>
         * "account" - Account that is being created.
         * "funder" - Account that is funding the newly created account.
         * "starting_balance" - The balance that the account is initially being funded with. Will
         * always be in XLM.
         */
        CREATE_ACCOUNT(0, R.string.operation_account_created),
        /**
         * Sends a simple payment between two accounts in the Stellar Network. Fields to look for:
         * <p>
         * "to" - Account receiving funds.
         * "from" - Account sending funds.
         * "amount" - Amount being sent.
         * "asset_type"/"asset_code" - The type of asset being sent. Will not have an asset code if
         * asset type is native (XLM). Otherwise, check for asset code.
         */
        PAYMENT(1, R.string.operation_payment),
        /**
         * Sends a path payment between two accounts in the Stellar Network. Fields to look for:
         * <p>
         * TODO
         */
        PATH_PAYMENT(2, R.string.operation_path_payment),
        /**
         * Creates, updates, or deletes an offer in the Stellar Network. Fields to look for:
         * <p>
         * TODO
         */
        MANAGE_OFFER(3, R.string.operation_manage_offer),
        /**
         * Creates an offer, that won't consume a counter offer that exactly matches this offer.
         * Fields to look for:
         * <p>
         * TODO
         */
        CREATE_PASSIVE_OFFER(4, R.string.operation_create_passive_offer),
        /**
         * Sets account options (inflation destination, adding signers, etc.). Fields to look for:
         * <p>
         * TODO
         */
        SET_OPTIONS(5, R.string.operation_set_options),
        /**
         * Creates, updates, or deletes a trust line. Fields to look for:
         * <p>
         * TODO
         */
        CHANGE_TRUST(6, R.string.operation_change_trust),
        /**
         * Updates the authorized flag of an existing trust line; this is called by the issuer of
         * the related asset. Fields to look for:
         * <p>
         * TODO
         */
        ALLOW_TRUST(7, R.string.operation_allow_trust),
        /**
         * Deletes account and transfers remaining balance to destination account. Fields to look
         * for:
         * <p>
         * TODO
         */
        ACCOUNT_MERGE(8, R.string.operation_account_merge),
        /**
         * Runs inflation. Fields to look for:
         * <p>
         * TODO
         */
        INFLATION(9, R.string.operation_inflation),
        /**
         * Set, modify or delete a Data Entry (name/value pair) for an account. Fields to look for:
         * <p>
         * TODO
         */
        MANAGE_DATA(10, R.string.operation_manage_data),
        /**
         * A placeholder for unrecognized operation types.
         */
        UNKNOWN(-1, R.string.operation_unknown);

        private static final SparseArray<OperationType> VALUES;

        static {
            OperationType[] operationTypes = values();
            VALUES = new SparseArray<>(operationTypes.length);

            for (OperationType operationType : operationTypes) {
                VALUES.append(operationType.getType(), operationType);
            }
        }

        private final int type;

        @StringRes
        private final int friendlyName;

        OperationType(int type, @StringRes int friendlyName) {
            this.type = type;
            this.friendlyName = friendlyName;
        }

        public int getType() {
            return type;
        }

        @StringRes
        public int getFriendlyName() {
            return friendlyName;
        }

        @NonNull
        static OperationType findOperationType(int type) {
            OperationType operationType = VALUES.get(type);
            return operationType == null ? OperationType.UNKNOWN : operationType;
        }
    }

    private OperationLinks _links;
    private String id;
    private String paging_token;
    private String source_account;
    private String type;
    private int type_i;
    private String created_at;
    private String transaction_hash;
    private String asset_type;
    private String asset_code;
    private String asset_issuer;
    private double limit;
    private String trustee;
    private String trustor;
    private String from;
    private String to;
    private double amount;
    private double starting_balance;
    private String funder;
    private String account;
    private Transaction transaction;

    public OperationLinks get_links() {
        return _links;
    }

    public void set_links(OperationLinks _links) {
        this._links = _links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaging_token() {
        return paging_token;
    }

    public void setPaging_token(String paging_token) {
        this.paging_token = paging_token;
    }

    public String getSource_account() {
        return source_account;
    }

    public void setSource_account(String source_account) {
        this.source_account = source_account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getType_i() {
        return type_i;
    }

    public void setType_i(int type_i) {
        this.type_i = type_i;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTransaction_hash() {
        return transaction_hash;
    }

    public void setTransaction_hash(String transaction_hash) {
        this.transaction_hash = transaction_hash;
    }

    public String getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(String asset_type) {
        this.asset_type = asset_type;
    }

    public String getAsset_code() {
        return AssetUtil.getAssetCode(asset_type, asset_code);
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

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public String getTrustee() {
        return trustee;
    }

    public void setTrustee(String trustee) {
        this.trustee = trustee;
    }

    public String getTrustor() {
        return trustor;
    }

    public void setTrustor(String trustor) {
        this.trustor = trustor;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getStarting_balance() {
        return starting_balance;
    }

    public void setStarting_balance(double starting_balance) {
        this.starting_balance = starting_balance;
    }

    public String getFunder() {
        return funder;
    }

    public void setFunder(String funder) {
        this.funder = funder;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @NonNull
    public OperationType getOperationType() {
        return OperationType.findOperationType(getType_i());
    }

    public static class OperationLinks {
        @Nullable
        private Link self, transaction, effects, succeeds, precedes;

        @Nullable
        public Link getSelf() {
            return self;
        }

        public void setSelf(@Nullable Link self) {
            this.self = self;
        }

        @Nullable
        public Link getTransaction() {
            return transaction;
        }

        public void setTransaction(@Nullable Link transaction) {
            this.transaction = transaction;
        }

        @Nullable
        public Link getEffects() {
            return effects;
        }

        public void setEffects(@Nullable Link effects) {
            this.effects = effects;
        }

        @Nullable
        public Link getSucceeds() {
            return succeeds;
        }

        public void setSucceeds(@Nullable Link succeeds) {
            this.succeeds = succeeds;
        }

        @Nullable
        public Link getPrecedes() {
            return precedes;
        }

        public void setPrecedes(@Nullable Link precedes) {
            this.precedes = precedes;
        }
    }
}
