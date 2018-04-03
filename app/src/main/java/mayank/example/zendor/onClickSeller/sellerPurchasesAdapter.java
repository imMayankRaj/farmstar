package mayank.example.zendor.onClickSeller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.navigationDrawerOption.onClickCancelledCard;
import mayank.example.zendor.navigationDrawerOption.onClickCollectedCard;
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import mayank.example.zendor.onClickPicked.onClickPickedCard;

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
        return new purchaseHolder(view);
    }

    @Override
    public void onBindViewHolder(purchaseHolder holder, int position) {

        final sellerPurchaseClass current = list.get(position);
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
        holder.ppid.setText(current.getPid());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("pid", current.getPid());
                switch (current.getFlag()){
                    case "bk":
                        Intent intent = new Intent(mContext, onClickBookedCard.class);
                        intent.putExtra("pid",current.getPid());
                        mContext.startActivity(intent);
                        break;

                    case "pk":
                        Intent intent1 = new Intent(mContext, onClickPickedCard.class);
                        intent1.putExtra("pid",current.getPid());
                        mContext.startActivity(intent1);
                        break;

                    case "cn":
                        Intent intent2 = new Intent(mContext, onClickCancelledCard.class);
                        bundle.putString("extra", "1");
                        intent2.putExtra("extras",bundle);
                        mContext.startActivity(intent2);
                        break;

                    case "co":
                        Intent intent3 = new Intent(mContext, onClickCollectedCard.class);
                        bundle.putString("extra", "1");
                        intent3.putExtra("extras",bundle);
                        mContext.startActivity(intent3);
                        break;

                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class purchaseHolder extends RecyclerView.ViewHolder {
        TextView commodity, weight, rate, amount, name, ts, ts_text, weightText, status_text, pid, ppid;
        View view;
        public purchaseHolder(View itemView) {
            super(itemView);
            view = itemView;
            commodity = itemView.findViewById(R.id.cname);
            weight = itemView.findViewById(R.id.weight);
            rate = itemView.findViewById(R.id.rate);
            amount = itemView.findViewById(R.id.totalValue);
            name = itemView.findViewById(R.id.name);
            ts = itemView.findViewById(R.id.ts);
            weightText = itemView.findViewById(R.id.weightText);
            status_text = itemView.findViewById(R.id.statusText);
            ts_text = itemView.findViewById(R.id.status_ts);
            pid = itemView.findViewById(R.id.pid);
            ppid = itemView.findViewById(R.id.ppid);
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
