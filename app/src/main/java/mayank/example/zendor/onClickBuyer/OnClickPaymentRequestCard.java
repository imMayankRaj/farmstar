package mayank.example.zendor.onClickBuyer;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class OnClickPaymentRequestCard extends AppCompatActivity {

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
    private TextView collectPayment;
    private TextView cancel;
    private String buyer_id;
    private SharedPreferences sharedPreferences;
    private TextView toolbarSaleId;
    private LoadingClass lc;
    private Toolbar toolbar;
    private ListView splitPayment;
    private String amountDueToBuyer;
    private ArrayList<paymentDetails> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_payment_request_card);


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
        collectPayment = findViewById(R.id.collectPayment);
        cancel = findViewById(R.id.cancel);
        toolbar = findViewById(R.id.toolbar);
        splitPayment = findViewById(R.id.splitPayment);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lc = new LoadingClass(this);


        arrayList = new ArrayList<>();

        saleid = getIntent().getStringExtra("sid");

        //  amountDue.setText(buyerLedger.buyercb+"");
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        toolbarSaleId = findViewById(R.id.tsid);
        toolbarSaleId.setText("SALE ID :" + saleid);

        getOnClickDeliveredCardDetails();
        getPaymentData();

        String pos = sharedPreferences.getString("position", "");

        if (!pos.equals("0")) {
            cancel.setVisibility(View.GONE);
        }

       /* if (getIntent().getStringExtra("f").equals("0")) {
            cancel.setVisibility(View.GONE);
            collectPayment.setVisibility(View.GONE);
        }*/

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
                    weight.setText(details.getString("w2") + " kgs");
                    rate.setText(details.getString("rate") + "/kg");
                    // amountDue.setText(details.getString("amount_due"));

                    double weight1 = Double.parseDouble(details.getString("weight"));
                    double weight2 = Double.parseDouble(details.getString("w2"));

                    double rate = Double.parseDouble(details.getString("rate"));

                    buyer_id = details.getString("buyer_id");

                    String paid[] = details.getString("paid").split(",");

                    double PAID = 0.0;
                    for(int i = 0;i<paid.length;i++){
                        if(paid[i].length() == 0){paid[i] = "0.0";}
                        PAID = PAID + Double.parseDouble(paid[i]);
                    }

                    double aa = Double.parseDouble(details.getString("aa"));
                    double fa = aa - PAID;
                    amountDueToBuyer = String.valueOf(fa);
                    amountDue.setText('\u20B9'+""+fa);
                    totalAmount.setText((rate * weight1) + "");
                    approvedAmount.setText(details.getString("aa"));
                    deliveredDate.setText(details.getString("delivered_date"));
                    dispatchedDate.setText(details.getString("dispatch_date"));
                    deliveryAddress.setText(details.getString("dispatch_add"));
                    billingAddress.setText(details.getString("billing_add"));

                  //  getBuyerCb(buyer_id);
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
                    Toast.makeText(OnClickPaymentRequestCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, OnClickPaymentRequestCard.this.getClass().getName(), OnClickPaymentRequestCard.this);


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

    private void getPaymentData(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_PAYMENT_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("pay res", response);
                arrayList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("Details");
                    for(int i =0;i<array.length();i++){
                        JSONObject jsonObject1 = array.getJSONObject(i);
                        String paid[] = jsonObject1.getString("paid").split(",");
                        String paidOn[] = jsonObject1.getString("paidOn").split(",");
                        for(int j=1;j<paid.length;j++){
                            arrayList.add(new paymentDetails(paid[j], paidOn[j]));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                paymentAdapter adapter = new paymentAdapter(OnClickPaymentRequestCard.this, arrayList);
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

    public class paymentDetails{
        private String paid;
        private String paidOn;

        paymentDetails(String paid, String paidOn){
            this.paid = paid;
            this.paidOn = paidOn;
        }


        public String getPaid() {
            return paid;
        }

        public String getPaidOn() {
            return paidOn;
        }
    }
}
