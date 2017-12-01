package xendorp1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import mayank.example.zendor.R;
import xendorp1.cards.buyer_card;

/**
 * Created by GOTHAM on 02-11-2017.
 */

public class buyer_recycler_adapter extends RecyclerView.Adapter<buyer_recycler_adapter.MyViewHolder> {
    private Context mContext;
    private List<buyer_card> buyer_cardList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, buyer_id;
        public View view1;

        public MyViewHolder(View view) {
            super(view);
            view1 = view;
            name = view.findViewById(R.id.name);
            buyer_id=view.findViewById(R.id.buyer_id);
        }
    }
    public buyer_recycler_adapter(Context mContext, List<buyer_card> buyer_cardList) {
        this.mContext = mContext;
        this.buyer_cardList=buyer_cardList;
    }
    @Override
    public buyer_recycler_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyer_card, parent, false);

        return new buyer_recycler_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final buyer_recycler_adapter.MyViewHolder holder, int position) {
        buyer_card buyer_card =buyer_cardList.get(position);
        holder.name.setText(buyer_card.getName());
        holder.buyer_id.setText(buyer_card.getBuyer_id());
        /*holder.view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new zonal_manager();
                Bundle args = new Bundle();
                args.putString("id", zonal_manager_card.getId());
                args.putString("zname", zonal_manager_card.getZone_name());
                fragment.setArguments(args);
                FragmentTransaction transaction = fragment1.getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.drawer_layout, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return buyer_cardList.size();
    }
}
