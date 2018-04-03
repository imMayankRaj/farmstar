package mayank.example.zendor.onClickBuyer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.navigationDrawerOption.sale;
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import mayank.example.zendor.onClickPicked.onClickPickedCard;
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
    private TextView delWeight;
    private String amountDueToBuyer;
    private ListView splitPayment;
    private ArrayList<OnClickPaymentRequestCard.paymentDetails> arrayList;
    private String roc;
    private TextView vehNum, driNum, dispBy, delBy;
    private ImageView callDriver, callDel, callDis;
    private String delNum, disNum, canNum;
    private DatabaseReference mDatabase, mDatabase1;
    private int count = 0;
    private boolean isClicked = true;
    private boolean isClickedButton = false;
    private boolean isClickedCancel = false;

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
        delWeight = findViewById(R.id.deweight);
        cancel = findViewById(R.id.cancel);
        toolbar = findViewById(R.id.toolbar);
        splitPayment = findViewById(R.id.splitPayment);
        vehNum = findViewById(R.id.vnum);
        driNum = findViewById(R.id.driNum);
        dispBy = findViewById(R.id.disBy);
        delBy = findViewById(R.id.delBy);
        callDriver = findViewById(R.id.callDriver);
        callDel = findViewById(R.id.callDel);
        callDis = findViewById(R.id.callDispatch);

        arrayList = new ArrayList<>();

        saleid = getIntent().getStringExtra("sid");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.removeEventListener(valueEventListener);
                finish();
            }
        });

        lc = new LoadingClass(this);

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


        callDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(disNum);
            }
        });


        //  amountDue.setText(buyerLedger.buyercb+"");
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        toolbarSaleId = findViewById(R.id.tsid);
        toolbarSaleId.setText("SALE ID :" + saleid);

        getOnClickDeliveredCardDetails();
        getPaymentData();

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
                cancellationDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDatabase.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists() && count != 0){
                getOnClickDeliveredCardDetails();
                getPaymentData();
            }
            count++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    Intent intent;
    private void callDialog(final String a) {

        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91"+a));
        if (ActivityCompat.checkSelfPermission(onClickDeliveredCard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(onClickDeliveredCard.this, new String[]{Manifest.permission.CALL_PHONE},
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

    private void getPaymentData() {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_PAYMENT_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                arrayList.clear();
                try {
                    Log.e("resp", response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("Details");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject1 = array.getJSONObject(i);
                        String paid[] = jsonObject1.getString("paid").split(",");
                        String paidOn[] = jsonObject1.getString("paidOn").split(",");
                        String paidBy[] = jsonObject1.getString("paidBy").split(",");
                        for (int j = 1; j < paid.length; j++) {
                            arrayList.add(new OnClickPaymentRequestCard.paymentDetails(paid[j], paidOn[j], paidBy[j]));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                paymentAdapter adapter = new paymentAdapter(onClickDeliveredCard.this, arrayList);
                splitPayment.setAdapter(adapter);
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
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
                    weight.setText(details.getString("w1") + " kgs");
                    rate.setText(details.getString("rate") + "/kg");
                    // amountDue.setText(details.getString("amount_due"));
                    double weight1 = 0.0;

                    weight1 = Double.parseDouble(details.getString("w1"));
                    double weight2 = Double.parseDouble(details.getString("w2"));

                    delWeight.setText(details.getString("w2") + " kgs");
                    double rate = Double.parseDouble(details.getString("rate"));

                    buyer_id = details.getString("buyer_id");

                    String paid[] = details.getString("paid").split(",");

                    double PAID = 0.0;
                    for (int i = 0; i < paid.length; i++) {
                        if (paid[i].length() == 0) {
                            paid[i] = "0.0";
                        }
                        PAID = PAID + Double.parseDouble(paid[i]);
                    }

                    double aa = Double.parseDouble(details.getString("aa"));
                    double fa = aa - PAID;
                    amountDueToBuyer = String.valueOf(fa);
                    amountDue.setText('\u20B9' + "" + fa);
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

                    disNum = details.getString("disNum");
                    delNum = details.getString("delNum");

                    if(isClicked){
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("buyers").child(buyer_id).child("Ledger");
                        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("buyerSales").child(buyer_id);

                        mDatabase.addValueEventListener(valueEventListener);

                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    long time = System.currentTimeMillis();
                                    mDatabase.setValue(time+"");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        isClicked = false;
                    }

                    getBuyerCb(buyer_id);
                } catch (Exception e) {
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
                if(isClickedButton){
                    mDatabase.removeEventListener(valueEventListener);
                }

                long time = System.currentTimeMillis();
                mDatabase.setValue(time+"");
             //  mDatabase1.setValue(time+"");
                if (getIntent().getStringExtra("f").equals("0")) {
                    Intent intent = new Intent(onClickDeliveredCard.this, sale.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (getIntent().getStringExtra("f").equals("2")) {
                   // buyerLedger.click.performClick();
                    finish();
                } else {
                    getSaleDetail(onClickDeliveredCard.this);
                   // buyerLedger.click.performClick();
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
                params.put("des", "\n" + remark + "\n" + ref);
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
                if (amount.length() != 0 && ref.length() != 0) {
                    double amt = Double.parseDouble(amount);
                    double b_amount = Double.parseDouble(amountDueToBuyer);
                    if (amt <= b_amount) {
                        isClickedButton = true;
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
                    Toast.makeText(onClickDeliveredCard.this, "Please provide some reason for cancellation.", Toast.LENGTH_SHORT).show();
                } else {
                    isClickedCancel = true;
                    cancelBuyer();
                }
            }
        });
        dialog.show();
    }


    private void cancelBuyer() {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.CANCEL_BUYER_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(isClickedCancel){
                    mDatabase.removeEventListener(valueEventListener);
                }

                long time = System.currentTimeMillis();
                mDatabase.setValue(time+"");
                mDatabase1.setValue(time+"");

                if (getIntent().getStringExtra("f").equals("0")) {
                    Intent intent = new Intent(onClickDeliveredCard.this, sale.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (getIntent().getStringExtra("f").equals("2")) {
                    buyerLedger.click.performClick();
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
                String id = sharedPreferences.getString("id", "");

                Map<String, String> params = new HashMap<>();
                params.put("sid", saleid);
                params.put("remark", roc);
                params.put("canBy", id);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
