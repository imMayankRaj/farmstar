package mayank.example.zendor.navigationDrawerOption;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.frequentlyUsedClass.notifyUser;

public class wallet extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String id;
    private ListView buyerListView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView buyerNameAndZone;
    private LoadingClass ld;
    private ImageView addExpenses;
    private String position;
    private Toolbar toolbar;
    private LinearLayout ll;
    private DatabaseReference mDatabase;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        cb = findViewById(R.id.cb);
        ld = new LoadingClass(this);

        buyerListView = findViewById(R.id.ledgerView);
        buyerNameAndZone = findViewById(R.id.name);
        ledgerList = new ArrayList<>();
        addExpenses = findViewById(R.id.addCredit);
        toolbar = findViewById(R.id.toolbar);
        ll = findViewById(R.id.ll);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.removeEventListener(valueEventListener);
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("details", Context.MODE_PRIVATE);

        id = sharedPreferences.getString("id", "");
        position = sharedPreferences.getString("position", "");

        if (position.equals("0")) {
            ll.setVisibility(View.GONE);
        }

        buyerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String flag = ledgerList.get(position).getFlag();
                if (flag.equals("1")) {
                    getSellerPaymentDetails(ledgerList.get(position).getSid());
                }
            }
        });

        addExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDialog();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase = mDatabase.child("users").child(id);

        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Long time = System.currentTimeMillis();
                    mDatabase.child("Ledger").setValue(time + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.addValueEventListener(valueEventListener);


        getCurrentBalance();
        getLedger();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDatabase.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() && (count != 0 )) {
                getCurrentBalance();
                getLedger();
            }
            count ++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private void getSellerPaymentDetails(final String sid) {
        ld.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_LEDGER_SELLER_PURCHASE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dsdsad", response);
                        ld.dismissDialog();
                        showSellerPaymentDetails(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sid", sid);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void showSellerPaymentDetails(String s) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.on_click_ledger);

        ImageView back = dialog.findViewById(R.id.back);
        TextView reqId = dialog.findViewById(R.id.prid);
        TextView reqBy = dialog.findViewById(R.id.reqBy);
        TextView reqAt = dialog.findViewById(R.id.reqAt);
        TextView proBy = dialog.findViewById(R.id.proBy);
        TextView proAt = dialog.findViewById(R.id.proAt);
        TextView amt = dialog.findViewById(R.id.amount);
        TextView sellerName = dialog.findViewById(R.id.sellerName);
        TextView sellerId = dialog.findViewById(R.id.sellerId);
        TextView ok = dialog.findViewById(R.id.ok);

        try {
            JSONObject jsonObject = new JSONObject(s);
            reqId.setText(jsonObject.getString("reqId"));
            reqBy.setText(jsonObject.getString("reqBy"));
            reqAt.setText(jsonObject.getString("reqAt"));
            proBy.setText(jsonObject.getString("proBy"));
            proAt.setText(jsonObject.getString("proAt"));
            amt.setText('\u20B9' + jsonObject.getString("amount"));
            sellerId.setText(jsonObject.getString("sellerId"));
            sellerName.setText(jsonObject.getString("sellerName"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void getCurrentBalance() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    cb.setText('\u20B9' + CB);
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", id);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getLedger() {
        ld.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ZM_LEDGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ledgerList.clear();

                    JSONObject json = new JSONObject(response);
                    JSONArray ledgerArray = json.getJSONArray("ledger");
                    for (int i = 0; i < ledgerArray.length(); i++) {
                        JSONObject ledger = ledgerArray.getJSONObject(i);
                        String date = ledger.getString("date");
                        String pid = ledger.getString("pid");
                        String balance = ledger.getString("Balance");
                        String cd = ledger.getString("cd");
                        String flag = ledger.getString("flag");
                        String sid = ledger.getString("sid");

                        try {
                            if (cd.substring(cd.lastIndexOf(" ")).equals(" cr") || cd.substring(cd.lastIndexOf(" ")).equals(" dr"))
                                cd = '\u20B9' + cd;
                        } catch (Exception e) {
                        }
                        ledgerList.add(new sellerLedger.ledgerClass(date, pid, cd, '\u20B9' + balance, flag, sid));
                    }

                    JSONObject details = json.getJSONObject("details");
                    String name = details.getString("zm");
                    String szone = details.getString("zm_zone");
                    if (position.equals("0")) {
                        buyerNameAndZone.setText(name);
                    } else
                        buyerNameAndZone.setText(name + " - " + szone);
                } catch (JSONException e) {
                    ld.dismissDialog();
                    Toast.makeText(wallet.this, "Error Occured", Toast.LENGTH_SHORT).show();

                }

                ledgerAdapter adapter = new ledgerAdapter(wallet.this, 0, ledgerList);
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", id);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void expenseDialog() {

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
                if (amt.length() == 0 || description.length() == 0) {
                    Toast.makeText(wallet.this, "Some fields are left empty.", Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest(amt, description);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void sendRequest(final String amt, final String desc) {
        ld.showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_EXPENSES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String name = sharedPreferences.getString("name", "");
                String pos = sharedPreferences.getString("position", "");
                String zname = sharedPreferences.getString("zone", "");

                if (pos.equals("2")) {
                    String message = "Executive : " + name + " (" + zname  +") Request ID : " + response+".";
                    notifyUser("Expense Added", message, wallet.this, "2","");
                }
                else if (pos.equals("1")) {
                    String message = "Zonal Manager : " + name + " ("+zname +") Request ID : " + response+".";
                    notifyUser("Expense Added", message, wallet.this, "2", "");

                }

                Long time = System.currentTimeMillis();
                mDatabase.child("Ledger").setValue(time + "");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(wallet.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, wallet.this.getClass().getName(), wallet.this);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", id);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


}
