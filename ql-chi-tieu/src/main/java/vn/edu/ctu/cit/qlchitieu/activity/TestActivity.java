package vn.edu.ctu.cit.qlchitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import vn.edu.ctu.cit.qlchitieu.R;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toast.makeText(this,"Xin chào, tôi là Toast!!!",Toast.LENGTH_LONG).show();

        Button button=findViewById(R.id.btn_cancel);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // Code xử lý sự kiện khi người dùng bấm vào button
    }
}