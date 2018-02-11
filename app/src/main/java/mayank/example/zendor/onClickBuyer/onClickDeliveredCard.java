package mayank.example.zendor.onClickBuyer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.navigationDrawerOption.sale;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.navigationDrawerOption.sale.recreate;
import static mayank.example.zendor.onClickBuyer.buyerSale.getSaleDetail;

public class onClickDeliveredCard extends AppCompatActivity {

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
    private String amountDueToBuyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_delivered_card);

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

        collectPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialog();
            }
        });

        String pos = sharedPreferences.getString("position", "");

        if (!pos.equals("0")) {
            cancel.setVisibility(View.GONE);
        }

       /* if (getIntent().getStringExtra("f").equals("0")) {
            cancel.setVisibility(View.GONE);
            collectPayment.setVisibility(View.GONE);
        }*/

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBuyer();
            }
        });
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
                    Toast.makeText(onClickDeliveredCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyerLedger.class.getName(), onClickDeliveredCard.this);


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
                    Toast.makeText(onClickDeliveredCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDeliveredCard.this.getClass().getName(), onClickDeliveredCard.this);


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


    private void collectPayment(final String amount, final String ref, final String remark) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.BUYER_DELIVERED_ON_COLLET_CLICK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (getIntent().getStringExtra("f").equals("0")) {
                    Intent intent = new Intent(onClickDeliveredCard.this, sale.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    getSaleDetail(onClickDeliveredCard.this);
                    buyerLedger.click.performClick();
                }
                lc.dismissDialog();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickDeliveredCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDeliveredCard.this.getClass().getName(), onClickDeliveredCard.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String id = sharedPreferences.getString("id", "");


                Map<String, String> params = new HashMap<>();
                params.put("bid", buyer_id);
                params.put("sid", saleid);
                params.put("rid", id);
                params.put("des","\n"+ remark + "\n" + ref);
                params.put("amt", amount);
                params.put("amtDue", amountDueToBuyer);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void showPaymentDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.collect_payment_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final EditText am = dialog.findViewById(R.id.amount);
        final EditText re = dialog.findViewById(R.id.reference);
        final EditText rem = dialog.findViewById(R.id.remark);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView submit = dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = am.getText().toString();
                String ref = re.getText().toString();
                String remark = rem.getText().toString();
                if (amount.length() != 0 && ref.length() != 0 && remark.length() != 0) {
                    double amt = Double.parseDouble(amount);
                    double b_amount = Double.parseDouble(amountDueToBuyer );
                    if (amt <= b_amount) {
                        collectPayment(amount, ref, remark);
                        dialog.dismiss();
                    } else
                        Toast.makeText(onClickDeliveredCard.this, "Requested Amount Greater Than Amount Due.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(onClickDeliveredCard.this, "All Fields Are Compulsory.", Toast.LENGTH_SHORT).show();
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

    private void cancelBuyer() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.CANCEL_BUYER_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (getIntent().getStringExtra("f").equals("0")) {
                    Intent intent = new Intent(onClickDeliveredCard.this, sale.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else
                    getSaleDetail(onClickDeliveredCard.this);

                lc.dismissDialog();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickDeliveredCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickDeliveredCard.this.getClass().getName(), onClickDeliveredCard.this);


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
