package vn.edu.ctu.cit.qlchitieu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import vn.edu.ctu.cit.qlchitieu.R;
import vn.edu.ctu.cit.qlchitieu.model.User;

public class StatisticActivity extends AppCompatActivity {
    private PieChart pieChartYear;
    private BarChart barChartMonth;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        user=getIntent().getParcelableExtra("USER");

        barChartMonth=findViewById(R.id.bar_chart_month);
        barChartMonth.getDescription().setEnabled(false);
        barChartMonth.getLegend().setEnabled(false);
        barChartMonth.setFitBars(true); // make the x-axis fit exactly all bars
        barChartMonth.animateY(2000, Easing.EaseInSine);

        pieChartYear=findViewById(R.id.bar_chart_year);
        pieChartYear.getDescription().setEnabled(false);
        pieChartYear.animateY(2000, Easing.EaseInSine);
        pieChartYear.setDrawEntryLabels(false);

        final Calendar from=Calendar.getInstance();
        from.set(Calendar.MONTH,0);
        from.set(Calendar.DAY_OF_MONTH,1);
        from.set(Calendar.HOUR_OF_DAY,0);
        from.set(Calendar.MINUTE,0);
        from.set(Calendar.SECOND,0);

        Calendar to=Calendar.getInstance();
        //to.set(Calendar.DAY_OF_MONTH,to.getActualMaximum(Calendar.DAY_OF_MONTH));
        to.set(Calendar.MONTH,11);
        to.set(Calendar.DAY_OF_MONTH,31);
        to.set(Calendar.HOUR_OF_DAY,23);
        to.set(Calendar.MINUTE,59);
        to.set(Calendar.SECOND,59);

        db.collection("transactions")
                .whereGreaterThanOrEqualTo("date",from.getTime())
                .whereLessThanOrEqualTo("date",to.getTime())
                .whereEqualTo("userid",db.document("users/"+ user.getId()))
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<BarEntry> monthData=new ArrayList<>();
                        List<PieEntry> yearData=new ArrayList<>();
                        List<String> labels=new ArrayList<>();

                        for (DocumentSnapshot doc :queryDocumentSnapshots.getDocuments()) {
                            if (doc.getString("direction").equalsIgnoreCase("OUT")) {
                                Calendar cu = Calendar.getInstance();
                                Calendar c = Calendar.getInstance();
                                c.setTime(doc.getDate("date"));

                                // Month process
                                if (c.get(Calendar.MONTH) == cu.get(Calendar.MONTH)) {
                                    BarEntry barEntry = new BarEntry((float) c.get(Calendar.DAY_OF_MONTH), doc.getDouble("amount").floatValue());
                                    int pos = -1;
                                    for (int i = 0; i < monthData.size(); i++) {
                                        if (monthData.get(i).getX() == barEntry.getX()) {
                                            pos = i;
                                            break;
                                        }
                                    }
                                    if (pos >= 0) {
                                        monthData.get(pos).setY(monthData.get(pos).getY() + barEntry.getY());
                                    } else {
                                        monthData.add(barEntry);
                                    }
                                }

                                // Year process
                                PieEntry pieEntry=new PieEntry(doc.getDouble("amount").floatValue()
                                        ,"T"+String.format(Locale.getDefault(),"%02d",c.get(Calendar.MONTH)+1));
                                int pos=-1;
                                for (int i=0;i<yearData.size();i++) {
                                    if(yearData.get(i).getLabel().equalsIgnoreCase(pieEntry.getLabel())) {
                                        pos = i;
                                        break;
                                    }
                                }

                                if (pos >= 0) {
                                    yearData.get(pos).setY(yearData.get(pos).getY()+pieEntry.getY());
                                } else {
                                    yearData.add(pieEntry);
                                }
                            }
                        }

                        for (int i=0;i<31;i++) {
                            labels.add("N"+String.format(Locale.getDefault(),"%02d",i));
                        }

                        XAxis xAxis=barChartMonth.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
                        xAxis.setDrawLabels(true);
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

                        BarDataSet monthDataset = new BarDataSet(monthData, "Month data");
                        monthDataset.setColors(ColorTemplate.MATERIAL_COLORS);

                        BarData barData = new BarData(monthDataset);
                        barData.setBarWidth(0.9f); // set custom bar width

                        barChartMonth.setData(barData);
                        barChartMonth.invalidate(); // refresh

                        PieDataSet yearDataset = new PieDataSet(yearData, "Năm "+from.get(Calendar.YEAR));
                        yearDataset.setColors(ColorTemplate.MATERIAL_COLORS);

                        PieData pieData = new PieData(yearDataset);
                        pieChartYear.setData(pieData);
                        pieChartYear.invalidate(); // refresh
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(StatisticActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage("Lấy dữ liệu thống kê không thành công, vui lòng thử lại sau")
                                .setPositiveButton(R.string.ok,null)
                                .create()
                                .show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}