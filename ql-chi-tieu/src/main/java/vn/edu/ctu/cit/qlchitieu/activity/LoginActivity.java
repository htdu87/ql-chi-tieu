package vn.edu.ctu.cit.qlchitieu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import vn.edu.ctu.cit.qlchitieu.LocalDatabase;
import vn.edu.ctu.cit.qlchitieu.R;
import vn.edu.ctu.cit.qlchitieu.Utility;
import vn.edu.ctu.cit.qlchitieu.model.User;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText txtUsername;
    private TextInputEditText txtPassword;
    private LocalDatabase sqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sqlite=new LocalDatabase(this);

        txtUsername=findViewById(R.id.txt_username);
        txtPassword=findViewById(R.id.txt_password);
    }

    @Override
    protected void onDestroy() {
        sqlite.close();
        super.onDestroy();
    }

    public void onLogin(View v) {
        String usr=txtUsername.getText()==null?"":txtUsername.getText().toString();
        String pwd=txtPassword.getText()==null?"":txtPassword.getText().toString();

        if (TextUtils.isEmpty(usr)||TextUtils.isEmpty(pwd)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!")
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        } else {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("username",usr)
                    .whereEqualTo("password", Utility.MD5Encrypt(usr+pwd))
                    .whereEqualTo("lock",false)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult()!=null && task.getResult().size()>0) {
                                    // Save user to local database
                                    DocumentSnapshot doc=task.getResult().getDocuments().get(0);
                                    User user=new User();
                                    user.setAddress(doc.getString("address"));
                                    user.setEmail(doc.getString("email"));
                                    user.setFullName(doc.getString("fullname"));
                                    user.setPassword(doc.getString("password"));
                                    user.setSex(doc.getDouble("sex").intValue());
                                    user.setTel(doc.getString("tel"));
                                    user.setUserName(doc.getString("username"));
                                    user.setId(doc.getId());

                                    sqlite.addUser(user);

                                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                    i.putExtra("USER",user);
                                    startActivity(i);
                                    finish();
                                } else {
                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle(R.string.app_name)
                                            .setMessage("Tên đăng nhập và mật khẩu không đúng, vui lòng kiểm tra lại!")
                                            .setPositiveButton(R.string.ok, null)
                                            .create()
                                            .show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Đã có lỗi xãy ra, vui lòng thử lại sau!")
                                    .setPositiveButton(R.string.ok, null)
                                    .create()
                                    .show();
                        }
                    });
        }
    }

    public void onRegister(View v) {
        startActivity(new Intent(this,RegisterActivity.class));
    }
}