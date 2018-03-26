package com.ximcoin.ximwallet.presenter.inflation;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class InflationOperationSummary implements Parcelable {
    @NonNull
    private final String inflationDestination;

    private final double fees;

    public InflationOperationSummary(@NonNull String inflationDestination, double fees) {
        this.inflationDestination = inflationDestination;
        this.fees = fees;
    }

    public double getFees() {
        return fees;
    }

    @NonNull
    public String getInflationAddress() {
        return inflationDestination;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(fees);
        dest.writeString(inflationDestination);
    }

    protected InflationOperationSummary(Parcel in) {
        fees = in.readDouble();
        inflationDestination = in.readString();
    }

    public static final Creator<InflationOperationSummary> CREATOR = new Creator<InflationOperationSummary>() {
        @Override
        public InflationOperationSummary createFromParcel(Parcel in) {
            return new InflationOperationSummary(in);
        }

        @Override
        public InflationOperationSummary[] newArray(int size) {
            return new InflationOperationSummary[size];
        }
    };
}
