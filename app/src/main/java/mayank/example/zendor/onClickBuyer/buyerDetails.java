package mayank.example.zendor.onClickBuyer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.MainActivity;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.frequentlyUsedClass;
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static android.content.Context.MODE_PRIVATE;
import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.onClickBuyer.buyerSale.getSaleDetail;


public class buyerDetails extends Fragment {

    public static String BUYER_ID = "buyer_id";
    private String buyer_id;
    private TextView buyer_name;
    private TextView call;
    private TextView companyName;
    private TextView registeredBy;
    private TextView address;
    public static TextView amountDue;
    private TextView gstNumber;
    private TextView sale;
    private String number, othermob;
    private LinearLayout commodities;
    public static String num[];
    private Intent intent;
    public static String comm;
    private LoadingClass lc;
    private ImageView buyerImage;
    private String picpath;
    private ProgressBar pbar;
    private TextView edit;
    private String GSTNUMBER;
    private SharedPreferences sharedPreferences;


    public buyerDetails() {
        // Required empty public constructor
    }


    public static buyerDetails newInstance(String buyer_id) {
        buyerDetails fragment = new buyerDetails();
        Bundle bundle = new Bundle();
        bundle.putString(BUYER_ID, buyer_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buyer_id = getArguments().getString(BUYER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_details, container, false);
        buyer_name = view.findViewById(R.id.buyerName);
        call = view.findViewById(R.id.callBuyer);
        companyName = view.findViewById(R.id.companyName);
        registeredBy = view.findViewById(R.id.registererName);
        address = view.findViewById(R.id.address);
        amountDue = view.findViewById(R.id.amountDue);
        gstNumber = view.findViewById(R.id.gstNumber);
        commodities = view.findViewById(R.id.commodities);
        sale = view.findViewById(R.id.sale);
        buyerImage = view.findViewById(R.id.buyerImage);
        pbar = view.findViewById(R.id.pbar);
        edit = view.findViewById(R.id.buyerEdit);

        sharedPreferences = getActivity().getSharedPreferences("details", MODE_PRIVATE);


        lc = new LoadingClass(getActivity());
        getBuyerDetails();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBuyerDialog();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(num);
            }
        });

        return view;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String comm = (String) v.getTag();
            showSaleDialog(comm);
        }
    };

    private void updateLayout(String comm) {
        commodities.removeAllViews();
        String COMM[] = comm.split(",");
        for (int i = 0; i < COMM.length; i++) {
            TextView textView = new TextView(getActivity());
            textView.setPadding(10, 10, 10, 10);
            textView.setTextSize(18);
            textView.setTag(COMM[i]);
            textView.setBackgroundResource(R.drawable.border);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.setMargins(0, 5, 0, 5);
            textView.setLayoutParams(params);
            textView.setText(COMM[i]);
            textView.setOnClickListener(listener);
            commodities.addView(textView);
        }
    }


    private void showSaleDialog(final String COMM){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.buyer_sale_purchase_dialog);
        final TextView comm = dialog.findViewById(R.id.commodity);
        final EditText weight = dialog.findViewById(R.id.weight);
        final EditText rate = dialog.findViewById(R.id.rate);
        final EditText vnumber = dialog.findViewById(R.id.vNumber);
        final EditText driverContact = dialog.findViewById(R.id.driverContact);
        final EditText deliveryAddress = dialog.findViewById(R.id.deliveryAddress);
        final EditText billingAddress = dialog.findViewById(R.id.billingAddress);
        ImageView back = dialog.findViewById(R.id.back);

        comm.setText(COMM);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView save = dialog.findViewById(R.id.save);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String we = weight.getText().toString();
                String r = rate.getText().toString();
                String vn = vnumber.getText().toString();
                String dc = driverContact.getText().toString();
                String da =deliveryAddress.getText().toString();
                String ba = billingAddress.getText().toString();

                if ( we.length() == 0 || r.length() == 0 || vn.length() == 0 || dc.length() == 0 || da.length() == 0 || ba.length() == 0) {
                    Toast.makeText(getActivity(), "Some Fields Are Left Empty.", Toast.LENGTH_SHORT).show();
                }else {
                    addNewSale(COMM, we, r, vn, dc, da, ba);
                    dialog.dismiss();
                    frequentlyUsedClass.sendOTP(buyerDetails.num[0], "Your Foodmonk verification code is " + "Dispatched" + " . Happy food ordering :)", getActivity());
                    frequentlyUsedClass.sendOTP(dc, "Your Foodmonk verification code is " + da + " . Happy food ordering :)", getActivity());
                }
            }
        });

        dialog.show();

    }

    private void addNewSale(final String comm, final String weight, final String rat, final String number, final String dc, final String address, final String baddress){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_SALE_BUYER_BUTTON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                getSaleDetail(getActivity());
                buyerLedger.click.performClick();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, getActivity().getClass().getName(), getActivity());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                String id = sharedPreferences.getString("id", "");


                Map<String, String> parameters = new HashMap<>();
                parameters.put("com", comm);
                parameters.put("est", weight);
                parameters.put("rate", rat);
                parameters.put("vn", number);
                parameters.put("dc", dc);
                parameters.put("da", address);
                parameters.put("ba", baddress);
                parameters.put("bid", buyer_id);
                parameters.put("bookedBy", id);
                parameters.put("ts1", dateTimeInGMT.format(new Date()));
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getBuyerDetails() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.BUYER_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject json = jsonObject.getJSONObject("values");
                    buyer_name.setText(json.getString("name"));
                    companyName.setText(json.getString("company_name"));
                    registeredBy.setText(json.getString("booker"));
                    address.setText(json.getString("address"));
                    number = json.getString("mob");
                    othermob = json.getString("othermob");
                    String COMM = (json.getString("commodities"));
                    picpath = json.getString("picpath");

                    GSTNUMBER = json.getString("gst_number");
                    if(!GSTNUMBER.equals("null"))
                        gstNumber.setText(GSTNUMBER);
                    else
                        gstNumber.setText("");

                    comm = json.getString("commodities");
                    number = number + "," + othermob;
                    num = number.split(",");

                    updateLayout(COMM);

                    Glide.with(getActivity()).load(URLclass.COMMODITY_PIC_PATH + picpath)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    pbar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    pbar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(buyerImage);


                } catch (JSONException e) {
                    lc.dismissDialog();
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
                    showError(error, buyerDetails.class.getName(), getActivity());


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("buyer_id", buyer_id);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

        Set<String> s = new HashSet<>();
        s.addAll(numberList);
        numberList.clear();
        numberList.addAll(s);

        final String[] b = numberList.toArray(new String[numberList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Call :")
                .setItems(b, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String number = b[which];
                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91" + number));

                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                                    2);
                            return;
                        } else
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


    private void editBuyerDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_buyer_details_dialog);
        final EditText gst = dialog.findViewById(R.id.gst);
        ImageView addnum = dialog.findViewById(R.id.add_num);
        final TextView update = dialog.findViewById(R.id.update);
        TextView cancel = dialog.findViewById(R.id.cancel);
        final LinearLayout linearLayout = dialog.findViewById(R.id.phno_values);

        if(!GSTNUMBER.equals("null"))
            gst.setText(GSTNUMBER);
        else
            gst.setText("");

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(num));
        Set<String> s = new HashSet<>();
        s.addAll(numberList);
        numberList.clear();
        numberList.addAll(s);

        final String[] NUM = numberList.toArray(new String[numberList.size()]);



        for (int i = 0; i < NUM.length; i++) {
            if(!NUM[i].equals("null")) {
                final View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.phone_number_inputter1, null);
                linearLayout.addView(view1);
                ImageView removenum = view1.findViewById(R.id.remove);
                EditText numedit = view1.findViewById(R.id.numedit);
                numedit.setText(NUM[i]);
                removenum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.removeView(view1);
                    }
                });
            }
        }

        addnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.phone_number_inputter1, null);
                linearLayout.addView(view1);
                ImageView removenum = view1.findViewById(R.id.remove);
                removenum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.removeView(view1);
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = true;
                String GST = gst.getText().toString();
                String number = "";
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    EditText num = linearLayout.getChildAt(i).findViewById(R.id.numedit);
                    String NUM = num.getText().toString();
                    if (NUM.length() == 10)
                        number = number.concat(NUM + ",");
                    else {
                        b = false;
                        Toast.makeText(getActivity(), "Enter a valid number.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(b)
                    updateData(GST, number);
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void updateData(final String gstNumber, final String number){
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.UPDATE_BUYER_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                getBuyerDetails();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                lc.dismissDialog();
                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, buyerDetails.class.getName(), getActivity());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("buyer_id", buyer_id);
                params.put("gstNumber", gstNumber);
                params.put("number", number);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
