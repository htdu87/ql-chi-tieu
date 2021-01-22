package vn.edu.ctu.cit.qlchitieu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.ctu.cit.qlchitieu.LocalDatabase;
import vn.edu.ctu.cit.qlchitieu.R;
import vn.edu.ctu.cit.qlchitieu.adapter.TransactionAdapter;
import vn.edu.ctu.cit.qlchitieu.model.User;

public class MainActivity extends AppCompatActivity implements OnSelectDateListener {
    private LocalDatabase lcdb;
    private User user;
    private FirebaseFirestore db;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user=getIntent().getParcelableExtra("USER");
        Calendar from=Calendar.getInstance();
        from.set(Calendar.DAY_OF_MONTH,1);
        from.set(Calendar.HOUR_OF_DAY,0);
        from.set(Calendar.MINUTE,0);
        from.set(Calendar.SECOND,0);

        Calendar to=Calendar.getInstance();
        to.set(Calendar.DAY_OF_MONTH,to.getActualMaximum(Calendar.DAY_OF_MONTH));
        to.set(Calendar.HOUR_OF_DAY,23);
        to.set(Calendar.MINUTE,59);
        to.set(Calendar.SECOND,59);

        db=FirebaseFirestore.getInstance();
        Query query = db.collection("transactions")
                .whereGreaterThanOrEqualTo("date",from.getTime())
                .whereLessThanOrEqualTo("date",to.getTime())
                .whereEqualTo("userid",db.document("users/"+ user.getId()))
                .orderBy("date",Query.Direction.DESCENDING);

        adapter=new TransactionAdapter(this,user,query);
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int mid=item.getItemId();
        if (mid==R.id.act_logout) {
            lcdb=new LocalDatabase(this);
            lcdb.removeAllUser();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
            return true;
        } else if (mid==R.id.act_add) {
            Intent i=new Intent(this,TransactionActivity.class);
            i.putExtra("USER",user);
            startActivity(i);
            return true;
        } else if (mid==R.id.act_statistic) {
            Intent i=new Intent(this,StatisticActivity.class);
            i.putExtra("USER",user);
            startActivity(i);
            return true;
        } else if(mid==R.id.act_choice_date) {
            DatePickerBuilder builder = new DatePickerBuilder(this,this)
                    .date(Calendar.getInstance())
                    .pickerType(CalendarView.RANGE_PICKER)
                    .date(Calendar.getInstance())
                    .headerColor(R.color.colorPrimary)
                    .selectionColor(R.color.colorAccent)
                    .todayLabelColor(R.color.colorAccent);

            DatePicker picker=builder.build();
            picker.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (lcdb!=null)
            lcdb.close();
        super.onDestroy();
    }

    @Override
    public void onSelect(List<Calendar> calendar) {
        Calendar from=Calendar.getInstance();
        Calendar to=Calendar.getInstance();

        if (calendar.size()==1){
            from.setTime(calendar.get(0).getTime());
            from.set(Calendar.HOUR_OF_DAY,0);
            from.set(Calendar.MINUTE,0);
            from.set(Calendar.SECOND,0);

            to.setTime(calendar.get(0).getTime());
            to.set(Calendar.HOUR_OF_DAY,23);
            to.set(Calendar.MINUTE,59);
            to.set(Calendar.SECOND,59);
        } else {
            from.setTime(calendar.get(0).getTime());
            from.set(Calendar.HOUR_OF_DAY,0);
            from.set(Calendar.MINUTE,0);
            from.set(Calendar.SECOND,0);

            to.setTime(calendar.get(calendar.size()-1).getTime());
            to.set(Calendar.HOUR_OF_DAY,23);
            to.set(Calendar.MINUTE,59);
            to.set(Calendar.SECOND,59);
        }

        Query query = db.collection("transactions")
                .whereGreaterThanOrEqualTo("date",from.getTime())
                .whereLessThanOrEqualTo("date",to.getTime())
                .orderBy("date",Query.Direction.DESCENDING);
        adapter.setQuery(query);
    }
}