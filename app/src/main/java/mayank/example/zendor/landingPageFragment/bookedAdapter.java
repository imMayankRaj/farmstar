package mayank.example.zendor.landingPageFragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickBooked.onClickBookedCard;

/**
 * Created by mayank on 12/2/2017.
 */

public class bookedAdapter extends RecyclerView.Adapter<bookedAdapter.bookedViewHolder> {

    private Context mContext;
    private ArrayList<bookedClass> bookedList;

    public bookedAdapter(Context mContext, ArrayList bookedList){

        this.mContext = mContext;
        this.bookedList = bookedList;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView PID = v.findViewById(R.id.purchaseId);
            String pid = PID.getText().toString().substring(13);
            Intent intent = new Intent(mContext, onClickBookedCard.class);
            intent.putExtra("pid",pid.trim());
            mContext.startActivity(intent);
        }
    };


    @Override
    public bookedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.booked_card, parent, false);
        bookedViewHolder holder = new bookedViewHolder(view);
        view.setOnClickListener(onClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(bookedViewHolder holder, int position) {
        bookedClass current= bookedList.get(position);
        holder.commodityName.setText(current.getCommodity());
        holder.purchaseId.setText("Purchase Id : "+current.getPid());
        holder.sellerName.setText(current.getSellerName()+" : "+current.getZone());
        holder.est_weight.setText("Est. Weight: "+current.getEst_weight()+" kgs");
        holder.rate.setText("Rate: "+current.getRate()+"/kg");
        holder.bookedAt.setText(current.getBooked_ts());

        DecimalFormat formatter = new DecimalFormat("#.##");

        double rate = Double.parseDouble(current.getRate());
        double est_wt = Double.parseDouble(current.getEst_weight());
        double est_val = rate*est_wt;

        holder.est_Value.setText("Est. Value: "+formatter.format(est_val));
    }

    @Override
    public int getItemCount() {
        return bookedList.size();
    }

    public class bookedViewHolder extends RecyclerView.ViewHolder {
        TextView commodityName, purchaseId, sellerName, est_weight, rate, est_Value, bookedAt;
        public bookedViewHolder(View itemView) {
            super(itemView);
            commodityName = itemView.findViewById(R.id.commodityName);
            purchaseId = itemView.findViewById(R.id.purchaseId);
            sellerName = itemView.findViewById(R.id.sellerNameAndZone);
            est_weight = itemView.findViewById(R.id.est_weight);
            rate = itemView.findViewById(R.id.rate);
            est_Value = itemView.findViewById(R.id.estimatedValue);
            bookedAt = itemView.findViewById(R.id.bookedAt);
        }
    }
}
