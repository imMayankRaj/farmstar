package mayank.example.zendor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import xendorp1.adapters.zone_card_spinner_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zone_card;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;

public class sellerDetailActivity extends AppCompatActivity {

    private EditText name;
    private EditText address;
    private EditText phone;
    private TextView proceed;
    private EditText pincode;
    public static RequestQueue requestQueue;
    private apiConnect connect;
    private View view;
    private Spinner zone;
    private SharedPreferences sharedPreferences;
    private static final String userName = "2000148140";
    private static final String password = "hvDjCw";
    private ProgressDialog dialog;
    private String zone_id = "";
    private boolean check = true;
    private List<zone_card> zonelist;
    private LinearLayout zoneLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        proceed = findViewById(R.id.proceed);
        view = findViewById(R.id.sellerView);
        pincode = findViewById(R.id.pincode);
        zone = findViewById(R.id.zone);
        zoneLayout = findViewById(R.id.zonelayout);
        zonelist = new ArrayList<>();


        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        connect = new apiConnect(sellerDetailActivity.this, "pushSellerData");
        requestQueue = connect.getRequestQueue();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String pos = sharedPreferences.getString("position", "");

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 10){
                    checkNumber(s.toString());
                }
            }
        });

        if(pos.equals("0")){
            zoneLayout.setVisibility(View.VISIBLE);
            dialog.show();
            getZones();
        }else
            zone_id = " ";

        zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    zone_id =zonelist.get(position).getZone_id();
                }else
                    zone_id = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pincode.getText().toString();
                String n = name.getText().toString();
                String a = address.getText().toString();
                String p = phone.getText().toString();

                if(n.length()==0 || a.length()==0 || p.length()==0 || pin.length()==0 || zone_id.length() == 0)
                    Toast.makeText(sellerDetailActivity.this, "Some Fields Are Left Empty", Toast.LENGTH_SHORT).show();
                else if(p.length()<10)
                    Toast.makeText(sellerDetailActivity.this, "Incorrect Number Entered.", Toast.LENGTH_SHORT).show();
                else if(!check)
                    Toast.makeText(sellerDetailActivity.this, "Number Already Exists.", Toast.LENGTH_SHORT).show();
                else {
                    Random rand = new Random();
                    String otp = String.format("%04d",rand.nextInt(10000));
                    Intent intent = new Intent(sellerDetailActivity.this, sellerOtpVerify.class);
                    sendOTP(p, otp);
                    intent.putExtra("otp", otp);
                    intent.putExtra("name", n);
                    intent.putExtra("address", a);
                    intent.putExtra("phone", p);
                    intent.putExtra("zoneid",zone_id);
                    intent.putExtra("pincode",pin);
                    startActivity(intent);
                }
            }
        });



    }

    public static void sendOTP(final String phone, final String otp){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("enterprise.smsgupshup.com");
        builder.appendPath("GatewayAPI");
        builder.appendPath("rest");
        builder.appendQueryParameter("method", "sendMessage");
        builder.appendQueryParameter("msg","Your Foodmonk verification code is "+otp+" . Happy food ordering :)");
        builder.appendQueryParameter("v","1.1");
        builder.appendQueryParameter("userid", userName);
        builder.appendQueryParameter("password", password);
        builder.appendQueryParameter("send_to","91"+phone);
        builder.appendQueryParameter("msg_type", "text");
        builder.appendQueryParameter("format", "JSON");
        builder.appendQueryParameter("auth_scheme", "Plain");
        builder.appendQueryParameter("mask", "ZENDOR");
        String url = builder.build().toString();
        Log.e("url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("otp response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    private void getZones() {
        zonelist.clear();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_ZONES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    zonelist.add(new zone_card());
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        zone_card zone_card = new zone_card();
                        zone_card.setZone_name(jsonObject.getString("zname"));
                        zone_card.setZone_id(jsonObject.getString("zid"));
                        zonelist.add(zone_card);
                    }
                    zone_card_spinner_adapter adapter=new zone_card_spinner_adapter(sellerDetailActivity.this,R.layout.spinner_zone,zonelist,getLayoutInflater());
                    zone.setAdapter(adapter);
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(strReq, "getzones");
    }

    public void checkNumber(final String number){
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.CHECKNUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if(status.equals("exists")) {
                        check = false;
                        Toast.makeText(sellerDetailActivity.this, "Number Already Exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        check = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("number", number);
                return parameters;
            }
        };
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }
}

