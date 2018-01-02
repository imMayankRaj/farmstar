package mayank.example.zendor.landingPageFragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickPicked.onClickPickedCard;

/**
 * Created by mayank on 12/2/2017.
 */

public class pickedAdapter extends RecyclerView.Adapter<pickedAdapter.pickedViewHolder>{

    private ArrayList<pickedClass> pickedList;
    private Context mContext;

    public pickedAdapter(Context mContext, ArrayList pickedList){
        this.pickedList = pickedList;
        this.mContext = mContext;

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView PID = v.findViewById(R.id.purchaseId);
            String pid = PID.getText().toString().substring(13);
            Intent intent = new Intent(mContext, onClickPickedCard.class);
            intent.putExtra("pid",pid);
            mContext.startActivity(intent);
        }
    };

    @Override
    public pickedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.picked_layout, parent, false);
        pickedViewHolder holder = new pickedViewHolder(view);
        view.setOnClickListener(onClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(pickedViewHolder holder, int position) {
        pickedClass current = pickedList.get(position);
        holder.cname.setText(current.getCommodity());
        holder.pid.setText("Purchase Id : "+ current.getPid());
        holder.sellerNameAndZone.setText(current.getSellerName().concat(" : ").concat(current.getZone()));
        holder.weight.setText("Actual Weight : "+current.getWeight().concat(" kgs"));
        holder.pickedAt.setText(current.getPicked_ts());
        holder.rate.setText("Rate :"+current.getRate()+"/kg");

        DecimalFormat formatter = new DecimalFormat("#.##");

        double rate = Double.parseDouble(current.getRate());
        double weight = Double.parseDouble(current.getWeight());
        double value = rate*weight;

        holder.totalValue.setText("Value : "+formatter.format(value));
    }

    @Override
    public int getItemCount() {
        return pickedList.size();
    }

    public class pickedViewHolder extends RecyclerView.ViewHolder {
        TextView cname, pid, sellerNameAndZone, weight, rate, totalValue, pickedAt;
        public pickedViewHolder(View itemView) {
            super(itemView);
            cname = itemView.findViewById(R.id.commodityName);
            pid = itemView.findViewById(R.id.purchaseId);
            sellerNameAndZone = itemView.findViewById(R.id.sellerNameAndZone);
            weight = itemView.findViewById(R.id.weight);
            rate = itemView.findViewById(R.id.rate);
            totalValue = itemView.findViewById(R.id.value);
            pickedAt = itemView.findViewById(R.id.pickedAt);
        }
    }
}
