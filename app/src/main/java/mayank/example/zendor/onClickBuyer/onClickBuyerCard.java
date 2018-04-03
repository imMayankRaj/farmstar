package mayank.example.zendor.onClickBuyer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.frequentlyUsedClass;
import mayank.example.zendor.onClickSeller.OnClickSellerCard;
import mayank.example.zendor.onClickSeller.sellerDetails;
import mayank.example.zendor.onClickSeller.sellerLedger;
import mayank.example.zendor.onClickSeller.sellerPurchases;
import xendorp1.adapters.zone_card_spinner_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zone_card;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.onClickBuyer.buyerDetails.buyerName;
import static mayank.example.zendor.onClickBuyer.buyerDetails.buyerNumber;
import static mayank.example.zendor.onClickBuyer.buyerSale.getSaleDetail;

public class onClickBuyerCard extends AppCompatActivity {


    private ViewPager viewPager;
    public static TabLayout header;
    private String buyer_id;
    private TextView saleButton;
    private String commo[];
    private LoadingClass lc;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private List<zone_card> zonelist;
    private DatabaseReference mDatabase;
    private buyerLedger instance;
    private buyerSale instanceSale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_buyer_card);

        buyer_id = getIntent().getStringExtra("buyer_id");

        lc = new LoadingClass(this);
        viewPager = findViewById(R.id.viewPager);
        header = findViewById(R.id.header);
        saleButton = findViewById(R.id.saleButton);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        zonelist = new ArrayList<>();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.removeListener();
                instanceSale.removeListener();
                finish();
            }
        });

        header.setupWithViewPager(viewPager);
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        getCommodities();
        getZones();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDialog();
            }
        });
        viewPager.setOffscreenPageLimit(3);

        if (onClickDispatchedCard.check) {
            onClickDispatchedCard.check = false;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("buyerSales").child(buyer_id);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    long time = System.currentTimeMillis();
                    mDatabase.setValue(time+"");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        createPager();
    }

    private void createPager() {
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        instance = buyerLedger.newInstance(buyer_id);
        instanceSale = buyerSale.newInstance(buyer_id);
        adapter.addFrag(buyerDetails.newInstance(buyer_id), "Details");
        adapter.addFrag(instanceSale, "Sale");
        adapter.addFrag(instance, "Ledger");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        instance.removeListener();
        instanceSale.removeListener();
    }

    public class viewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();

        public viewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            fragmentArrayList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    private void getCommodities() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLclass.GET_COMMODITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                commo = response.split(",");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickBuyerCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickBuyerCard.this.getClass().getName(), onClickBuyerCard.this);


            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void addNewSale(final String comm, final String weight, final String rat, final String number, final String dc, final String address, final String baddress, final String zone) {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_SALE_BUYER_BUTTON,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                frequentlyUsedClass.sendOTPE(buyerNumber, "This is to inform that\n" +
                        "Commodity : "+comm.trim()+"\n" +
                        "Rate : "+rat.trim()+" per kg\n" +
                        "Vehicle No : "+number.trim()+"\n" +
                        "Driver : "+dc.trim()+"\n" +
                        "Has been despatched by Farmstar from "+baddress.trim()+" to " + address.trim(), onClickBuyerCard.this);

                long time = System.currentTimeMillis();
                mDatabase.setValue(time+"");

                lc.dismissDialog();
              //  getSaleDetail(onClickBuyerCard.this);
              //  buyerLedger.click.performClick();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickBuyerCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickBuyerCard.this.getClass().getName(), onClickBuyerCard.this);


            }
        }) {
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
                parameters.put("zone", zone);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void getZones() {
        lc.showDialog();
        zonelist.clear();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_ZONES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    zonelist.add(new zone_card());
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        zone_card zone_card = new zone_card();
                        zone_card.setZone_name(jsonObject.getString("zname"));
                        zone_card.setZone_id(jsonObject.getString("zid"));
                        zonelist.add(zone_card);
                    }

                    lc.dismissDialog();
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
                    Toast.makeText(onClickBuyerCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), onClickBuyerCard.this);


            }
        });
        AppController.getInstance().addToRequestQueue(strReq, "getzones");
    }


    private void showSaleDialog() {

        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sale_dialog);
        final Spinner comm = dialog.findViewById(R.id.commodity);
        final Spinner zone = dialog.findViewById(R.id.zone);
        final EditText weight = dialog.findViewById(R.id.weight);
        final EditText rate = dialog.findViewById(R.id.rate);
        final EditText vnumber = dialog.findViewById(R.id.vNumber);
        final EditText driverContact = dialog.findViewById(R.id.driverContact);
        final EditText deliveryAddress = dialog.findViewById(R.id.deliveryAddress);
        final EditText billingAddress = dialog.findViewById(R.id.billingAddress);
        ImageView back = dialog.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView save = dialog.findViewById(R.id.save);

        zone_card_spinner_adapter adapter = new zone_card_spinner_adapter(onClickBuyerCard.this, R.layout.spinner_zone, zonelist, getLayoutInflater());
        zone.setAdapter(adapter);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buyerDetails.comm.split(","));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        comm.setAdapter(spinnerArrayAdapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commodity = comm.getSelectedItem().toString();
                String we = weight.getText().toString();
                String r = rate.getText().toString();
                String vn = vnumber.getText().toString();
                String dc = driverContact.getText().toString();
                String da = deliveryAddress.getText().toString();
                String ba = billingAddress.getText().toString();
                String zo = zonelist.get(zone.getSelectedItemPosition()).getZone_name();

                if (commodity.length() == 0 || we.length() == 0 || r.length() == 0 || vn.length() == 0 || dc.length() == 0 || da.length() == 0 || ba.length() == 0 || zone.getSelectedItemPosition() == 0) {
                    Toast.makeText(onClickBuyerCard.this, "Some Fields Are Left Empty.", Toast.LENGTH_SHORT).show();
                }else if(dc.length() <10) {
                    Toast.makeText(onClickBuyerCard.this, "Enter valid driver contact number.", Toast.LENGTH_SHORT).show();
                }else
                {
                    addNewSale(commodity, we, r, vn, dc, da, ba, zo);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instanceSale.removeListener();
        instance.removeListener();
    }
}
