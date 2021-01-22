package vn.edu.ctu.cit.qlchitieu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import vn.edu.ctu.cit.qlchitieu.R;
import vn.edu.ctu.cit.qlchitieu.Utility;

public class RegisterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private TextInputEditText txtFullName;
    private TextInputEditText txtAddress;
    private TextInputEditText txtTel;
    private TextInputEditText txtEmail;
    private TextInputEditText txtUserName;

    private int gender=1;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db=FirebaseFirestore.getInstance();

        txtFullName=findViewById(R.id.txt_full_name);
        txtAddress=findViewById(R.id.txt_address);
        txtTel=findViewById(R.id.txt_tel);
        txtEmail=findViewById(R.id.txt_email);
        txtUserName=findViewById(R.id.txt_username);

        RadioButton radMale = findViewById(R.id.rad_male);
        radMale.setOnCheckedChangeListener(this);
        RadioButton radFemale = findViewById(R.id.rad_female);
        radFemale.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            gender=buttonView.getId()==R.id.rad_male?1:0;
        }
    }

    public void onRegister(View v) {
        if (checkUsrInfo()) {
            final String fullName=txtFullName.getText()==null ?"":txtFullName.getText().toString();
            final String address=txtAddress.getText()==null ?"":txtAddress.getText().toString();
            final String tel=txtTel.getText()==null ?"":txtTel.getText().toString();
            final String email=txtEmail.getText()==null ?"":txtEmail.getText().toString();
            final String userName=txtUserName.getText()==null ?"":txtUserName.getText().toString();

            // Check username exist?
            db.collection("users")
                    .whereEqualTo("username",userName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult()!=null && task.getResult().size()>0) {
                                    new AlertDialog.Builder(RegisterActivity.this)
                                            .setTitle(R.string.app_name)
                                            .setMessage("Tên đăng nhập đã tồn tại, vui lòng chọn tên đăng nhập khác!")
                                            .setPositiveButton(R.string.ok,null)
                                            .create()
                                            .show();
                                } else {
                                    addUser(fullName,address,tel,email,userName,gender);
                                }
                            } else {
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle(R.string.app_name)
                                        .setMessage("Đã có lỗi xãy ra, vui lòng thử lại sau!")
                                        .setPositiveButton(R.string.ok,null)
                                        .create()
                                        .show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Đã có lỗi xãy ra, vui lòng thử lại sau!")
                                    .setPositiveButton(R.string.ok,null)
                                    .create()
                                    .show();
                        }
                    });
        }
    }

    public void onCancel(View v) {
        onBackPressed();
    }

    private void addUser(String fullName, String address, String tel, String email, String userName, int sex) {
        Map<String,Object> user=new HashMap<>();
        user.put("fullname",fullName);
        user.put("lock",false);
        user.put("password", Utility.MD5Encrypt(userName+"123a@"));
        user.put("sex",sex);
        user.put("username",userName);
        user.put("address",address);
        user.put("tel",tel);
        user.put("email",email);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("Đăng ký tài khoản thành công")
                                .setMessage("Bây giờ bạn có thể đăng nhập với mật khẩu 123a@")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create()
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage("Đăng ký không thành công, vui lòng thử lại sau!")
                                .setPositiveButton(R.string.ok,null)
                                .create()
                                .show();
                    }
                });
    }

    private boolean checkUsrInfo() {
        if (txtFullName.getText()==null || TextUtils.isEmpty(txtFullName.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Vui lòng nhập họ tên của bạn!")
                    .setPositiveButton(R.string.ok,null)
                    .create()
                    .show();
            return false;
        }
        if (txtUserName.getText()==null || TextUtils.isEmpty(txtUserName.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Vui lòng nhập tên đăng nhập cho tài khoản của bạn!")
                    .setPositiveButton(R.string.ok,null)
                    .create()
                    .show();
            return false;
        }
        return true;
    }
}