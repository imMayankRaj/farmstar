package mayank.example.zendor.onClickPicked;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.MainActivity;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.frequentlyUsedClass;
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import mayank.example.zendor.onClickExecutive.executiveLedger;

import static mayank.example.zendor.MainActivity.showError;

public class onClickPickedCard extends AppCompatActivity {

    private TextView sellerNameAndZone;
    private TextView sellerId;
    private TextView callSeller;
    private TextView bookedAt;
    private TextView pickedAt;
    private TextView cname;
    private TextView est_weight;
    private TextView actualWeight;
    private TextView rate;
    private TextView amount;
    private TextView bookedBy;
    private ImageView callBooker;
    private TextView pickedBy;
    private ImageView callPicker;
    private TextView cancel;
    private TextView collect;
    private SharedPreferences sharedPreferences;
    private Intent intent;

    private String pid;
    private String snumber, bnumber, pnumber;
    private String roc;
    private String collectWeight;
    private String[] SNUM, BNUM, PNUM;
    private LoadingClass lc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_picked_card);

        sellerNameAndZone = findViewById(R.id.sellerNameAndZone);
        sellerId = findViewById(R.id.sellerId);
        callSeller = findViewById(R.id.callSeller);
        bookedAt = findViewById(R.id.bookedAt);
        pickedAt = findViewById(R.id.pickedAt);
        cname = findViewById(R.id.cname);
        est_weight = findViewById(R.id.estWeight);
        actualWeight = findViewById(R.id.actualWeight);
        rate = findViewById(R.id.rate);
        amount = findViewById(R.id.totalValue);
        bookedBy = findViewById(R.id.bookerName);
        callBooker = findViewById(R.id.callBooker);
        pickedBy = findViewById(R.id.pickerName);
        callPicker = findViewById(R.id.callPicker);
        cancel = findViewById(R.id.cancel);
        collect = findViewById(R.id.collect);

        lc = new LoadingClass(this);

        sharedPreferences = getSharedPreferences("details", Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        pid = bundle.getString("pid");

        getPickedData();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancellationDialog();
            }
        });

        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCollectClickedDialog();
            }
        });

        callSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(SNUM);
            }
        });

        callBooker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(BNUM);
            }
        });

        callPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(PNUM);
            }
        });

    }


    private void getPickedData(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_BOOKED_CARD_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("values");
                    cname.setText(jsonObject1.getString("commodities"));
                    est_weight.setText(jsonObject1.getString("estimated_weight")+"kgs");
                    rate.setText(jsonObject1.getString("rate")+"/kg");
                    bookedAt.setText(jsonObject1.getString("booked_ts"));
                    pickedAt.setText(jsonObject1.getString("picked_ts"));
                    sellerNameAndZone.setText(jsonObject1.getString("sellername")+"-"+jsonObject1.getString("zname"));
                    sellerId.setText(jsonObject1.getString("seller_id"));
                    bookedBy.setText(jsonObject1.getString("booker"));
                    pickedBy.setText(jsonObject1.getString("picker"));
                    actualWeight.setText(jsonObject1.getString("actual_weight")+"kgs");

                    snumber = jsonObject1.getString("seller_phone");
                    bnumber = jsonObject1.getString("booker_mob");
                    pnumber = jsonObject1.getString("picker_phone");

                    DecimalFormat formatter = new DecimalFormat("#.##");

                    double rate = Double.parseDouble(jsonObject1.getString("rate"));
                    double acw = Double.parseDouble(jsonObject1.getString("actual_weight"));
                    double total = rate*acw;
                    amount.setText(formatter.format(total)+"");

                    SNUM = snumber.split(",");
                    BNUM = bnumber.split(",");
                    PNUM = pnumber.split(",");

                } catch (JSONException e) {
                    Log.e("repoiu", e+"");
                }

                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickPickedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickPickedCard.this.getClass().getName(), onClickPickedCard.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("purchase_id",pid);
                return parameters;
            }
        };
        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void onClickCancelButton(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PICKED_CARD_CANCEL_BUTTON_CLICK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();

                Intent intent = new Intent(onClickPickedCard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Toast.makeText(onClickPickedCard.this, "Error occured.", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickPickedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickPickedCard.this.getClass().getName(), onClickPickedCard.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                Map<String, String> parameters = new HashMap<>();
                parameters.put("purchase_id", pid);
                parameters.put("cancelled_by", id);
                parameters.put("cancelled_ts", dateTimeInGMT.format(new Date()));
                parameters.put("flag", "cn");
                parameters.put("roc_p",roc);

                return parameters;
            }
        };
        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void onClickCollectButton(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PICKED_CARD_COLLECT_BUTTON_CLICK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                Intent intent = new Intent(onClickPickedCard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Toast.makeText(onClickPickedCard.this, "Error occured.", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickPickedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickPickedCard.this.getClass().getName(), onClickPickedCard.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                Map<String, String> parameters = new HashMap<>();
                parameters.put("purchase_id", pid);
                parameters.put("collected_weight", collectWeight);
                parameters.put("collected_ts", dateTimeInGMT.format(new Date()));
                parameters.put("flag", "co");
                parameters.put("collected_by", id);

                return parameters;
            }
        };
        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }


    private void cancellationDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancellation_dialog_picked);
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
                if(roc.length() == 0){
                    Toast.makeText(onClickPickedCard.this, "Please provide some reason for cancellation.", Toast.LENGTH_SHORT).show();
                }else
                    onClickCancelButton();
            }
        });
        dialog.show();
    }

    private void onCollectClickedDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.collect_dialog_picked);
        dialog.setCancelable(true);
        final EditText aw = dialog.findViewById(R.id.collectedWeight);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView submit = dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectWeight = aw.getText().toString();
                if(collectWeight.length() == 0){
                    Toast.makeText(onClickPickedCard.this, "All fields are compulsory.", Toast.LENGTH_SHORT).show();
                }else {
                    onClickCollectButton();
                    dialog.dismiss();
                    frequentlyUsedClass.sendOTP(SNUM[0], "Your Foodmonk verification code is " + "Collected" + " . Happy food ordering :)", onClickPickedCard.this);
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


    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

        final String[] b = numberList.toArray(new String[numberList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Call :")
                .setItems(b, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String number = b[which];
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91"+number));
                        if (ActivityCompat.checkSelfPermission(onClickPickedCard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(onClickPickedCard.this, new String[]{Manifest.permission.CALL_PHONE},
                                    2);
                            return;
                        }else
                            startActivity(intent);
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
        }
    }
}
