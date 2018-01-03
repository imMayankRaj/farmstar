package mayank.example.zendor.onClickBuyer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private String sid;
    private SharedPreferences sharedPreferences;
    private ImageView back;
    public static boolean check = false;
    private LoadingClass lc;


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
        back = findViewById(R.id.back);

        lc = new LoadingClass(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbarSaleId = findViewById(R.id.tsid);


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

        String pos = sharedPreferences.getString("position","");

        if(!pos.equals("0")){
            cancel.setVisibility(View.GONE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancelButton();
            }
        });

        if(getIntent().getStringExtra("f").equals("0")){
            cancel.setVisibility(View.GONE);
            delivered.setVisibility(View.GONE);
        }

    }


    private void onClickDispatchedDetails(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_SALE_CARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("dispatched details", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject details = jsonObject.getJSONObject("values");
                    buyerName.setText("Buyer : "+details.getString("bname"));
                    saleId.setText("Sale Id : "+details.getString("saleid"));
                    commodity.setText(details.getString("commodities"));
                    weight.setText(details.getString("w1")+" kgs");
                    deliveryAddress.setText(details.getString("delivery_addr"));
                    billingAddress.setText(details.getString("billing"));
                    dispatchedDate.setText(details.getString("ts1"));
                    rate.setText(details.getString("rate")+"/kg");
                    buyerId.setText("Buyer Id :"+details.getString("buyer_id"));

                    double RATE = Double.parseDouble(details.getString("rate"));
                    double w1 = Double.parseDouble(details.getString("w1"));
                    totalAmount.setText((RATE*w1)+"");

                } catch (JSONException e) {
                    Log.e("error", e+"");
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
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sale_id", sid);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }


    private void deliveryDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.on_click_delivery_button);
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
                if(approved.length() !=0 && re.length() != 0 && dew.length() != 0) {
                    onSubmitClick(approved, re, dew);
                    dialog.dismiss();
                    frequentlyUsedClass.sendOTP(buyerDetails.num[0], "Your Foodmonk verification code is " + " Dispatched " + " . Happy food ordering :)", onClickDispatchedCard.this);
                }else
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

    private void onSubmitClick(final String aa, final String remark, final String dw){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_DELIVERED_BUTTON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                check = true;
                lc.dismissDialog();
                getSaleDetail(onClickDispatchedCard.this);
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
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));


                Map<String, String> parameters = new HashMap<>();
                parameters.put("sale_id", sid);
                parameters.put("approved_amt", aa);
                parameters.put("remark", remark);
                parameters.put("weight", dw);
                parameters.put("ts2", dateTimeInGMT.format(new Date()));
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void onClickCancelButton(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_DELIVERED_SALE_CARD_CANCEL_BUTTON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                lc.dismissDialog();
                check = true;
               getSaleDetail(onClickDispatchedCard.this);
                finish();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickDispatchedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDispatchedCard.this.getClass().getName(), onClickDispatchedCard.this);


            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sale_id", sid);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}
