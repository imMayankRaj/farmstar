package xendorp1.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayank.example.zendor.R;
import xendorp1.cards.zonal_manager_card;
import xendorp1.fragments.zonal_manager;

/**
 * Created by GOTHAM on 02-11-2017.
 */

public class zonal_manager_recycler_adapter extends RecyclerView.Adapter<zonal_manager_recycler_adapter.MyViewHolder> {
    private Context mContext;
    private List<zonal_manager_card> zonalManagerCardList;
    private Fragment fragment1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, username_info, password_info, zone_info, curbal_info;
        public ImageView active;
        public View view1;
        public Button call;

        public MyViewHolder(View view) {
            super(view);
            view1 = view;
            name = view.findViewById(R.id.name);
            username_info = view.findViewById(R.id.username_info);
            password_info = view.findViewById(R.id.password_info);
            zone_info = view.findViewById(R.id.zone_info);
            curbal_info = view.findViewById(R.id.curbal_info);
            active = view.findViewById(R.id.active);
            call = view.findViewById(R.id.call);
        }
    }

    public zonal_manager_recycler_adapter(Context mContext, List<zonal_manager_card> zonal_manager_cardList, Fragment fragment1) {
        this.mContext = mContext;
        this.zonalManagerCardList = zonal_manager_cardList;
        this.fragment1 = fragment1;
    }

    @Override
    public zonal_manager_recycler_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zonal_manager_card, parent, false);

        return new zonal_manager_recycler_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final zonal_manager_recycler_adapter.MyViewHolder holder, int position) {
        final zonal_manager_card zonal_manager_card = zonalManagerCardList.get(position);
        holder.name.setText(zonal_manager_card.getName());
        holder.password_info.setText(zonal_manager_card.getPassword());
        holder.zone_info.setText(zonal_manager_card.getZone_name());
        holder.curbal_info.setText('\u20B9'+zonal_manager_card.getCb());
        int status = zonal_manager_card.getStatus();
        if (status == 1) {
            holder.active.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.green));
            holder.username_info.setText(zonal_manager_card.getUsername());
        } else {
            holder.active.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.red));
            String username = zonal_manager_card.getUsername();
            if (username.lastIndexOf("|") != -1)
                holder.username_info.setText(username.substring(0, username.lastIndexOf("|")));
            else
                holder.username_info.setText(username);
        }

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String num[] = zonal_manager_card.getNumber().split(",");
                callDialog(num);

            }
        });

        holder.view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new zonal_manager();
                Bundle args = new Bundle();
                args.putString("id", zonal_manager_card.getId());
                args.putString("zid", zonal_manager_card.getZid());
                args.putString("zname", zonal_manager_card.getZone_name());
                args.putInt("status", zonal_manager_card.getStatus());
                fragment.setArguments(args);
                FragmentTransaction transaction = fragment1.getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.drawer_layout, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return zonalManagerCardList.size();
    }


    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

        Set<String> s = new HashSet<>();
        s.addAll(numberList);
        numberList.clear();
        numberList.addAll(s);

        final String[] b = numberList.toArray(new String[numberList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Call :")
                .setItems(b, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String number = b[which];
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91" + number));
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }else
                            mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();

    }

}
