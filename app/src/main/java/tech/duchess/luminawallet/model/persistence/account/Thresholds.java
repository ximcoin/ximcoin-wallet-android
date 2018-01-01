package tech.duchess.luminawallet.model.persistence.account;

public class Thresholds {
    private int low_threshold;
    private int med_threshold;
    private int high_threshold;

    public int getLow_threshold() {
        return low_threshold;
    }

    public void setLow_threshold(int low_threshold) {
        this.low_threshold = low_threshold;
    }

    public int getMed_threshold() {
        return med_threshold;
    }

    public void setMed_threshold(int med_threshold) {
        this.med_threshold = med_threshold;
    }

    public int getHigh_threshold() {
        return high_threshold;
    }

    public void setHigh_threshold(int high_threshold) {
        this.high_threshold = high_threshold;
    }
}
