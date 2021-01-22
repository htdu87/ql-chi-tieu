package vn.edu.ctu.cit.qlchitieu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import vn.edu.ctu.cit.qlchitieu.model.User;

public class LocalDatabase extends SQLiteOpenHelper {
    private final static String DB_NAME="ql_chi_tieu";
    private final static int DB_VERSION=1;

    public LocalDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="create table USERS(" +
                "iduser text not null," +
                "address text," +
                "email text," +
                "fullname text not null," +
                "password text," +
                "sex integer," +
                "tel text," +
                "username text not null," +
                "primary key(iduser))";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<User> getListUser() {
        List<User> users=new ArrayList<>();
        SQLiteDatabase reader=getReadableDatabase();
        Cursor cursor=reader.rawQuery("select * from USERS",null);
        if (cursor.moveToNext()) {
            do {
                User user=new User();
                user.setId(cursor.getString(0));
                user.setAddress(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setFullName(cursor.getString(3));
                user.setPassword(cursor.getString(4));
                user.setSex(cursor.getInt(5));
                user.setTel(cursor.getString(6));
                user.setUserName(cursor.getString(7));

                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        reader.close();
        return users;
    }

    public boolean addUser(User user) {
        SQLiteDatabase writer=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("iduser",user.getId());
        cv.put("address",user.getAddress());
        cv.put("email",user.getEmail()  );
        cv.put("fullname",user.getFullName());
        cv.put("password",user.getPassword());
        cv.put("sex",user.getSex());
        cv.put("tel",user.getTel());
        cv.put("username",user.getUserName());

        long id=writer.insert("USERS",null,cv);
        writer.close();
        return id!=-1;
    }

    public void removeAllUser() {
        SQLiteDatabase writer=getWritableDatabase();
        writer.delete("USERS",null,null);
        writer.close();
    }
}
