package mayank.example.zendor.onClickSeller;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mayank.example.zendor.R;

/**
 * Created by mayank on 12/6/2017.
 */

public class sellerPurchasesAdapter extends RecyclerView.Adapter<sellerPurchasesAdapter.purchaseHolder> {

    private Context mContext;
    private ArrayList<sellerPurchaseClass> list;

    public sellerPurchasesAdapter(Context mContext, ArrayList list){
        this.list = list;
        this.mContext = mContext;
    }


    @Override
    public purchaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.booked_card_data, parent, false);
        purchaseHolder holder = new purchaseHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(purchaseHolder holder, int position) {

        sellerPurchaseClass current = list.get(position);
        switch (current.getFlag()){
            case "bk":
                holder.weightText.setText("Est. Weight");
                holder.status_text.setText("Booked By");
                holder.ts_text.setText("Booked At");
                holder.ts_text.setTextColor(Color.parseColor("#ffee58"));
                setCard(holder, current);
                break;

            case "pk":
                holder.weightText.setText("Actual Weight");
                holder.status_text.setText("Picked By");
                holder.ts_text.setText("Picked At");
                holder.ts_text.setTextColor(Color.parseColor("#ffca28"));
                setCard(holder, current);
                break;

            case "cn":
                holder.weightText.setText("Reason Of Cancellation");
                holder.status_text.setText("Cancelled By");
                holder.ts_text.setText("Cancelled At");
                holder.ts_text.setTextColor(Color.parseColor("#ac0800"));
                setCard(holder, current);
                break;

            case "co":
                holder.weightText.setText("Collected Weight");
                holder.status_text.setText("Collected By");
                holder.ts_text.setText("Collected At");
                holder.ts_text.setTextColor(Color.parseColor("#7cb342"));
                setCard(holder, current);
                break;
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class purchaseHolder extends RecyclerView.ViewHolder {
        TextView commodity, weight, rate, amount, name, ts, ts_text, weightText, status_text;
        public purchaseHolder(View itemView) {
            super(itemView);
            commodity = itemView.findViewById(R.id.cname);
            weight = itemView.findViewById(R.id.weight);
            rate = itemView.findViewById(R.id.rate);
            amount = itemView.findViewById(R.id.totalValue);
            name = itemView.findViewById(R.id.name);
            ts = itemView.findViewById(R.id.ts);
            weightText = itemView.findViewById(R.id.weightText);
            status_text = itemView.findViewById(R.id.statusText);
            ts_text = itemView.findViewById(R.id.status_ts);
        }
    }

    private void setCard(purchaseHolder holder, sellerPurchaseClass current){
        holder.commodity.setText(current.getCommodity());
        holder.weight.setText(current.getWeight()+" kgs");
        holder.rate.setText(current.getRate()+" /kg");

        DecimalFormat formatter = new DecimalFormat("#.##");

        if(!current.getFlag().equals("cn")) {
            double r = Double.parseDouble(current.getRate());
            double w = Double.parseDouble(current.getWeight());
            holder.amount.setText(formatter.format(r*w));
        }
        holder.name.setText(current.getName());
        holder.ts.setText(current.getTimestamp());
    }

}
