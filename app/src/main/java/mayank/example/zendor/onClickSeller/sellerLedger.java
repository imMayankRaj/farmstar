package mayank.example.zendor.onClickSeller;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
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
import com.google.firebase.database.ChildEventListener;
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
import java.util.Random;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.navigationDrawerOption.onClickCancelledCard;
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import xendorp1.application_classes.AppController;

import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.frequentlyUsedClass.notifyUser;
import static mayank.example.zendor.onClickSeller.sellerDetails.amountDue;


public class sellerLedger extends Fragment {


    public static String SELLER_ID = "seller_id";
    private String sid;
    private ListView sellerLedgerView;
    private TextView cb;
    private ArrayList<sellerledgerClass> ledgerList;
    private TextView sellerNameAndZone;
    private SharedPreferences sharedPreferences;
    private TextView transfer;
    private LoadingClass lc;
    private double ucb;
    public static double sellerCb;
    private DatabaseReference mDatabase;
    private int count = 0;


    public sellerLedger() {

    }

    public static sellerLedger newInstance(String seller_id) {
        sellerLedger fragment = new sellerLedger();
        Bundle bundle = new Bundle();
        bundle.putString(SELLER_ID, seller_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sid = getArguments().getString(SELLER_ID);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_ledger, container, false);
        sellerLedgerView = view.findViewById(R.id.ledgerView);
        cb = view.findViewById(R.id.cb);
        transfer = view.findViewById(R.id.transfer);
        sellerNameAndZone = view.findViewById(R.id.sellerNameAndZone);
        ledgerList = new ArrayList<>();

        lc = new LoadingClass(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getActivity().getSharedPreferences("details", MODE_PRIVATE);

        count = 0;
        getSellerCb();
        getSellerLedger();
        getUserCb();

        mDatabase = mDatabase.child("users").child(sid.substring(11));

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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


        sellerLedgerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String isFlag = ledgerList.get(position).getIsSale();
                String det = ledgerList.get(position).getTransaction();
                String pid = det.substring(det.lastIndexOf(" "));
                if (isFlag.equals("1")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pid", pid.trim());
                    Intent intent = new Intent(getActivity(), onClickLedgerDetails.class);
                    intent.putExtra("extras", bundle);
                    startActivity(intent);
                }
            }
        });

        final String pos = sharedPreferences.getString("position", "");


        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if ((ucb < 0 || ucb == 0) && !pos.equals("0"))
                    Toast.makeText(getActivity(), "Not Enough Credits.", Toast.LENGTH_SHORT).show();
                else {*/
                if (sellerCb <= 0) {

                } else
                    requestDialog();
                // }
            }
        });


        return view;
    }

    public void removeListener() {
        mDatabase.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() && (count != 0)) {
                getSellerCb();
                getSellerLedger();
                getUserCb();
            }
            count++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getUserCb() {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    ucb = Double.parseDouble(CB);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), getActivity());


                lc.dismissDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");


                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", id);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getSellerCb() {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    cb.setText('\u20B9' + CB);
                    sellerCb = Double.parseDouble(CB);
                    amountDue.setText('\u20B9' + " " + CB);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), getActivity());


                lc.dismissDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", sid.substring(11));
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getSellerLedger() {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELL_LEDGER, new Response.Listener<String>() {
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
                        String dc = ledger.getString("dc");
                        String balance = ledger.getString("Balance");
                        String isSale = ledger.getString("isSale");
                        String sid = ledger.getString("sid");

                        try {
                            if (dc.substring(dc.lastIndexOf(" ")).equals(" cr") || dc.substring(dc.lastIndexOf(" ")).equals(" dr"))
                                dc = '\u20B9' + dc;
                        } catch (Exception e) {

                        }
                        ledgerList.add(new sellerledgerClass(date, pid, dc, '\u20B9' + balance, isSale, sid));
                    }

                    JSONObject details = json.getJSONObject("details");
                    String name = details.getString("zm");
                    String szone = details.getString("zm_zone");
                    sellerNameAndZone.setText(name + " - " + szone);

                } catch (JSONException e) {
                    Log.e("error e", e + "");
                }

                sellerLedgerAdapter adapter = new sellerLedgerAdapter(getActivity(), 0, ledgerList);
                sellerLedgerView.setAdapter(adapter);
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), getActivity());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", sid.substring(11));
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public static class sellerledgerClass {

        private String date;
        private String transaction;
        private String status;
        private String balance;
        private String isSale;
        private String sid;

        public sellerledgerClass(String date, String transaction, String status, String balance, String isSale, String sid) {

            this.date = date;
            this.transaction = transaction;
            this.status = status;
            this.sid = sid;
            this.isSale = isSale;
            this.balance = balance;
        }


        public String getIsSale() {
            return isSale;
        }

        public String getSid() {
            return sid;
        }

        public String getBalance() {
            return balance;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public String getTransaction() {
            return transaction;
        }
    }


    public static class ledgerClass {

        private String date;
        private String transaction;
        private String status;
        private String balance;
        private String flag;
        private String sid;

        public ledgerClass(String date, String transaction, String status, String balance, String flag, String sid) {

            this.date = date;
            this.transaction = transaction;
            this.status = status;
            this.flag = flag;
            this.sid = sid;
            this.balance = balance;
        }


        public String getSid() {
            return sid;
        }

        public String getFlag() {
            return flag;
        }

        public String getBalance() {
            return balance;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public String getTransaction() {
            return transaction;
        }
    }

    private void requestDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ledger_request);

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
                if (amt.length() == 0) {
                    Toast.makeText(getActivity(), "Enter a valid amount.", Toast.LENGTH_SHORT).show();
                } else {
                    double AMT = Double.parseDouble(amt);
                    if (AMT > sellerCb) {
                        Toast.makeText(getActivity(), "Requested amount greater than Seller current balance.", Toast.LENGTH_SHORT).show();
                    } else {
                        sendRequest(amt, description);
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }

    private void sendRequest(final String amt, final String desc) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               /* getSellerCb();
                getSellerLedger();*/

                Long time = System.currentTimeMillis();
                mDatabase.child("Ledger").setValue(time + "");


                notifyUser("Payment Requested", "Requested ID : "+ response, getActivity(), "2", "");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("rid", sid.substring(11));
                parameters.put("sid", id);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
