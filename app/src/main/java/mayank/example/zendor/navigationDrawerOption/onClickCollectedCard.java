package mayank.example.zendor.navigationDrawerOption;

import android.Manifest;
import android.app.AlertDialog;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.onClickPicked.onClickPickedCard;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class onClickCollectedCard extends AppCompatActivity {

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
    private Toolbar toolbar;
    private TextView collectedWeight;
    private TextView collectedBy;
    private TextView collectedAt;
    private String cw, cb, ca;
    private TextView toolbarText;
    private TextView aw;
    private TextView pat;
    private String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_collected_card);

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
        collectedWeight = findViewById(R.id.collectedWeight);
        collectedBy = findViewById(R.id.collectedBy);
        collectedAt = findViewById(R.id.collectedAt);
        toolbarText = findViewById(R.id.toolbarText);

        aw = findViewById(R.id.aw);
        pat = findViewById(R.id.pat);




        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lc = new LoadingClass(this);

        sharedPreferences = getSharedPreferences("details", Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getBundleExtra("extras");
        ca = bundle.getString("collectedts", "");
        cb = bundle.getString("collectedby","");
        cw = bundle.getString("collectedWe","");
        pid = bundle.getString("pid","");
        extra = bundle.getString("extra", "0");


        toolbarText.setText("Collected"+" (PID : "+pid+")");

        if(extra.equals("1")){
            getExtraDetails(pid);
        }

        getPickedData();

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

    private void getExtraDetails(final String pid){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.EXTRAS_SELLER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    collectedBy.setText(jsonObject.getString("collectedBy"));
                    collectedWeight.setText(jsonObject.getString("collectedWeight"));
                    collectedAt.setText(jsonObject.getString("collectedAt"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pid", pid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

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
                    rate.setText('\u20B9'+jsonObject1.getString("rate")+"/kg");
                    bookedAt.setText(jsonObject1.getString("booked_ts"));
                    if(jsonObject1.getString("picked_ts").length()==0){
                        aw.setVisibility(View.GONE);
                        pat.setVisibility(View.GONE);
                        pickedAt.setVisibility(View.GONE);
                        actualWeight.setVisibility(View.GONE);
                    }
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

                    try {
                        double rate = Double.parseDouble(jsonObject1.getString("rate"));
                        double acw = Double.parseDouble(jsonObject1.getString("actual_weight"));
                        double total = rate * acw;
                        amount.setText('\u20B9' + formatter.format(total) + "");
                    }catch (Exception e){}

                    SNUM = snumber.split(",");
                    BNUM = bnumber.split(",");
                    PNUM = pnumber.split(",");

                    if(!extra.equals("1")){
                        collectedWeight.setText(cw+"kgs");
                        collectedBy.setText(cb);
                        collectedAt.setText(ca);
                    }


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
                    Toast.makeText(onClickCollectedCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickCollectedCard.this.getClass().getName(), onClickCollectedCard.this);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("purchase_id",pid);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
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
                        if (ActivityCompat.checkSelfPermission(onClickCollectedCard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(onClickCollectedCard.this, new String[]{Manifest.permission.CALL_PHONE},
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
