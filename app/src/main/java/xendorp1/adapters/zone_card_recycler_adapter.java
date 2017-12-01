package xendorp1.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.List;

import mayank.example.zendor.R;
import xendorp1.cards.zone_card;
import xendorp1.fragments.zonal_managers;

/**
 * Created by GOTHAM on 02-11-2017.
 */

public class zone_card_recycler_adapter extends RecyclerView.Adapter<zone_card_recycler_adapter.MyViewHolder> {
    private Context mContext;
    private List<zone_card> zoneList;
    private Fragment fragment1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView zone_name,zone_id;
        public View view1;
        public MyViewHolder(View view) {
            super(view);
            view1=view;
            zone_name = view.findViewById(R.id.zone_name);
            zone_id = view.findViewById(R.id.zone_id);
        }
    }
    public zone_card_recycler_adapter(Context mContext, List<zone_card> zoneList,Fragment fragment1) {
        this.mContext = mContext;
        this.zoneList = zoneList;
        this.fragment1=fragment1;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final zone_card zone_card = zoneList.get(position);
        holder.zone_name.setText(zone_card.getZone_name());
        holder.zone_id.setText(zone_card.getZone_id());
        holder.view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new zonal_managers();
                Bundle args=new Bundle();
                args.putString("zid",zone_card.getZone_id());
                fragment.setArguments(args);
                FragmentTransaction transaction=fragment1.getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.drawer_layout,fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return zoneList.size();
    }

}
