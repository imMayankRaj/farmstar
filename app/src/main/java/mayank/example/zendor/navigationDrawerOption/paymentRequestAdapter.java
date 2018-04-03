package mayank.example.zendor.navigationDrawerOption;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.frequentlyUsedClass;
import mayank.example.zendor.landingPageFragment.booked;
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import xendorp1.application_classes.AppController;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.e;
import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.frequentlyUsedClass.notifyUser;

/**
 * Created by mayank on 12/11/2017.
 */

public class paymentRequestAdapter extends RecyclerView.Adapter<paymentRequestAdapter.paymentRequestHolder> {


    private Context mContext;
    private ArrayList arrayList;
    private SharedPreferences sharedPreferences;
    private LoadingClass lc;
    private DatabaseReference mDatabase;

    public paymentRequestAdapter(Context mContext, ArrayList arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getItemViewType(int position) {
        int i = 0;
        paymentRequest.requestClass current = (paymentRequest.requestClass) arrayList.get(position);
        switch (current.getFlag()) {
            case "r":
                i = 0;
                break;
            case "a":
                i = 1;
                break;
            case "j":
                i = 2;
                break;
        }
        return i;
    }

    @Override
    public paymentRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.pending_card, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.processed_card, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(mContext).inflate(R.layout.rejected_card, parent, false);
                break;
        }
        return new paymentRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(paymentRequestHolder holder, int position) {

        final paymentRequest.requestClass current = (paymentRequest.requestClass) arrayList.get(position);
        switch (current.getFlag()) {
            case "r":

                holder.requestedId.setText(current.getPid());
                holder.requestedBy.setText(current.getRequestedBy());
                holder.requestedAt.setText(current.getDate());

                final String id;
                if (current.getSflag().equals("0")) {
                    id = current.getSeller_id();
                    holder.seller_name_view.setVisibility(View.GONE);
                } else {
                    id = current.getSid();
                    holder.seller_name.setText(current.getSeller_name());
                }

                holder.amount.setText(current.getAmount());
                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickRejectDialog(current.getPid(), id);
                    }
                });

                boolean b = false;
                if (holder.seller_name_view.getVisibility() == View.VISIBLE) {
                    b = true;
                }
                final boolean finalB = b;
                holder.proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickProceedDialog(current.getPid(), id, current.getAmount(), finalB);

                    }
                });

                break;
            case "a":


                if (current.getSflag().equals("0")) {
                    holder.seller_name_view.setVisibility(View.GONE);
                } else
                    holder.seller_name.setText(current.getSeller_name());

                holder.requestedId.setText(current.getPid());
                holder.processedBy.setText(current.getProcessedBy());
                holder.processedAt.setText(current.getProcessedAt());
                holder.seller_name.setText(current.getSeller_name());
                holder.amount.setText(current.getAmount());

                break;
            case "j":

                holder.requestedId.setText(current.getPid());
                holder.remark.setText(current.getRemark());
                holder.rejectedAt.setText(current.getRejDate());
                holder.rejectedBy.setText(current.getRejectedBy());

                break;
        }

    }


    @Override
    public int getItemCount() {
        if (arrayList == null)
            return 0;
        else
            return arrayList.size();
    }

    public class paymentRequestHolder extends RecyclerView.ViewHolder {

        TextView requestedId, requestedBy, seller_name, amount, requestedAt, reject, proceed, spid, seller_id;
        TextView processedBy, processedAt;
        TextView rejectedBy, rejectedAt, remark;
        LinearLayout seller_name_view;


        public paymentRequestHolder(View itemView) {
            super(itemView);

            spid = itemView.findViewById(R.id.spid);
            requestedId = itemView.findViewById(R.id.ri);
            requestedBy = itemView.findViewById(R.id.rb);
            seller_name = itemView.findViewById(R.id.sname);
            amount = itemView.findViewById(R.id.amount);
            requestedAt = itemView.findViewById(R.id.ra);
            reject = itemView.findViewById(R.id.reject);
            proceed = itemView.findViewById(R.id.proceed);
            processedBy = itemView.findViewById(R.id.pb);
            processedAt = itemView.findViewById(R.id.pa);
            rejectedBy = itemView.findViewById(R.id.reb);
            rejectedAt = itemView.findViewById(R.id.rea);
            remark = itemView.findViewById(R.id.remark);
            seller_name_view = itemView.findViewById(R.id.seller_name_view);
        }
    }

    private void onClickRejectDialog(final String spid, final String seller_id) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pr_reject_dialog);

        final EditText remarks = dialog.findViewById(R.id.remark);
        TextView reject = dialog.findViewById(R.id.reject);
        TextView cancel = dialog.findViewById(R.id.cancel);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String re = remarks.getText().toString();
                if (re.length() == 0) {
                    Toast.makeText(mContext, "Please provide a reason.", Toast.LENGTH_SHORT).show();
                } else {
                    rejectRequest(spid, re, seller_id);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onClickProceedDialog(final String spid, final String seller_id, final String amount, final boolean b) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pr_proceed_dialog);

        final EditText remarks = dialog.findViewById(R.id.remark);
        final EditText rn = dialog.findViewById(R.id.rn);
        TextView reject = dialog.findViewById(R.id.submit);
        TextView cancel = dialog.findViewById(R.id.cancel);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String re = remarks.getText().toString();
                String RN = rn.getText().toString();
                if (RN.length() == 0) {
                    Toast.makeText(mContext, "Reference Number is compulsory.", Toast.LENGTH_SHORT).show();
                } else {
                    proceedRequest(spid, re, RN, seller_id, amount, b);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void rejectRequest(final String spid, final String rem, final String seller_id) {

        lc = new LoadingClass(mContext);
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PAYMENT_REQUEST_REJECT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                notifyUser("Payment Request Cancelled", "Requested ID : "+ spid, mContext, "5", seller_id);
                Long time = System.currentTimeMillis();
              //  mDatabase.child("users").child(response).child("Ledger").setValue(time + "");
                mDatabase.child("paymentRequests").setValue(time+"");
                lc.dismissDialog();
                paymentRequest.check.performClick();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(mContext, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, booked.class.getName(), (Activity) mContext);

                lc.dismissDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                sharedPreferences = mContext.getSharedPreferences("details", MODE_PRIVATE);

                String id = sharedPreferences.getString("id", "");

                Map<String, String> params = new HashMap<>();
                params.put("sid", spid);
                params.put("des", rem);
                params.put("rid", id);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void proceedRequest(final String spid, final String rem, final String ref, final String seller_id, final String amount, final boolean b) {

        lc = new LoadingClass(mContext);
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PAYMENT_REQUEST_PROCEED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String number = jsonObject.getString("number");
                    String accountNumber = jsonObject.getString("acc");
                    String pref;
                    String pos = sharedPreferences.getString("position", "");

                    if(!pos.equals("0")){
                        pref = "Cash";
                    }else
                        pref = accountNumber;

                    frequentlyUsedClass.sendOTP(number.split(",")[0], "प्रिय किसान भाई, ₹"+amount+" आपके खाता "+pref+" में जमा कर दिया गया है, Ref : "+ref+". फ़ार्मस्टार के साथ व्यापार के लिये धन्यवाद.", mContext);

                    Long time = System.currentTimeMillis();
                   // mDatabase.child("users").child(seller_id).child("Ledger").setValue(time + "");
                    mDatabase.child("paymentRequests").setValue(time+"");
                    notifyUser("Payment Request Processed", "Requested ID : "+ spid, mContext, "5", seller_id);


                } catch (JSONException e) {
                   Log.e("error", e+"");
                }
                lc.dismissDialog();
                paymentRequest.check.performClick();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(mContext, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, booked.class.getName(), (Activity) mContext);

                lc.dismissDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                sharedPreferences = mContext.getSharedPreferences("details", MODE_PRIVATE);

                String id = sharedPreferences.getString("id", "");


                Map<String, String> params = new HashMap<>();
                params.put("spid", spid);
                params.put("des", rem);
                params.put("rid", id);
                params.put("req", ref);
                params.put("sid", seller_id);
                params.put("amount", amount);
                params.put("status", b + "");
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

}
