package mayank.example.zendor.onClickBooked;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import mayank.example.zendor.landingPageFragment.booked;
import mayank.example.zendor.onClickPicked.onClickPickedCard;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.frequentlyUsedClass.notifyUser;

public class onClickBookedCard extends AppCompatActivity {

    private TextView sellerNameAndZone;
    private TextView sellerId;
    private TextView callSeller;
    private TextView bookedAt;
    private TextView cname;
    private TextView est_weight;
    private TextView rate;
    private TextView totalValue;
    private TextView bookerName;
    private ImageView callBooker;
    private TextView cancel;
    private TextView pick;
    private String pid;
    private SharedPreferences sharedPreferences;
    private String roc;
    private String snumber, bnumber;
    private String actualWeight;
    private Intent intent;
    private String SNUM[], BNUM[];
    private LoadingClass lc;
    private String Rate, Commodity;
    private Toolbar toolbar;
    private TextView toolbarText;
    private DatabaseReference mDatabase;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_booked_card);
        sellerNameAndZone = findViewById(R.id.sellerNameAndZone);
        sellerId = findViewById(R.id.sellerId);
        callSeller = findViewById(R.id.callSeller);
        bookedAt = findViewById(R.id.bookedAt);
        cname = findViewById(R.id.cname);
        est_weight = findViewById(R.id.estWeight);
        rate = findViewById(R.id.rate);
        totalValue = findViewById(R.id.totalValue);
        bookerName = findViewById(R.id.bookerName);
        callBooker = findViewById(R.id.callBooker);
        cancel = findViewById(R.id.cancel);
        pick = findViewById(R.id.pick);
        toolbar = findViewById(R.id.toolbar);
        toolbarText = findViewById(R.id.toolbarText);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lc = new LoadingClass(this);

        sharedPreferences = getSharedPreferences("details", Context.MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Bundle bundle = getIntent().getExtras();
        pid = bundle.getString("pid");
        toolbarText.setText("Booked" + " (PID : " + pid + ")");
        getBookedDetails();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancellationDialog();
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickClickedDialog();
            }
        });

        callBooker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(BNUM);
            }
        });

        callSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(SNUM);
            }
        });

    }

    private void addCancelDetails(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_CANCEL_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();

                String zid = sharedPreferences.getString("zid", "");
                long time = System.currentTimeMillis();
                mDatabase.getParent().getParent().child("booking").child(response).setValue(time+"");

               /* Intent intent = new Intent(onClickBookedCard.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Toast.makeText(onClickBookedCard.this, "Error occured.", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickBookedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickBookedCard.this.getClass().getName(), onClickBookedCard.this);

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
                parameters.put("roc_b",roc);

                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addPickDetails(){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_PICKED_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();

                String name = sharedPreferences.getString("name", "");
                String pos = sharedPreferences.getString("position", "");
                String zid = sharedPreferences.getString("zid", "");
                if (pos.equals("2"))
                    notifyUser("Commodity Picked", "Purchase Id : "+pid, onClickBookedCard.this, "1", "");


                Long time = System.currentTimeMillis();
                mDatabase.child("Ledger").setValue(time + "");
                mDatabase.getParent().getParent().child("picking").child(response).setValue(time+"");
                mDatabase.getParent().getParent().child("booking").child(response).setValue(time+"");

              /*  Intent intent = new Intent(onClickBookedCard.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Toast.makeText(onClickBookedCard.this, "Error occured.", Toast.LENGTH_SHORT).show();
                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickBookedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickBookedCard.this.getClass().getName(), onClickBookedCard.this);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                Map<String, String> parameters = new HashMap<>();
                parameters.put("purchase_id", pid);
                parameters.put("actual_weight", actualWeight);
                parameters.put("purchase_ts", dateTimeInGMT.format(new Date()));
                parameters.put("flag", "pk");
                parameters.put("picker_id", id);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void getBookedDetails(){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.GET_BOOKED_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("values");
                    cname.setText(jsonObject1.getString("commodities"));
                    est_weight.setText(jsonObject1.getString("estimated_weight")+"kgs");
                    rate.setText(jsonObject1.getString("rate")+"/kg");
                    bookedAt.setText(jsonObject1.getString("booked_ts"));
                    sellerNameAndZone.setText(jsonObject1.getString("sellername")+"-"+jsonObject1.getString("zname"));
                    sellerId.setText(jsonObject1.getString("seller_id"));
                    snumber = jsonObject1.getString("seller_phone");
                    bookerName.setText(jsonObject1.getString("booker"));
                    bnumber = jsonObject1.getString("booker_mob");

                    DecimalFormat formatter = new DecimalFormat("#.##");

                    double rate = Double.parseDouble(jsonObject1.getString("rate"));
                    double ewt = Double.parseDouble(jsonObject1.getString("estimated_weight"));
                    double total = rate*ewt;
                    totalValue.setText(formatter.format(total)+"");

                    SNUM = snumber.split(",");
                    BNUM = bnumber.split(",");

                    Rate = jsonObject1.getString("rate");
                    Commodity = jsonObject1.getString("commodities");

                    mDatabase = mDatabase.child("users").child(jsonObject1.getString("seller_id"));


                } catch (JSONException e) {
                    lc.dismissDialog();
                   Log.e("onclickexception", e+"");
                }

                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
                Log.e("repo", error+"");

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickBookedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickBookedCard.this.getClass().getName(), onClickBookedCard.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("purchase_id", pid);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void cancellationDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reason_for_cancellation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final RadioGroup rd = dialog.findViewById(R.id.rg);
        final RadioButton rb1 = dialog.findViewById(R.id.rb1);
        final RadioButton rb2 = dialog.findViewById(R.id.rb2);
        final RadioButton rb3 = dialog.findViewById(R.id.rb3);
        final EditText text = dialog.findViewById(R.id.others);
        ImageView back = dialog.findViewById(R.id.back);
        TextView submit = dialog.findViewById(R.id.submit);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1:
                        text.setVisibility(View.GONE);
                        break;
                    case R.id.rb2:
                        text.setVisibility(View.GONE);
                        break;
                    case R.id.rb3:
                        text.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roc = "";
                switch (rd.getCheckedRadioButtonId()){
                    case R.id.rb1:
                        text.setVisibility(View.GONE);
                        roc = rb1.getText().toString();
                        break;
                    case R.id.rb2:
                        text.setVisibility(View.GONE);
                        roc = rb2.getText().toString();
                        break;
                    case R.id.rb3:
                        text.setVisibility(View.VISIBLE);
                        roc = text.getText().toString();
                        break;

                }
                if(roc.length() == 0){
                    Toast.makeText(onClickBookedCard.this, "Add some reaons.", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.dismiss();
                    addCancelDetails();
                }
            }
        });
        dialog.show();
    }

    private void onPickClickedDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.actual_weight_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText aw = dialog.findViewById(R.id.actualWeight);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView submit = dialog.findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualWeight = aw.getText().toString();
                if (actualWeight.length() == 0) {
                    Toast.makeText(onClickBookedCard.this, "All fields are compulsory", Toast.LENGTH_SHORT).show();
                } else {
                    addPickDetails();
                    dialog.dismiss();

                    DecimalFormat formatter = new DecimalFormat("#.##");

                    double rate = Double.parseDouble(Rate);
                    double ewt = Double.parseDouble(actualWeight);
                    double total = rate*ewt;

                    frequentlyUsedClass.sendOTP(SNUM[0], "प्रिय किसान भाई, फ़ार्मस्टार के साथ व्यापार के लिये धन्यवाद,\n" +
                            "आपके बिक्री का विवरण:\n" +
                            "अनाज : "+Commodity+"\n" +
                            "वजन : "+actualWeight+" किलो\n" +
                            "दर : "+Rate+" प्रति किलो\n" +
                            "बिक्री-मूल्य : ₹ "+formatter.format(total)+"\n" +
                            "\n" +
                            "टीम फ़ार्मस्टार", onClickBookedCard.this);
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
                        if (ActivityCompat.checkSelfPermission(onClickBookedCard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(onClickBookedCard.this, new String[]{Manifest.permission.CALL_PHONE},
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
