package mayank.example.zendor.navigationDrawerOption;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;

import mayank.example.zendor.R;

/**
 * Created by mayank on 12/7/2017.
 */

public class allPurchaseAdapter extends RecyclerView.Adapter<allPurchaseAdapter.purchaseHolder> {

    private Context mContext;
    private ArrayList<purchaseClass> arrayList;

    public allPurchaseAdapter(Context mContext, ArrayList arrayList){
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView ck = v.findViewById(R.id.check);
            TextView p = v.findViewById(R.id.pos);
            int pos = Integer.parseInt(p.getText().toString());
            String chk = ck.getText().toString();
            switch (chk){
                case "0":
                    setDialogCancel(pos);
                    break;
                case "1":
                    setDialogCollect(pos);
                    break;
            }

        }
    };

    @Override
    public purchaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_purchase_card_layout,parent,false);
        view.setOnClickListener(onClickListener);
        purchaseHolder holder = new purchaseHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(purchaseHolder holder, int position) {
        purchaseClass current = arrayList.get(position);
        holder.cname.setText(current.getCommodities());
        holder.sname.setText(current.getSellerName());
        holder.pid.setText(current.getPurchase_id());
        String status = null;
        if(current.getFlag().equals("cn")){
            status = "Cancelled";
            holder.check.setText("0");
            holder.pos.setText(position+"");
            holder.status.setTextColor(Color.parseColor("#d32f2f"));
        }else if(current.getFlag().equals("co")) {
            status = "Collected";
            holder.check.setText("1");
            holder.pos.setText(position+"");
            holder.status.setTextColor(Color.parseColor("#000000"));

        }
        holder.status.setText(status);
        holder.rate.setText(current.getRate().concat("/kg"));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class purchaseHolder extends RecyclerView.ViewHolder {
        private TextView cname, sname, status, rate, check, pos, pid;
        public purchaseHolder(View itemView) {
            super(itemView);
            cname = itemView.findViewById(R.id.cname);
            sname = itemView.findViewById(R.id.sname);
            status = itemView.findViewById(R.id.status);
            rate = itemView.findViewById(R.id.rate);
            check = itemView.findViewById(R.id.check);
            pos = itemView.findViewById(R.id.pos);
            pid = itemView.findViewById(R.id.pid);
        }
    }

    private void setDialogCancel(int pos){
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.on_click_cancel_dialog);

        TextView commodity = dialog.findViewById(R.id.cname);
        TextView sname = dialog.findViewById(R.id.sname);
        TextView pickedAt = dialog.findViewById(R.id.pickedAt);
        TextView pickedBy = dialog.findViewById(R.id.pickedBy);
        TextView cancelledAt = dialog.findViewById(R.id.cancelledAt);
        TextView cancelledBy = dialog.findViewById(R.id.cancelledBy);
        TextView bookedBy = dialog.findViewById(R.id.bookedBy);
        TextView bookedAt = dialog.findViewById(R.id.bookedAt);
        TextView rate = dialog.findViewById(R.id.rate);
        TextView roc = dialog.findViewById(R.id.roc);

        purchaseClass current = arrayList.get(pos);
        commodity.setText(current.getCommodities());
        sname.setText(current.getSellerName());
        pickedAt.setText(current.getPicked());
        pickedBy.setText(current.getPicker());
        cancelledAt.setText(current.getCancelled_ts());
        cancelledBy.setText(current.getCancelled_by());
        bookedAt.setText(current.getBooked_ts());
        bookedBy.setText(current.getBooker());
        rate.setText(current.getRate()+"/kg");
        String remark = (current.getRoc_b() == null || current.getRoc_b().length() == 0) ? current.getRoc_p() : current.getRoc_b();
        roc.setText(remark);

        dialog.show();

    }

    private void setDialogCollect(int pos){
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.on_click_collect_dialog);
        TextView commodity = dialog.findViewById(R.id.cname);
        TextView sname = dialog.findViewById(R.id.sname);
        TextView pickedAt = dialog.findViewById(R.id.pickedAt);
        TextView pickedBy = dialog.findViewById(R.id.pickedBy);
        TextView collectedAt = dialog.findViewById(R.id.collectedAt);
        TextView collectedBy = dialog.findViewById(R.id.collectedBy);
        TextView rate = dialog.findViewById(R.id.rate);


        purchaseClass current = arrayList.get(pos);
        commodity.setText(current.getCommodities());
        sname.setText(current.getSellerName());
        pickedAt.setText(current.getPicked());
        pickedBy.setText(current.getPicker());
        collectedAt.setText(current.getCollected_ts());
        collectedBy.setText(current.getCollected_by());
        rate.setText(current.getRate()+"/kg");


        dialog.show();
    }

}
