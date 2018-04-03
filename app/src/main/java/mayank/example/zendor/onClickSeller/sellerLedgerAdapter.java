package mayank.example.zendor.onClickSeller;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mayank.example.zendor.R;

/**
 * Created by mayank on 12/10/2017.
 */

public class sellerLedgerAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList arrayList;

    public sellerLedgerAdapter(@NonNull Context context, int resource, @NonNull ArrayList arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.ledger_view, parent, false);
        }
        sellerLedger.sellerledgerClass current = (sellerLedger.sellerledgerClass) arrayList.get(position);
        TextView date=view.findViewById(R.id.date);
        TextView tran = view.findViewById(R.id.tran);
        TextView dc = view.findViewById(R.id.dc);
        TextView balance = view.findViewById(R.id.bal);

        if(current.getStatus().equals("pro")) {
            balance.setTextColor(Color.parseColor("#d32f2f"));
            date.setTextColor(Color.parseColor("#d32f2f"));
            dc.setTextColor(Color.parseColor("#d32f2f"));
            tran.setTextColor(Color.parseColor("#d32f2f"));
        }
        else {
            balance.setTextColor(Color.parseColor("#000000"));
            date.setTextColor(Color.parseColor("#000000"));
            dc.setTextColor(Color.parseColor("#000000"));
            tran.setTextColor(Color.parseColor("#000000"));
        }
        date.setText(current.getDate());
        tran.setText(current.getTransaction());
        dc.setText(current.getStatus());
        balance.setText(current.getBalance());

        return view;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
