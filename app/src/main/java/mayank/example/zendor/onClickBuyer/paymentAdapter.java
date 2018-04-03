package mayank.example.zendor.onClickBuyer;

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

import mayank.example.zendor.R;

/**
 * Created by mayank on 2/11/2018.
 */

public class paymentAdapter extends ArrayAdapter<OnClickPaymentRequestCard.paymentDetails> {

    private Context context;
    private ArrayList objects;

    public paymentAdapter(@NonNull Context context, @NonNull ArrayList<OnClickPaymentRequestCard.paymentDetails> objects) {
        super(context, 0, objects);
        this.context = context;
        this.objects = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.payment_layout, parent, false);
        }

        OnClickPaymentRequestCard.paymentDetails current = (OnClickPaymentRequestCard.paymentDetails) objects.get(position);

        TextView date = convertView.findViewById(R.id.date);
        TextView amount = convertView.findViewById(R.id.amount);
        TextView cb = convertView.findViewById(R.id.paidBy);


        date.setText(current.getPaidOn());
        amount.setText(current.getPaid());
        cb.setText(current.getPaidBy());

        return convertView;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public OnClickPaymentRequestCard.paymentDetails getItem(int position) {
        return (OnClickPaymentRequestCard.paymentDetails) objects.get(position);
    }


}
