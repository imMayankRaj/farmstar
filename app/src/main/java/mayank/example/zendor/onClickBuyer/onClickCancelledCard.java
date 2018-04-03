package mayank.example.zendor.onClickBuyer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class onClickCancelledCard extends AppCompatActivity {

    private TextView buyername;
    private TextView bid;
    private TextView commodity;
    private TextView sid;
    private TextView weight;
    private TextView rate;
    private TextView amountDue;
    private TextView totalAmount;
    private TextView approvedAmount;
    private TextView deliveryAddress;
    private TextView billingAddress;
    private TextView dispatchedDate;
    private TextView deliveredDate;
    private String saleid;
    private TextView tsid;
    private String buyer_id;
    private SharedPreferences sharedPreferences;
    private TextView toolbarSaleId;
    private LoadingClass lc;
    private Toolbar toolbar;
    private TextView delWeight;
    private String amountDueToBuyer;
    private ListView splitPayment;
    private ArrayList<OnClickPaymentRequestCard.paymentDetails> arrayList;
    private String roc;
    private TextView vehNum, driNum, dispBy, delBy, canBy, canAt, rocc, ad;
    private LinearLayout dell, delll, aall, aaaa, dw;
    private ImageView callDriver, callDel, callDis, callCan;
    private String delNum, disNum, canNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_cancelled_card2);

        buyername = findViewById(R.id.buyerName);
        bid = findViewById(R.id.bid);
        commodity = findViewById(R.id.commodity);
        sid = findViewById(R.id.saledId);
        weight = findViewById(R.id.weight);
        rate = findViewById(R.id.rate);
        amountDue = findViewById(R.id.amountDue);
        totalAmount = findViewById(R.id.totalAmount);
        approvedAmount = findViewById(R.id.approvedAmount);
        deliveredDate = findViewById(R.id.dedate);
        dispatchedDate = findViewById(R.id.ddate);
        deliveryAddress = findViewById(R.id.deliveryAddress);
        billingAddress = findViewById(R.id.billingAddress);
        delWeight = findViewById(R.id.deweight);
        toolbar = findViewById(R.id.toolbar);
        splitPayment = findViewById(R.id.splitPayment);
        vehNum = findViewById(R.id.vnum);
        driNum = findViewById(R.id.driNum);
        dispBy = findViewById(R.id.disBy);
        delBy = findViewById(R.id.delBy);
        canBy = findViewById(R.id.canBy);
        canAt = findViewById(R.id.canAt);
        rocc = findViewById(R.id.roc);
        dell = findViewById(R.id.delL);
        delll = findViewById(R.id.delll);
        aall = findViewById(R.id.llaa);
        aaaa = findViewById(R.id.aaaa);
        dw = findViewById(R.id.dw);
        ad = findViewById(R.id.ad);
        callDriver = findViewById(R.id.callDriver);
        callCan = findViewById(R.id.callCancel);
        callDel = findViewById(R.id.callDel);
        callDis = findViewById(R.id.callDispatch);

        arrayList = new ArrayList<>();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lc = new LoadingClass(this);


        saleid = getIntent().getStringExtra("sid");

        //  amountDue.setText(buyerLedger.buyercb+"");
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        toolbarSaleId = findViewById(R.id.tsid);
        toolbarSaleId.setText("SALE ID :" + saleid);

        getOnClickDeliveredCardDetails();
        getPaymentData();


        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(driNum.getText().toString());
            }
        });

        callDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(delNum);
            }
        });

        callCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(canNum);
            }
        });

        callDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(disNum);
            }
        });

        String pos = sharedPreferences.getString("position", "");



       /* if (getIntent().getStringExtra("f").equals("0")) {
            cancel.setVisibility(View.GONE);
            collectPayment.setVisibility(View.GONE);
        }*/
    }

    Intent intent;
    private void callDialog(final String a) {

        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91"+a));
        if (ActivityCompat.checkSelfPermission(onClickCancelledCard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(onClickCancelledCard.this, new String[]{Manifest.permission.CALL_PHONE},
                    2);
            return;
        }else
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

    private void getPaymentData(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_PAYMENT_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayList.clear();
                try {
                    Log.e("resp", response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("Details");
                    for(int i =0;i<array.length();i++){
                        JSONObject jsonObject1 = array.getJSONObject(i);
                        String paid[] = jsonObject1.getString("paid").split(",");
                        String paidOn[] = jsonObject1.getString("paidOn").split(",");
                        String paidBy[] = jsonObject1.getString("paidBy").split(",");
                        for(int j=1;j<paid.length;j++){
                            arrayList.add(new OnClickPaymentRequestCard.paymentDetails(paid[j], paidOn[j], paidBy[j]));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                paymentAdapter adapter = new paymentAdapter(onClickCancelledCard.this, arrayList);
                splitPayment.setAdapter(adapter);
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sid", saleid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String CB;

    private void getBuyerCb(final String buyer_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    CB = json.getString("current_balance");
                    //  amountDue.setText('\u20B9' + " " + CB);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickCancelledCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyerLedger.class.getName(), onClickCancelledCard.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", buyer_id);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
    private void getOnClickDeliveredCardDetails() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_BUYER_DELIVERED_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("response", response);
                    JSONObject object = new JSONObject(response);
                    JSONObject details = object.getJSONObject("Details");
                    buyername.setText("Buyer : " + details.getString("buyer_name"));
                    bid.setText("Buyer Id : " + details.getString("buyer_id"));
                    commodity.setText(details.getString("commodity"));
                    sid.setText("Sale Id :" + details.getString("sale_id"));
                    weight.setText(details.getString("w1") + " kgs");
                    rate.setText(details.getString("rate") + "/kg");
                    // amountDue.setText(details.getString("amount_due"));

                    double weight1 = Double.parseDouble(details.getString("w1"));
                //    double weight2 = Double.parseDouble(details.getString("w2"));

                    delWeight.setText(details.getString("w2")+" kgs");
                    double rate = Double.parseDouble(details.getString("rate"));

                    buyer_id = details.getString("buyer_id");

                    String paid[] = details.getString("paid").split(",");

                    double PAID = 0.0;
                    for(int i = 0;i<paid.length;i++){
                        if(paid[i].length() == 0){paid[i] = "0.0";}
                        PAID = PAID + Double.parseDouble(paid[i]);
                    }

                    totalAmount.setText((rate * weight1) + "");
                    approvedAmount.setText(details.getString("aa"));
                    deliveredDate.setText(details.getString("delivered_date"));
                    dispatchedDate.setText(details.getString("dispatch_date"));
                    deliveryAddress.setText(details.getString("dispatch_add"));
                    billingAddress.setText(details.getString("billing_add"));
                    vehNum.setText(details.getString("vnum"));
                    driNum.setText(details.getString("dnum"));
                    delBy.setText(details.getString("delName"));
                    dispBy.setText(details.getString("disName"));
                    rocc.setText(details.getString("roc"));
                    canAt.setText(details.getString("canAt"));
                    canBy.setText(details.getString("canBy"));

                    canNum = details.getString("canNum");
                    disNum = details.getString("disNum");
                    delNum = details.getString("delNum");

                    if(details.getString("delivered_date").length() == 0){
                        dell.setVisibility(View.GONE);
                        delll.setVisibility(View.GONE);
                        splitPayment.setVisibility(View.GONE);
                        aall.setVisibility(View.GONE);
                        aaaa.setVisibility(View.GONE);
                        dw.setVisibility(View.GONE);
                        ad.setVisibility(View.GONE);
                    }else {
                        double aa = Double.parseDouble(details.getString("aa"));
                        double fa = aa - PAID;
                        amountDueToBuyer = String.valueOf(fa);
                        amountDue.setText('\u20B9'+""+fa);

                    }

                    getBuyerCb(buyer_id);
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
                Log.e("error", error + "");

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickCancelledCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickCancelledCard.this.getClass().getName(), onClickCancelledCard.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sid", saleid);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
