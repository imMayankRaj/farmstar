package xendorp1.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.expenseAdapter;
import mayank.example.zendor.expenseClass;
import mayank.example.zendor.navigationDrawerOption.wallet;
import mayank.example.zendor.onClickExecutive.executiveLedger;
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;
import xendorp1.application_classes.AppController;

import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.frequentlyUsedClass.notifyUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ledger_zonal_manager extends Fragment {

    private ListView ZmLedgerView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView ZmNameAndZone;
    private String zmid;
    private TextView request;
    private ImageView addCredit;
    private SharedPreferences sharedPreferences;
    private LoadingClass lc;
    private String CB;
    private ImageView paymentRequest;
    private ArrayList<expenseClass> expenseList;
    private String pos;
    private DatabaseReference mDatabase;
    private int count = 0;
    private String nameAndZone;


    public ledger_zonal_manager() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            zmid = bundle.getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger_zonal_manager, container, false);

        ZmLedgerView = view.findViewById(R.id.ledgerView);
        cb = view.findViewById(R.id.cb);
        ZmNameAndZone = view.findViewById(R.id.sellerNameAndZone);
        ledgerList = new ArrayList<>();
        request = view.findViewById(R.id.request);
        addCredit = view.findViewById(R.id.addCredit);
        paymentRequest = view.findViewById(R.id.paymentRequest);


        lc = new LoadingClass(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("details", MODE_PRIVATE);

        pos = sharedPreferences.getString("position", "");

        if (pos.equals("2") || pos.equals("1"))
            paymentRequest.setVisibility(View.GONE);

        getZmCb();
        getZmLedger();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase = mDatabase.child("users").child(zmid);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Long time = System.currentTimeMillis();
                    mDatabase.setValue(time + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.addValueEventListener(valueEventListener);


        ZmLedgerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String flag = ledgerList.get(position).getFlag();
                if (flag.equals("1")) {
                    getSellerPaymentDetails(ledgerList.get(position).getSid());
                }
            }
        });

        expenseList = new ArrayList<>();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (details_zonal_manager.status == 1) {
                    double cb = Double.parseDouble(CB);
                    if (cb <= 0)
                        Toast.makeText(getActivity(), "Not Enough Credits.", Toast.LENGTH_SHORT).show();
                    else
                        requestDialog();
                } else
                    Toast.makeText(getActivity(), "Zonal Manager Is Disabled.", Toast.LENGTH_SHORT).show();
            }
        });

        addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (details_zonal_manager.status == 1) {
                    addCreditDialog();
                } else
                    Toast.makeText(getActivity(), "Zonal Manager Is Disabled.", Toast.LENGTH_SHORT).show();

            }
        });

        paymentRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpensesDetails();
            }
        });

        return view;
    }

    public void removeListener() {
        if (valueEventListener != null)
            mDatabase.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() && (count != 0)) {
                getZmCb();
                getZmLedger();
            }
            count++;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getSellerPaymentDetails(final String sid) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_LEDGER_SELLER_PURCHASE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lc.dismissDialog();
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
        final Dialog dialog = new Dialog(getActivity());
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

    private void getExpensesDetails() {
        expenseList.clear();
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.EXPENSES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray details = jsonObject.getJSONArray("details");

                    for (int i = 0; i < details.length(); i++) {
                        String rid = details.getJSONObject(i).getString("rid");
                        String t_details = details.getJSONObject(i).getString("details");
                        String amount = details.getJSONObject(i).getString("amount");
                        expenseList.add(new expenseClass(amount, rid, t_details));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                lc.dismissDialog();
                showExpenseDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, executiveLedger.class.getName(), getActivity());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", zmid);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void showExpenseDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Expenses : ");


        final ArrayAdapter<expenseClass> arrayAdapter = new expenseAdapter(getActivity(), expenseList);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createExpenseOnClickDialog(dialog, which);
            }
        });
        builderSingle.create();
        builderSingle.show();

    }

    private void createExpenseOnClickDialog(final DialogInterface d, int position) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expense_on_click);

        TextView accept = dialog.findViewById(R.id.accept);
        TextView reject = dialog.findViewById(R.id.reject);
        ImageView back = dialog.findViewById(R.id.back);
        final EditText remarks = dialog.findViewById(R.id.remarks);


        TextView det = dialog.findViewById(R.id.details);
        TextView amt = dialog.findViewById(R.id.amount);


        final String rid = expenseList.get(position).getRid();
        final String amount = expenseList.get(position).getAmount();
        String details = expenseList.get(position).getDetails();

        det.setText(details);
        amt.setText('\u20B9' + amount);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rem = remarks.getText().toString();
                lc.showDialog();
                updateExpenseStatus("a", rid, amount, rem);
                dialog.dismiss();
                d.dismiss();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rem = remarks.getText().toString();
                if (rem.length() == 0) {
                    Toast.makeText(getActivity(), "Please specify reasons.", Toast.LENGTH_SHORT).show();
                } else {
                    lc.showDialog();
                    updateExpenseStatus("j", rid, amount, rem);
                    dialog.dismiss();
                    d.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void updateExpenseStatus(final String state, final String id, final String amount, final String rem) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.EXPENSES_ON_CLICK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lc.dismissDialog();
                        String reqStatus;
                        if (state.equals("a"))
                            reqStatus = "Expense Approved";
                        else
                            reqStatus = "Expense Rejected";

                        String message = nameAndZone + " Request ID : " + id;

                        notifyUser(reqStatus, message, getActivity(), "3", zmid);

                        Long time = System.currentTimeMillis();
                        mDatabase.child("Ledger").setValue(time + "");


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, executiveLedger.class.getName(), getActivity());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String uid = sharedPreferences.getString("id", "");

                Map<String, String> params = new HashMap<>();
                params.put("status", state);
                params.put("rid", id);
                params.put("amount", amount);
                params.put("id", zmid);
                params.put("uid", uid);
                params.put("rem", rem);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }


    private void getZmCb() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    CB = json.getString("current_balance");
                    cb.setText('\u20B9' + CB);
                } catch (JSONException e) {
                    e.printStackTrace();
                    lc.dismissDialog();
                }
                lc.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error", error + "");
                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", zmid);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getZmLedger() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ZM_LEDGER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ledgerList.clear();
                Log.e("zm ledger", response);
                try {
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
                    ZmNameAndZone.setText(name + " - " + szone);
                    nameAndZone = name + " (" + szone + ")";

                } catch (JSONException e) {
                    Log.e("error", e + "");
                    e.printStackTrace();
                    lc.dismissDialog();
                }

                ledgerAdapter adapter = new ledgerAdapter(getActivity(), 0, ledgerList);
                ZmLedgerView.setAdapter(adapter);
                lc.dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", zmid);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void addCreditDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_credit_dialog);

        ImageView back = dialog.findViewById(R.id.back);
        final EditText amount = dialog.findViewById(R.id.amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView request = dialog.findViewById(R.id.pay);
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
                    Toast.makeText(getActivity(), "All Fields Are Compulsory.", Toast.LENGTH_SHORT).show();
                } else {
                    addCredit(amt, description);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
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
                if (amt.length() == 0 || description.length() == 0) {
                    Toast.makeText(getActivity(), "All fields are compulsory.", Toast.LENGTH_SHORT).show();
                } else {
                    double a = Double.parseDouble(amt);
                    double cb = Double.parseDouble(CB);

                    if (a <= cb) {
                        {
                            if (pos.equals("0")) {
                                sendRequestAdmin(amt, description);
                            } else
                                sendRequest(amt, description);
                            dialog.dismiss();
                        }
                    } else
                        Toast.makeText(getActivity(), "Not Enough Credits.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialog.show();
    }

    private void sendRequest(final String amt, final String desc) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Long time = System.currentTimeMillis();
                mDatabase.child("Ledger").setValue(time + "");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", zmid);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void addCredit(final String amt, final String desc) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_CREDIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String name = sharedPreferences.getString("name", "");
                String pos;
                if(sharedPreferences.getString("position", "").equals("0")){
                    pos = "Admin";
                }else
                    pos = "Zonal Manager";

                String msg = amt + " Rs. by "+ name + "(" + pos + ")";
                notifyUser("Amount Credited", msg, getActivity(), "4", zmid);

                Long time = System.currentTimeMillis();
                mDatabase.child("Ledger").setValue(time + "");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Some Error Occured.", Toast.LENGTH_SHORT).show();
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, ledger_zonal_manager.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("sid", id);
                parameters.put("rid", zmid);
                parameters.put("amt", amt);
                parameters.put("des", desc);


                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void sendRequestAdmin(final String amt, final String desc) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADMIN_DIRECT_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Long time = System.currentTimeMillis();
                        mDatabase.child("Ledger").setValue(time + "");


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, executiveLedger.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", zmid);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                parameters.put("rec", id);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
