package mayank.example.zendor.onClickExecutive;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import mayank.example.zendor.onClickSeller.ledgerAdapter;
import mayank.example.zendor.onClickSeller.sellerLedger;

import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;


public class executiveLedger extends Fragment {


    public static String EXE_ID = "exec_id";
    private String eid;
    private ListView sellerLedgerView;
    private TextView cb;
    private ArrayList<sellerLedger.ledgerClass> ledgerList;
    private TextView sellerNameAndZone;
    private SharedPreferences sharedPreferences;
    private TextView request;
    private ImageView addCredit;
    LoadingClass lc;
    private double ucb;
    private String pos;
    private double ecb;


    public executiveLedger() {
        // Required empty public constructor
    }

    public static executiveLedger newInstance(String exec_id) {
        executiveLedger fragment = new executiveLedger();
        Bundle bundle = new Bundle();
        bundle.putString(EXE_ID, exec_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eid = getArguments().getString(EXE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        sellerLedgerView = view.findViewById(R.id.ledgerView);
        cb = view.findViewById(R.id.cb);
        sellerNameAndZone = view.findViewById(R.id.sellerNameAndZone);
        ledgerList = new ArrayList<>();
        request = view.findViewById(R.id.request);
        addCredit = view.findViewById(R.id.addCredit);

        lc = new LoadingClass(getActivity());

        sharedPreferences = getActivity().getSharedPreferences("details", MODE_PRIVATE);

        pos = sharedPreferences.getString("position", "");


        getSellerCb();
        getZmLedger();
        getUserCb();


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClickExecutiveCard.status.equals("1"))
                    requestDialog();
                else
                    Toast.makeText(getActivity(), "Executive is disabled", Toast.LENGTH_SHORT).show();

            }
        });

        addCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClickExecutiveCard.status.equals("1")) {
                    addCreditDialog();
                } else
                    Toast.makeText(getActivity(), "Executive is disabled", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

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

                lc.dismissDialog();
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
                parameters.put("id", id);
                return parameters;
            }
        };
        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);

    }


    private void getSellerCb() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_CB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String CB = json.getString("current_balance");
                    cb.setText(CB);
                    ecb = Double.parseDouble(CB);
                } catch (JSONException e) {
                    lc.dismissDialog();
                }
                lc.dismissDialog();

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
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", eid);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
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
                        ledgerList.add(new sellerLedger.ledgerClass(date, pid, cd, '\u20B9' + balance));
                    }

                    JSONObject details = json.getJSONObject("details");
                    String name = details.getString("zm");
                    String szone = details.getString("zm_zone");
                    sellerNameAndZone.setText(name + " - " + szone);
                } catch (JSONException e) {
                    Log.e("error", e + "");
                    e.printStackTrace();
                }

                ledgerAdapter adapter = new ledgerAdapter(getActivity(), 0, ledgerList);
                sellerLedgerView.setAdapter(adapter);
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
                    showError(error, executiveLedger.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("id", eid);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
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
                Double AMT = Double.parseDouble(amt);
                if ((AMT >= ucb) && !pos.equals("0"))
                    Toast.makeText(getActivity(), "Not Enough Credits To Be Added In Executive's Current Balance.", Toast.LENGTH_LONG).show();
                else
                    addCredit(amt, description);
                dialog.dismiss();
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
                int a = Integer.parseInt(amt);
                if (a <= ecb) {
                    if (pos.equals("0")) {
                        sendRequestAdmin(amt, description);
                    } else
                        sendRequest(amt, description);
                } else
                    Toast.makeText(getActivity(), "Executive doesn't have enough credits to be debited.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendRequest(final String amt, final String desc) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                getSellerCb();
                getZmLedger();
                getUserCb();
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
                parameters.put("id", eid);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addCredit(final String amt, final String desc) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_CREDIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                getSellerCb();
                getZmLedger();
                getUserCb();
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
                parameters.put("sid", id);
                parameters.put("rid", eid);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void sendRequestAdmin(final String amt, final String desc) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADMIN_DIRECT_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        getSellerCb();
                        getZmLedger();
                        getUserCb();
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
                parameters.put("id", eid);
                parameters.put("amt", amt);
                parameters.put("des", desc);
                parameters.put("rec", id);
                return parameters;
            }
        };
        ApplicationQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
