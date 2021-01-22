package vn.edu.ctu.cit.qlchitieu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.ctu.cit.qlchitieu.R;
import vn.edu.ctu.cit.qlchitieu.model.Transaction;
import vn.edu.ctu.cit.qlchitieu.model.User;

public class TransactionActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private TextInputEditText txtTransName;
    private TextInputEditText txtTransAmount;
    private TextInputEditText txtTransDate;
    private RadioButton radTransOut;
    private RadioButton radTransIn;
    private TextInputEditText txtTransDesc;

    private User user;
    private Transaction transaction;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat;
    private String direction="OUT";

    private NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db=FirebaseFirestore.getInstance();
        dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        numberFormat=NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(0);

        user=getIntent().getParcelableExtra("USER");
        transaction=getIntent().getParcelableExtra("TRANSACTION");

        txtTransName=findViewById(R.id.txt_trans_name);
        txtTransAmount=findViewById(R.id.txt_trans_amount);
        txtTransDate=findViewById(R.id.txt_trans_date);
        txtTransDesc=findViewById(R.id.txt_trans_desc);

        radTransIn=findViewById(R.id.rad_trans_in);
        radTransIn.setOnCheckedChangeListener(this);
        radTransOut=findViewById(R.id.rad_trans_out);
        radTransOut.setOnCheckedChangeListener(this);

        if (transaction!=null) {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            db.collection("transactions")
                    .document(transaction.getId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot!=null) {
                                txtTransName.setText(documentSnapshot.getString("name"));
                                txtTransAmount.setText(numberFormat.format(documentSnapshot.getDouble("amount")));
                                txtTransDate.setText(dateFormat.format(documentSnapshot.getDate("date")));
                                txtTransDesc.setText(documentSnapshot.getString("description"));

                                if(documentSnapshot.getString("direction").equalsIgnoreCase("OUT")) {
                                    direction="OUT";
                                    radTransOut.setChecked(true);
                                } else {
                                    direction="IN";
                                    radTransIn.setChecked(true);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new AlertDialog.Builder(TransactionActivity.this)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Lấy giao dịch không thành công, vui lòng thử lại sau")
                                    .setPositiveButton(R.string.ok,null)
                                    .create()
                                    .show();
                        }
                    });
        } else {
            txtTransDate.setText(dateFormat.format(new Date()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transaction,menu);
        if (transaction!=null)
            menu.findItem(R.id.act_del).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int mid=item.getItemId();
        if (mid==android.R.id.home) {
            onBackPressed();
            return true;
        } else if (mid==R.id.act_save) {
            try {
                saveTransaction();
            } catch (ParseException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage("Ngày giao dịch không đúng định dạng dd/MM/yyy HH:mm")
                        .setPositiveButton(R.string.ok,null)
                        .create()
                        .show();
            }
            return true;
        } else if(mid==R.id.act_del) {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa giao dịch")
                    .setMessage("Bạn chắc muốn xóa giao dịch?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTransaction();
                        }
                    })
                    .setNegativeButton(R.string.no,null)
                    .create()
                    .show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            direction=buttonView.getId()==R.id.rad_trans_in?"IN":"OUT";
        }
    }

    private void deleteTransaction() {
        db.collection("transactions")
                .document(transaction.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TransactionActivity.this, "Xóa giao dịch thành công!", Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(TransactionActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage("Xóa giao dịch không thành công, vui lòng thử lại sau!")
                                .setPositiveButton(R.string.ok, null)
                                .create()
                                .show();
                    }
                });
    }

    private void saveTransaction() throws ParseException {
        if (chkInput()) {
            Map<String,Object> trans=new HashMap<>();
            trans.put("amount",Double.parseDouble(txtTransAmount.getText().toString()));
            trans.put("name",txtTransName.getText().toString());
            trans.put("description",txtTransDesc.getText().toString());
            trans.put("direction",direction);
            trans.put("date",dateFormat.parse(txtTransDate.getText().toString()));
            trans.put("userid",db.document("users/"+ user.getId()));
            if (transaction==null) {
                db.collection("transactions")
                        .add(trans)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("htdu87", "Add doc success with id: " + documentReference.getId());
                                Toast.makeText(TransactionActivity.this, "Lưu giao dịch thành công!", Toast.LENGTH_SHORT)
                                        .show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new AlertDialog.Builder(TransactionActivity.this)
                                        .setTitle(R.string.app_name)
                                        .setMessage("Lưu giao dịch không thành công, vui lòng thử lại sau!")
                                        .setPositiveButton(R.string.ok, null)
                                        .create()
                                        .show();
                            }
                        });
            } else {
                db.collection("transactions")
                        .document(transaction.getId())
                        .update(trans)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("htdu87", "Update doc success ");
                                Toast.makeText(TransactionActivity.this, "Lưu giao dịch thành công!", Toast.LENGTH_SHORT)
                                        .show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new AlertDialog.Builder(TransactionActivity.this)
                                        .setTitle(R.string.app_name)
                                        .setMessage("Lưu giao dịch không thành công, vui lòng thử lại sau!")
                                        .setPositiveButton(R.string.ok, null)
                                        .create()
                                        .show();
                            }
                        });
            }
        }
    }

    private boolean chkInput() {
        if (txtTransName.getText()==null || TextUtils.isEmpty(txtTransName.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Vui lòng nhập tên giao dịch!")
                    .setPositiveButton(R.string.ok,null)
                    .create()
                    .show();
            return false;
        }
        if (txtTransAmount.getText()==null || TextUtils.isEmpty(txtTransAmount.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Vui lòng nhập số tiền giao dịch!")
                    .setPositiveButton(R.string.ok,null)
                    .create()
                    .show();
            return false;
        }
        if (txtTransDate.getText()==null || TextUtils.isEmpty(txtTransDate.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("Vui lòng nhập số tiền giao dịch!")
                    .setPositiveButton(R.string.ok,null)
                    .create()
                    .show();
            return false;
        }
        return true;
    }
}