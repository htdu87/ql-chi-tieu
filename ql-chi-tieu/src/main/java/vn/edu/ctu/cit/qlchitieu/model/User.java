package vn.edu.ctu.cit.qlchitieu.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String id;
    private String address;
    private String email;
    private String fullName;
    private String password;
    private int sex;
    private String tel;
    private String userName;

    public User() {
    }

    public User(String id, String fullName, String password, int sex, String userName) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.sex = sex;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", sex=" + sex +
                ", tel='" + tel + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.address);
        dest.writeString(this.email);
        dest.writeString(this.fullName);
        dest.writeString(this.password);
        dest.writeInt(this.sex);
        dest.writeString(this.tel);
        dest.writeString(this.userName);
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.address = in.readString();
        this.email = in.readString();
        this.fullName = in.readString();
        this.password = in.readString();
        this.sex = in.readInt();
        this.tel = in.readString();
        this.userName = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
