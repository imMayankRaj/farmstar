package mayank.example.zendor.navigationDrawerOption;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mayank.example.zendor.R;

/**
 * Created by mayank on 12/8/2017.
 */

public class commodityListAdapter extends RecyclerView.Adapter<commodityListAdapter.commodityHolder> {

    private Context mContext;
    private ArrayList<onClickCommodityList.commodityClass> arrayList;
    private String commodity;

    public commodityListAdapter(Context mContext, ArrayList arrayList, String commodity){
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.commodity = commodity;
    }

    @Override
    public commodityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.commodity_total, parent, false);
        commodityHolder holder = new commodityHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(commodityHolder holder, int position) {
        onClickCommodityList.commodityClass current = arrayList.get(position);
        holder.cname.setText(commodity);
        holder.zname.setText(current.getZname());
        holder.bookedWeight.setText(current.getBookedWeight()+" kgs");
        holder.pickedWeight.setText(current.getPickedWeight()+" kgs");
        holder.collectedWeight.setText(current.getCollectedWeight()+" kgs");
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class commodityHolder extends RecyclerView.ViewHolder {

        private TextView cname, zname, collectedWeight, pickedWeight, bookedWeight;
        public commodityHolder(View itemView) {
            super(itemView);
            cname = itemView.findViewById(R.id.cname);
            zname = itemView.findViewById(R.id.zname);
            collectedWeight = itemView.findViewById(R.id.collectedWeight);
            pickedWeight = itemView.findViewById(R.id.pickedWeight);
            bookedWeight = itemView.findViewById(R.id.bookedWeight);
        }
    }
}
