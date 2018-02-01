package tech.duchess.luminawallet.presenter.account.send;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionSummary implements Parcelable {
    double selfMinimumBalance;
    double transactionFees;
    double sendAmount;
    String sendingAssetCode;
    final LinkedHashMap<String, Double> remainingBalances = new LinkedHashMap<>();
    boolean selfMinimumBalanceViolated;
    boolean isCreatingAccount;
    boolean createdAccountMinimumBalanceMet;
    double createdAccountMinimumBalance;
    String recipient;
    String memo;

    public TransactionSummary() {

    }

    public double getSelfMinimumBalance() {
        return selfMinimumBalance;
    }

    public double getTransactionFees() {
        return transactionFees;
    }

    public double getSendAmount() {
        return sendAmount;
    }

    public String getSendingAssetCode() {
        return sendingAssetCode;
    }

    public LinkedHashMap<String, Double> getRemainingBalances() {
        return remainingBalances;
    }

    public boolean isSelfMinimumBalanceViolated() {
        return selfMinimumBalanceViolated;
    }

    public boolean isCreatingAccount() {
        return isCreatingAccount;
    }

    public boolean isCreatedAccountMinimumBalanceMet() {
        return createdAccountMinimumBalanceMet;
    }

    public double getCreatedAccountMinimumBalance() {
        return createdAccountMinimumBalance;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMemo() {
        return memo;
    }

    protected TransactionSummary(Parcel in) {
        selfMinimumBalance = in.readDouble();
        transactionFees = in.readDouble();
        sendAmount = in.readDouble();
        sendingAssetCode = in.readString();
        selfMinimumBalanceViolated = in.readByte() != 0x00;
        isCreatingAccount = in.readByte() != 0x00;
        createdAccountMinimumBalanceMet = in.readByte() != 0x00;
        createdAccountMinimumBalance = in.readDouble();
        recipient = in.readString();
        memo = in.readString();

        for (int i = 0; i < in.readInt(); i++) {
            remainingBalances.put(in.readString(), in.readDouble());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(selfMinimumBalance);
        dest.writeDouble(transactionFees);
        dest.writeDouble(sendAmount);
        dest.writeString(sendingAssetCode);
        dest.writeByte((byte) (selfMinimumBalanceViolated ? 0x01 : 0x00));
        dest.writeByte((byte) (isCreatingAccount ? 0x01 : 0x00));
        dest.writeByte((byte) (createdAccountMinimumBalanceMet ? 0x01 : 0x00));
        dest.writeDouble(createdAccountMinimumBalance);
        dest.writeString(recipient);
        dest.writeString(memo);

        dest.writeInt(remainingBalances.size());
        for (Map.Entry<String, Double> entry : remainingBalances.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeDouble(entry.getValue());
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TransactionSummary> CREATOR = new Parcelable.Creator<TransactionSummary>() {
        @Override
        public TransactionSummary createFromParcel(Parcel in) {
            return new TransactionSummary(in);
        }

        @Override
        public TransactionSummary[] newArray(int size) {
            return new TransactionSummary[size];
        }
    };
}
