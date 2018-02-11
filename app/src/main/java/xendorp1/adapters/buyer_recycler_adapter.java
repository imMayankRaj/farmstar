package xendorp1.adapters;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mayank.example.zendor.R;
import mayank.example.zendor.onClickBuyer.onClickBuyerCard;
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
        private TextView call;
        private TextView due_amount_volume;

        public MyViewHolder(View view) {
            super(view);
            view1 = view;
            name = view.findViewById(R.id.name);
            buyer_id=view.findViewById(R.id.buyer_id);
            call = view.findViewById(R.id.call);
            due_amount_volume = view.findViewById(R.id.due_amount_volume);
        }
    }
    public buyer_recycler_adapter(Context mContext, List<buyer_card> buyer_cardList) {
        this.mContext = mContext;
        this.buyer_cardList=buyer_cardList;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView id = v.findViewById(R.id.buyer_id);
            String b_id = id.getText().toString();
            Intent intent = new Intent(mContext, onClickBuyerCard.class);
            intent.putExtra("buyer_id", b_id);
            mContext.startActivity(intent);

        }
    };

    @Override
    public buyer_recycler_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyer_card, parent, false);

        itemView.setOnClickListener(onClickListener);
        return new buyer_recycler_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final buyer_recycler_adapter.MyViewHolder holder, int position) {
        final buyer_card buyer_card =buyer_cardList.get(position);
        holder.name.setText(buyer_card.getName());
        holder.buyer_id.setText(buyer_card.getBuyer_id());
        holder.due_amount_volume.setText('\u20B9'+buyer_card.getCb());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num[] = buyer_card.getNumber().split(",");
                callDialog(num);
            }
        });

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

    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

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
