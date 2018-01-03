package mayank.example.zendor.navigationDrawerOption;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.landingPageFragment.picked;
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;

import static mayank.example.zendor.MainActivity.showError;

public class wallet extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String id;
    private ListView buyerListView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView buyerNameAndZone;
    private ImageView back;
    private LoadingClass ld;
    private ImageView addExpenses;
    private String position;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        cb = findViewById(R.id.cb);
        ld = new LoadingClass(this);

        buyerListView = findViewById(R.id.ledgerView);
        buyerNameAndZone = findViewById(R.id.name);
        ledgerList = new ArrayList<>();
        back = findViewById(R.id.back);
        addExpenses = findViewById(R.id.addCredit);
        ll = findViewById(R.id.ll);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("details", Context.MODE_PRIVATE);

        id = sharedPreferences.getString("id", "");
        position = sharedPreferences.getString("position", "");

        if(position.equals("0")){
            ll.setVisibility(View.GONE);
        }

        addExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDialog();
            }
        });


        getCurrentBalance();
        getLedger();
    }

    private void getCurrentBalance(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    cb.setText(CB);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(wallet.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, wallet.this.getClass().getName(), wallet.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",id);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getLedger(){
        ld.showDialog();
        ledgerList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ZM_LEDGER , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray ledgerArray = json.getJSONArray("ledger");
                    for(int i =0;i<ledgerArray.length();i++){
                        JSONObject ledger = ledgerArray.getJSONObject(i);
                        String date = ledger.getString("date");
                        String pid = ledger.getString("pid");
                        String balance = ledger.getString("Balance");
                        String cd = ledger.getString("cd");
                        ledgerList.add(new sellerLedger.ledgerClass(date, pid, cd,'\u20B9'+ balance));
                    }

                    JSONObject details = json.getJSONObject("details");
                    String name = details.getString("zm");
                    String szone = details.getString("zm_zone");
                    if(position.equals("0")){
                        buyerNameAndZone.setText(name);
                    }else
                        buyerNameAndZone.setText(name+" - "+szone);
                } catch (JSONException e) {
                    ld.dismissDialog();
                    Toast.makeText(wallet.this, "Error Occured", Toast.LENGTH_SHORT).show();

                }

                ledgerAdapter adapter = new ledgerAdapter(wallet.this,0, ledgerList);
                buyerListView.setAdapter(adapter);
                ld.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(wallet.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, wallet.this.getClass().getName(), wallet.this);


                ld.dismissDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",id);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void expenseDialog(){

        final Dialog dialog = new Dialog(wallet.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ledger_request_dialog);

        ImageView back = dialog.findViewById(R.id.back);
        final EditText amount = dialog.findViewById(R.id.amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView request = dialog.findViewById(R.id.request);
        final EditText desc = dialog.findViewById(R.id.dsc);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amt = amount.getText().toString();
                String description = desc.getText().toString();
                sendRequest(amt, description);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendRequest(final String amt, final String desc){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getCurrentBalance();
                getLedger();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(wallet.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, wallet.this.getClass().getName(), wallet.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id","");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("id",id);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
