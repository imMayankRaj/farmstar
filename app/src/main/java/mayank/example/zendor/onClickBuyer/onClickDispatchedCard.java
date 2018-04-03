package mayank.example.zendor.onClickBuyer;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.frequentlyUsedClass;
import mayank.example.zendor.navigationDrawerOption.sale;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.onClickBuyer.buyerSale.getSaleDetail;

public class onClickDispatchedCard extends AppCompatActivity {


    private TextView buyerName;
    private TextView buyerId;
    private TextView commodity;
    private TextView saleId;
    private TextView weight;
    private TextView rate;
    private TextView totalAmount;
    private TextView deliveryAddress;
    private TextView billingAddress;
    private TextView dispatchedDate;
    private TextView cancel;
    private TextView delivered;
    private TextView toolbarSaleId;
    private Toolbar toolbar;
    private String sid;
    private SharedPreferences sharedPreferences;
    public static boolean check = false;
    private LoadingClass lc;
    private String buyer_id, roc;
    private TextView vehNum, driNum, dispBy;
    private ImageView callDriver, callDis;
    private String disNum;
    private String rrate, ba, dela, commo;
    private DatabaseReference mDatabase1;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_dispatched_card);

        buyerName = findViewById(R.id.buyerName);
        buyerId = findViewById(R.id.bid);
        commodity = findViewById(R.id.commodity);
        saleId = findViewById(R.id.saledId);
        weight = findViewById(R.id.weight);
        rate = findViewById(R.id.rate);
        totalAmount = findViewById(R.id.totalAmount);
        deliveryAddress = findViewById(R.id.deliveryAddress);
        billingAddress = findViewById(R.id.billingAddress);
        dispatchedDate = findViewById(R.id.ddate);
        cancel = findViewById(R.id.cancel);
        delivered = findViewById(R.id.delivered);
        toolbar = findViewById(R.id.toolbar);
        vehNum = findViewById(R.id.vnum);
        driNum = findViewById(R.id.driNum);
        dispBy = findViewById(R.id.disBy);
        callDriver = findViewById(R.id.callDriver);
        callDis = findViewById(R.id.callDispatch);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lc = new LoadingClass(this);

        toolbarSaleId = findViewById(R.id.tsid);

        // buyer_id = getIntent().getStringExtra("buyer_id");

        sid = getIntent().getStringExtra("sid");
        String flag = getIntent().getStringExtra("flag");

        toolbarSaleId.setText("SALE ID :" + sid);

        onClickDispatchedDetails();

        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryDialog();
            }
        });

        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        String pos = sharedPreferences.getString("position", "");

        if (!pos.equals("0")) {
            cancel.setVisibility(View.GONE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancellationDialog();
            }
        });

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(driNum.getText().toString());
            }
        });

        callDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(disNum);
            }
        });

       /* if(getIntent().getStringExtra("f").equals("0")){
            cancel.setVisibility(View.GONE);
            delivered.setVisibility(View.GONE);
        }*/

    }

    Intent intent;

    private void callDialog(final String a) {

        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91" + a));
        if (ActivityCompat.checkSelfPermission(onClickDispatchedCard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(onClickDispatchedCard.this, new String[]{Manifest.permission.CALL_PHONE},
                    2);
            return;
        } else
            startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
        }
    }

    private void cancellationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancellation_dialog_picked);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setCancelable(true);
        final EditText remark = dialog.findViewById(R.id.remark);
        TextView submit = dialog.findViewById(R.id.submit);
        ImageView back = dialog.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                roc = remark.getText().toString();
                if (roc.length() == 0) {
                    Toast.makeText(onClickDispatchedCard.this, "Please provide some reason for cancellation.", Toast.LENGTH_SHORT).show();
                } else
                    onClickCancelButton();
            }
        });
        dialog.show();
    }


    private void onClickDispatchedDetails() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_SALE_CARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("dispatched details", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject details = jsonObject.getJSONObject("values");
                    buyerName.setText("Buyer : " + details.getString("bname"));
                    saleId.setText("Sale Id : " + details.getString("saleid"));
                    commodity.setText(details.getString("commodities"));
                    weight.setText(details.getString("w1") + " kgs");
                    deliveryAddress.setText(details.getString("delivery_addr"));
                    billingAddress.setText(details.getString("billing"));
                    dispatchedDate.setText(details.getString("ts1"));
                    rate.setText(details.getString("rate") + "/kg");
                    buyerId.setText("Buyer Id :" + details.getString("buyer_id"));
                    buyer_id = details.getString("buyer_id");
                    double RATE = Double.parseDouble(details.getString("rate"));
                    double w1 = Double.parseDouble(details.getString("w1"));
                    totalAmount.setText((RATE * w1) + "");
                    vehNum.setText(details.getString("vnum"));
                    driNum.setText(details.getString("dnum"));
                    dispBy.setText(details.getString("disName"));

                    disNum = details.getString("disNum");

                    commo = details.getString("commodities");
                    rrate = details.getString("rate");
                    ba = details.getString("billing");
                    dela = details.getString("delivery_addr");

                    mDatabase1 = FirebaseDatabase.getInstance().getReference().child("buyerSales").child(buyer_id);


                } catch (JSONException e) {
                    Log.e("error", e + "");
                    lc.dismissDialog();
                }
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickDispatchedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDispatchedCard.this.getClass().getName(), onClickDispatchedCard.this);


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sale_id", sid);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }


    private void deliveryDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.on_click_delivery_button);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final EditText aa = dialog.findViewById(R.id.approvedAmount);
        final EditText remark = dialog.findViewById(R.id.remark);
        final EditText deliveredWe = dialog.findViewById(R.id.dw);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView submit = dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String approved = aa.getText().toString();
                String re = remark.getText().toString();
                String dew = deliveredWe.getText().toString();
                if (approved.length() != 0 && dew.length() != 0) {
                    onSubmitClick(approved, re, dew);
                    dialog.dismiss();
                } else
                    Toast.makeText(onClickDispatchedCard.this, "All Fields Are Compulsory.", Toast.LENGTH_SHORT).show();
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

    private void onSubmitClick(final String aa, final String remark, final String dw) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_DELIVERED_BUTTON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String ab[] = response.split(",");
                frequentlyUsedClass.sendOTPE(ab[1], "Delivered: " + commo + " " + dw + " kgs @ " + rrate + " per kg loaded from " + ba + " dated : " + ab[0] + " have been delivered successfully at " + dela + ".", onClickDispatchedCard.this);

                long time = System.currentTimeMillis();
                mDatabase1.setValue(time+"");

                if (getIntent().getStringExtra("f").equals("0")) {


                    Intent intent = new Intent(onClickDispatchedCard.this, sale.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    getSaleDetail(onClickDispatchedCard.this);
                    // buyerLedger.click.performClick();
                }


                check = true;
                lc.dismissDialog();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickDispatchedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDispatchedCard.this.getClass().getName(), onClickDispatchedCard.this);


                lc.dismissDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                String id = sharedPreferences.getString("id", "");


                Log.e("bid", buyer_id);
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sale_id", sid);
                parameters.put("approved_amt", aa);
                parameters.put("remark", remark);
                parameters.put("weight", dw);
                parameters.put("bid", buyer_id);
                parameters.put("bookedBy", id);

                parameters.put("ts2", dateTimeInGMT.format(new Date()));
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void onClickCancelButton() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_DELIVERED_SALE_CARD_CANCEL_BUTTON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                lc.dismissDialog();
                check = true;

                long time = System.currentTimeMillis();
                mDatabase1.setValue(time+"");

                if (getIntent().getStringExtra("f").equals("0")) {
                    Intent intent = new Intent(onClickDispatchedCard.this, sale.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else
                    getSaleDetail(onClickDispatchedCard.this);

                finish();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickDispatchedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDispatchedCard.this.getClass().getName(), onClickDispatchedCard.this);


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("sale_id", sid);
                parameters.put("canBy", id);
                parameters.put("roc", roc);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}
