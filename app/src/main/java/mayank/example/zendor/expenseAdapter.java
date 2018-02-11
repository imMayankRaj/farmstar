package mayank.example.zendor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 1/8/2018.
 */

public class expenseAdapter extends ArrayAdapter<expenseClass> {

    private ArrayList arrayList;
    private Context context;

    public expenseAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
        this.arrayList = (ArrayList) objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.expense_layout, parent, false);
        }

        TextView details = convertView.findViewById(R.id.details);
        TextView amount = convertView.findViewById(R.id.amount);
        TextView rid = convertView.findViewById(R.id.rid);

        expenseClass current = (expenseClass) arrayList.get(position);
        amount.setText('\u20B9'+current.getAmount());
        details.setText(current.getDetails());
        rid.setText(current.getRid());

        return convertView;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Nullable
    @Override
    public expenseClass getItem(int position) {
        return (expenseClass) arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
