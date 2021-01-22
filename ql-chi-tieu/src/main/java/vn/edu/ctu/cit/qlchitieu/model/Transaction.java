package vn.edu.ctu.cit.qlchitieu.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Transaction implements Parcelable {
    private String id;
    private double amount;
    private String name;
    private String description;
    private Date date;
    private String uid;
    private String type;

    public Transaction() {
    }

    public Transaction(double amount, String name, Date date, String type) {
        this.amount = amount;
        this.name = name;
        this.date = date;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.amount);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.uid);
        dest.writeString(this.type);
    }

    protected Transaction(Parcel in) {
        this.id = in.readString();
        this.amount = in.readDouble();
        this.name = in.readString();
        this.description = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.uid = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
