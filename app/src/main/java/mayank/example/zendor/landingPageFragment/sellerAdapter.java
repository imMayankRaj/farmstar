package mayank.example.zendor.landingPageFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mayank.example.zendor.R;

/**
 * Created by mayan on 10/31/2017.
 */

public class sellerAdapter extends RecyclerView.Adapter<sellerAdapter.sellerHolder> {

    Context context;
    ArrayList<sellerClass> sellerList;

    public sellerAdapter(Context context, ArrayList sellerList){
        this.context = context;
        this.sellerList = sellerList;
    }

    @Override
    public sellerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seller_cardview, parent, false);
        sellerHolder holder = new sellerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(sellerHolder holder, int position) {

        sellerClass current = sellerList.get(position);
        holder.name.setText(current.getName());
        holder.seller_id.setText("Seller Id: " +current.getSellerId());
        holder.address.setText(current.getAddress());
        holder.last_purchase.setText(current.getLast_purchase());
    }

    @Override
    public int getItemCount() {
        return sellerList.size();
    }

    public class sellerHolder extends RecyclerView.ViewHolder {
        private TextView name, address, seller_id, last_purchase;
        public sellerHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            seller_id = itemView.findViewById(R.id.sellerId);
            last_purchase = itemView.findViewById(R.id.last_purchase);
        }
    }
}
