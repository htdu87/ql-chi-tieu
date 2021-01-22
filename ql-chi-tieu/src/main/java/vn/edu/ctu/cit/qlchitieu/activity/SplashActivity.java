package vn.edu.ctu.cit.qlchitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import vn.edu.ctu.cit.qlchitieu.LocalDatabase;
import vn.edu.ctu.cit.qlchitieu.model.User;

public class SplashActivity extends AppCompatActivity {
    private LocalDatabase lcdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lcdb=new LocalDatabase(this);
        List<User> users=lcdb.getListUser();

        if (users.size()>0) {
            Intent i=new Intent(this,MainActivity.class);
            i.putExtra("USER",users.get(0));
            startActivity(i);
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        lcdb.close();
        super.onDestroy();
    }
}