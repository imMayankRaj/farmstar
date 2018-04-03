package mayank.example.zendor.onClickBuyer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mayank.example.zendor.R;

/**
 * Created by mayank on 12/9/2017.
 */

public class saleAdapter extends RecyclerView.Adapter<saleAdapter.saleHolder> {


    private Context mContext;
    private ArrayList<buyerSale.saleClass> arrayList;
    private int f;


    public saleAdapter(Context mContext, ArrayList arrayList, int f) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.f = f;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView sid = v.findViewById(R.id.sid);
            TextView flag = v.findViewById(R.id.flag);
            String SID = sid.getText().toString();
            String FLAG = flag.getText().toString();

            switch (FLAG) {

                case "di":
                    Intent intent = new Intent(mContext, onClickDispatchedCard.class);
                    intent.putExtra("sid", SID.substring(9));
                    intent.putExtra("flag", FLAG);
                    intent.putExtra("f", f + "");
                    mContext.startActivity(intent);
                    break;

                case "de":
                    Intent intent1 = new Intent(mContext, onClickDeliveredCard.class);
                    intent1.putExtra("sid", SID.substring(9));
                    intent1.putExtra("flag", FLAG);
                    intent1.putExtra("f", f + "");
                    mContext.startActivity(intent1);
                    break;

                case "ps":
                    Intent intent3 = new Intent(mContext, OnClickPaymentRequestCard.class);
                    intent3.putExtra("sid", SID.substring(9));
                    intent3.putExtra("flag", FLAG);
                    intent3.putExtra("f", f + "");
                    mContext.startActivity(intent3);
                    break;

                case "cn":
                    Intent intent4 = new Intent(mContext, onClickCancelledCard.class);
                    intent4.putExtra("sid", SID.substring(9));
                    intent4.putExtra("flag", FLAG);
                    intent4.putExtra("f", f + "");
                    mContext.startActivity(intent4);
                    break;

            }


        }
    };

    @Override
    public saleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sale_card, parent, false);
        view.setOnClickListener(onClickListener);
        saleHolder holder = new saleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(saleHolder holder, int position) {
        buyerSale.saleClass current = arrayList.get(position);
        holder.bname.setText(current.getBname());
        holder.sid.setText("Sale Id : " + current.getSid());
        holder.cname.setText(current.getCname());
        holder.weight.setText(current.getWeight() + " kgs");
        holder.rate.setText('\u20B9' + current.getRate() + "/kg");
        holder.ts.setText(current.getTs());
        String flag = current.getFlag();
        holder.flag.setText(flag);
        holder.ts.setText(current.getTs());
        holder.delWeight.setText(current.getDelWeight() + " kgs");

        double rate = Double.parseDouble(current.getRate());


        switch (flag) {
            case "di":
                double weight1 = Double.parseDouble(current.getWeight());
                holder.delWeight.setVisibility(View.GONE);
                holder.ll.setVisibility(View.GONE);
                holder.saleFlag.setText("Dispatched");
                holder.totalAmount.setText('\u20B9' + "" + (rate * weight1));
                break;

            case "de":
                double weight2 = Double.parseDouble(current.getDelWeight());
                holder.saleFlag.setText("Delivered");
                holder.totalAmount.setText('\u20B9' + current.getAa());
                break;

            case "ps":
                double weight3 = Double.parseDouble(current.getDelWeight());
                holder.totalAmount.setText('\u20B9' + current.getAa());
                holder.saleFlag.setText("Payment Success");
                break;

            case "cn":

                if (current.getDelWeight().length() == 0) {
                    holder.ll.setVisibility(View.GONE);
                    double weight4 = Double.parseDouble(current.getWeight());
                    holder.totalAmount.setText('\u20B9' + (weight4 * rate) + "");
                } else {
                    holder.ll.setVisibility(View.VISIBLE);

                    holder.totalAmount.setText('\u20B9' + current.getAa());
                }
                holder.saleFlag.setText("Cancelled");
                break;
        }


    }

    @Override
    public int getItemCount() {
        Log.e("size", arrayList.size() + "");
        return arrayList.size();
    }

    public class saleHolder extends RecyclerView.ViewHolder {
        private TextView bname, sid, cname, weight, rate, saleFlag, ts, totalAmount, flag, delWeight;
        private LinearLayout ll;

        public saleHolder(View itemView) {
            super(itemView);

            ll = itemView.findViewById(R.id.ll);
            delWeight = itemView.findViewById(R.id.deweight);
            bname = itemView.findViewById(R.id.bname);
            sid = itemView.findViewById(R.id.sid);
            cname = itemView.findViewById(R.id.cname);
            weight = itemView.findViewById(R.id.weight);
            rate = itemView.findViewById(R.id.rate);
            saleFlag = itemView.findViewById(R.id.saleFlag);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            ts = itemView.findViewById(R.id.ts);
            flag = itemView.findViewById(R.id.flag);
        }
    }
}
