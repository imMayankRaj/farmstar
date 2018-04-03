package mayank.example.zendor.onClickSeller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import mayank.example.zendor.onClickBooked.onClickBookedCard;
import mayank.example.zendor.onClickPicked.onClickPickedCard;
import mayank.example.zendor.showErrorDialogs;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.frequentlyUsedClass.notifyUser;


public class sellerDetails extends Fragment {

    private static final String SELLER_ID = "SELLER_ID";
    private String sid;
    private Intent intent;
    private String com, num[];
    private LoadingClass lc;
    private String zid;
    private int counter = 0;

    public sellerDetails() {

    }

    public static sellerDetails newInstance(String sellerId) {
        sellerDetails fragment = new sellerDetails();
        Bundle bundle = new Bundle();
        bundle.putString(SELLER_ID, sellerId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private TextView sellerName;
    private TextView registeredBy;
    private TextView address;
    private TextView bankDetails;
    public static TextView amountDue;
    private LinearLayout commodities;
    private TextView purchaseButton;
    private TextView call;
    private String ew;
    private String rt;
    private String commodity;
    private SharedPreferences sharedPreferences;
    private TextView addRemove;
    private TextView gpsaddress;
    private TextView edit;
    public static TextView click;
    private TextView locateSeller;
    private String geoLocation;
    private ImageView chequeImage;
    private ProgressBar pbar;
    private RelativeLayout otherPic;
    private View parentLayout;
    private DatabaseReference mDatabase;

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
        View view = inflater.inflate(R.layout.fragment_seller_details2, container, false);

        sellerName = view.findViewById(R.id.sellerName);
        registeredBy = view.findViewById(R.id.registererName);
        address = view.findViewById(R.id.address);
        bankDetails = view.findViewById(R.id.bankDetails);
        amountDue = view.findViewById(R.id.amountDue);
        purchaseButton = view.findViewById(R.id.purchase);
        commodities = view.findViewById(R.id.commodities);
        call = view.findViewById(R.id.call);
        addRemove = view.findViewById(R.id.addRemove);
        gpsaddress = view.findViewById(R.id.gpsaddress);
        edit = view.findViewById(R.id.edit);
        click = view.findViewById(R.id.clickIt);
        locateSeller = view.findViewById(R.id.locateSeller);
        chequeImage = view.findViewById(R.id.chequeImage);
        pbar = view.findViewById(R.id.pbar);
        parentLayout = view.findViewById(R.id.parent);
        otherPic = view.findViewById(R.id.otherPic);

        sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);

        String zone_id = sharedPreferences.getString("zid","");
        mDatabase = FirebaseDatabase.getInstance().getReference();


        locateSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (geoLocation.length() != 0) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(geoLocation));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                } else
                    Toast.makeText(getActivity(), "Gps Address Not Available.", Toast.LENGTH_SHORT).show();
            }
        });

        chequeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chequeImage.getDrawable() != null)
                    showBankImage();
            }
        });


        addRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), addRemoveActivity.class);
                intent.putExtra("seller_id", sid.substring(11));
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog(num);
            }
        });


        lc = new LoadingClass(getActivity());

        getSellerDetails();


        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSellerDetails();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), sellerEdit.class);
                intent.putExtra("seller_id", sid.substring(11));
                startActivity(intent);
            }
        });

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (com != null)
                    createPurchaseDialog();
                else
                    Toast.makeText(getActivity(), "No data. Please Reload and try again.", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private void showBankImage() {

        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.on_click_bank_image);

        PhotoView imageView = dialog.findViewById(R.id.imageView);
        ImageView cut = dialog.findViewById(R.id.cut);
        imageView.setImageDrawable(chequeImage.getDrawable());

        cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getSellerDetails() {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLERDATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("asdasd", response);
                            JSONObject json = new JSONObject(response);
                            JSONObject parseJson = json.getJSONObject("values");

                            sellerName.setText(parseJson.getString("name"));
                            address.setText(parseJson.getString("address"));
                            bankDetails.setText(parseJson.getString("an") + "\n" + parseJson.getString("account") + "\n" + parseJson.getString("ifsc"));
                            registeredBy.setText(parseJson.getString("addby"));
                            String COMM = parseJson.getString("commodities");
                            gpsaddress.setText(parseJson.getString("gpsAddress"));
                            com = parseJson.getString("commodities");
                            zid = parseJson.getString("zid");

                            String othermob = parseJson.getString("othermob");
                            num = othermob.split(",");
                            geoLocation = parseJson.getString("gpsAddress");
                            String picpath = parseJson.getString("path");

                            updateLayout(COMM);

                            if (bankDetails.getText().length() == 2) {
                                bankDetails.setTypeface(null, Typeface.BOLD);
                                bankDetails.setText("\b\bBank Details Not Available.");
                            }

                            otherPic.setVisibility(View.VISIBLE);

                            if (picpath.equals("")) {
                                otherPic.setVisibility(View.GONE);
                            }


                            Glide.with(getActivity()).load(URLclass.CHEQUE_PIC_PATH + picpath)
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
                                    .into(chequeImage);

                        } catch (JSONException e) {
                            Log.e("erroe", e + "");
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
                    showError(error, sellerDetails.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("sid", sid);
                Map<String, String> parameters = new HashMap<>();
                parameters.put("sid", sid.substring(11));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void createPurchaseDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.on_purchase_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText est_weight = dialog.findViewById(R.id.estWeight);
        final EditText rate = dialog.findViewById(R.id.rate);
        final Spinner spinner = dialog.findViewById(R.id.commodity);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView book = dialog.findViewById(R.id.book);
        ImageView back = dialog.findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        String commo[] = com.split(",");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, commo);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ew = est_weight.getText().toString();
                rt = rate.getText().toString();
                commodity = spinner.getSelectedItem().toString();
                if (ew.length() == 0 || rt.length() == 0) {
                    Toast.makeText(getActivity(), "All fields are compulsory", Toast.LENGTH_SHORT).show();
                } else {
                    insertPurchaseData();
                    dialog.dismiss();

                    DecimalFormat formatter = new DecimalFormat("#.##");

                    double rate = Double.parseDouble(rt);
                    double ewt = Double.parseDouble(ew);
                    double total = rate * ewt;

                    frequentlyUsedClass.sendOTP(num[0], "प्रिय किसान भाई, फ़ार्मस्टार परिवार में आपका स्वागत है,\n" +
                            "आपके बुकिंग का विवरण:\n" +
                            "अनाज : " + commodity + "\n" +
                            "अनुमानित वजन : " + ew + " किलो\n" +
                            "दर : " + rt + " प्रति किलो\n" +
                            "अनुमानित बिक्री-मूल्य : ₹ " + formatter.format(total) + "\n" +
                            "\n" +
                            "धन्यवाद,\n" +
                            "टीम फ़ार्मस्टार", getActivity());
                }
            }
        });

        dialog.show();
    }

    private void insertPurchaseData() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.INSERT_PURCHASE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                String name = sharedPreferences.getString("name", "");
                String pos = sharedPreferences.getString("position", "");


                mDatabase.child("booking").child(zid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            long time = System.currentTimeMillis();
                            mDatabase.child("booking").child(zid).setValue(time + "");
                            counter++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabase.child("picking").child(zid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            long time = System.currentTimeMillis();
                            mDatabase.child("picking").child(zid).setValue(time + "");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                if(counter == 0){
                    long time = System.currentTimeMillis();
                    mDatabase.child("booking").child(zid).setValue(time + "");
                    mDatabase.child("picking").child(zid).setValue(time + "");
                }


                if (pos.equals("2"))
                    notifyUser("Commodity Booked", "Purchase ID : "+response, getActivity(), "1", "");
                getActivity().finish();
                /*Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();
                Toast.makeText(getActivity(), "Error occured.", Toast.LENGTH_SHORT).show();

                if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerDetails.class.getName(), getActivity());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = sharedPreferences.getString("id", "");

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                Map<String, String> parameters = new HashMap<>();
                parameters.put("seller_id", sid.substring(11));
                parameters.put("booker_id", id);
                parameters.put("zid", zid);
                parameters.put("flag", "bk");
                parameters.put("com_code", commodity);
                parameters.put("rate", rt);
                parameters.put("est_weight", ew);
                parameters.put("booked_ts", dateTimeInGMT.format(new Date()));
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void callDialog(final String a[]) {

        ArrayList<String> numberList = new ArrayList<>(Arrays.asList(a));
        numberList.removeAll(Collections.singleton("null"));

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

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String comm = (String) v.getTag();
            createPurchaseDialog2(comm);
        }
    };

    private void createPurchaseDialog2(final String c) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.purhase_dialog_on_comm_click);
        final EditText est_weight = dialog.findViewById(R.id.estWeight);
        final EditText rate = dialog.findViewById(R.id.rate);
        final TextView comm = dialog.findViewById(R.id.commodity);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView book = dialog.findViewById(R.id.book);
        ImageView back = dialog.findViewById(R.id.back);


        comm.setText(c);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ew = est_weight.getText().toString();
                rt = rate.getText().toString();
                commodity = c;
                if (ew.length() == 0 || rt.length() == 0) {
                    Toast.makeText(getActivity(), "All fields are compulsory", Toast.LENGTH_SHORT).show();
                } else {
                    insertPurchaseData();

                    DecimalFormat formatter = new DecimalFormat("#.##");

                    double rate = Double.parseDouble(rt);
                    double ewt = Double.parseDouble(ew);
                    double total = rate * ewt;

                    dialog.dismiss();
                    frequentlyUsedClass.sendOTP(num[0], "प्रिय किसान भाई, फ़ार्मस्टार परिवार में आपका स्वागत है,\n" +
                            "आपके बुकिंग का विवरण:\n" +
                            "अनाज : " + c + "\n" +
                            "अनुमानित वजन : " + ew + " किलो\n" +
                            "दर : " + rt + " प्रति किलो\n" +
                            "अनुमानित बिक्री-मूल्य : ₹ " + formatter.format(total) + "\n" +
                            "\n" +
                            "धन्यवाद,\n" +
                            "टीम फ़ार्मस्टार", getActivity());
                }
            }
        });

        dialog.show();
    }

}
