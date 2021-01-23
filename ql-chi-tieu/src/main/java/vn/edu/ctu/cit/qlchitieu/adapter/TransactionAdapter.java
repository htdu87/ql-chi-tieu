package vn.edu.ctu.cit.qlchitieu.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.edu.ctu.cit.qlchitieu.R;
import vn.edu.ctu.cit.qlchitieu.activity.TransactionActivity;
import vn.edu.ctu.cit.qlchitieu.model.Transaction;
import vn.edu.ctu.cit.qlchitieu.model.User;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>
        implements EventListener<QuerySnapshot>, View.OnClickListener {
    private LayoutInflater inflater;
    private List<Transaction> data;
    private SimpleDateFormat dateFormat;
    private NumberFormat numberFormat;
    private Context context;
    private User user;
    private TransactionAdapterEvents events;

    public TransactionAdapter(Context context,User u, Query query) {
        query.addSnapshotListener(this);
        this.context=context;
        this.user=u;
        inflater=LayoutInflater.from(context);
        data=new ArrayList<>();
        dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        numberFormat=NumberFormat.getNumberInstance(Locale.getDefault());
    }

    public void setQuery(Query query) {
        query.addSnapshotListener(this);
        data.clear();
        notifyDataSetChanged();
    }

    public void setTransactionAdapterEventsListener(TransactionAdapterEvents e) {
        events=e;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if (error!=null) {
            Log.e("htdu87","FirebaseFirestoreException: "+error);
            Toast.makeText(context,"Lỗi lấy dữ liệu từ Firestore, vui lòng thử lại sau",Toast.LENGTH_SHORT).show();
            return;
        }

        for (DocumentChange doc:value.getDocumentChanges()) {
            //DocumentSnapshot snapshot=doc.getDocument();
            switch (doc.getType()) {
                case ADDED:
                    onAdd(doc);
                    break;
                case REMOVED:
                    onRem(doc);
                    break;
                case MODIFIED:
                    onMod(doc);
                    break;
            }
        }

        updateMoney();
    }

    @Override
    public void onClick(View v) {
        String id= (String) v.getTag();
        Intent i=new Intent(context, TransactionActivity.class);
        i.putExtra("TRANSACTION",getById(id));
        i.putExtra("USER",user);
        context.startActivity(i);
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(inflater.inflate(R.layout.item_transaction,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        Transaction trans=data.get(position);

        holder.itemView.setTag(trans.getId());
        holder.itemView.setOnClickListener(this);

        holder.lblTransName.setText(trans.getName());
        holder.lblTransDesc.setText(trans.getDescription());
        holder.lblTransDate.setText(dateFormat.format(trans.getDate()));
        holder.lblTransAmount.setText(numberFormat.format(trans.getAmount()));
        holder.lblTransAmount.setTextColor(trans.getType().equalsIgnoreCase("OUT")
                ?context.getResources().getColor(R.color.colorRed)
                :context.getResources().getColor(R.color.colorGreen));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private Transaction getById(String id) {
        for (Transaction tr:data) {
            if (tr.getId().equalsIgnoreCase(id))
                return tr;
        }
        return null;
    }

    private void updateMoney() {
        double in=0;
        double out=0;

        for (Transaction t:data) {
            if (t.getType().equalsIgnoreCase("OUT"))
                out+=t.getAmount();
            else
                in+=t.getAmount();
        }

        if (events!=null)
            events.onUpdateTrans(out,in);
    }

    void onMod(DocumentChange doc) {
        Log.d("htdu87","onMod");
        QueryDocumentSnapshot snapshot = doc.getDocument();
        if (doc.getNewIndex()==doc.getOldIndex()) {
            Transaction trans=data.get(doc.getNewIndex());
            trans.setAmount(snapshot.getDouble("amount"));
            trans.setDate(snapshot.getDate("date"));
            trans.setDescription(snapshot.getString("description"));
            trans.setId(snapshot.getId());
            trans.setName(snapshot.getString("name"));
            trans.setType(snapshot.getString("direction"));
            trans.setUid(snapshot.getDocumentReference("userid").getId());

            notifyItemChanged(doc.getNewIndex());
        } else {
            Transaction trans=new Transaction();
            trans.setAmount(snapshot.getDouble("amount"));
            trans.setDate(snapshot.getDate("date"));
            trans.setDescription(snapshot.getString("description"));
            trans.setId(snapshot.getId());
            trans.setName(snapshot.getString("name"));
            trans.setType(snapshot.getString("direction"));
            trans.setUid(snapshot.getDocumentReference("userid").getId());

            data.remove(doc.getOldIndex());
            data.add(doc.getNewIndex(),trans);
            notifyItemMoved(doc.getOldIndex(),doc.getNewIndex());
        }

    }

    void onRem(DocumentChange doc) {
        Log.d("htdu87","onRem");
        data.remove(doc.getOldIndex());
        notifyItemRemoved(doc.getOldIndex());
    }

    void onAdd(DocumentChange doc) {
        Log.d("htdu87","onAdd");
        QueryDocumentSnapshot snapshot = doc.getDocument();
        Transaction trans=new Transaction();
        trans.setAmount(snapshot.getDouble("amount"));
        trans.setDate(snapshot.getDate("date"));
        trans.setDescription(snapshot.getString("description"));
        trans.setId(snapshot.getId());
        trans.setName(snapshot.getString("name"));
        trans.setType(snapshot.getString("direction"));
        trans.setUid(snapshot.getDocumentReference("userid").getId());

        data.add(doc.getNewIndex(),trans);
        notifyItemInserted(doc.getNewIndex());
    }

    public interface TransactionAdapterEvents {
        void onUpdateTrans(double out, double in);
    }

    /**
     *
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView lblTransName;
        TextView lblTransDesc;
        TextView lblTransDate;
        TextView lblTransAmount;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            lblTransName=itemView.findViewById(R.id.lbl_trans_name);
            lblTransDesc=itemView.findViewById(R.id.lbl_trans_desc);
            lblTransDate=itemView.findViewById(R.id.lbl_trans_date);
            lblTransAmount=itemView.findViewById(R.id.lbl_trans_amount);
        }
    }
}
