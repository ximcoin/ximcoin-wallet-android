package tech.duchess.luminawallet.model;

public class Account {
    private String id;
    private String account_id;
    private long sequence;
    private long subentry_count;

    public String toString() {
        return "id: " + id + " account_id: " + account_id + " sequence: " + sequence +
                " subentry_count: " + subentry_count;
    }
}
