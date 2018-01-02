package mayank.example.zendor.onClickBuyer;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.onClickBuyer.buyerSale.getSaleDetail;

public class onClickBuyerCard extends AppCompatActivity {


    private ViewPager viewPager;
    private TabLayout header;
    private String buyer_id;
    private TextView saleButton;
    private String commo[];
    private LoadingClass lc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click_buyer_card);

        buyer_id = getIntent().getStringExtra("buyer_id");

        lc = new LoadingClass(this);
        viewPager = findViewById(R.id.viewPager);
        header = findViewById(R.id.header);
        saleButton = findViewById(R.id.saleButton);

        header.setupWithViewPager(viewPager);

        getCommodities();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleDialog();
            }
        });
        viewPager.setOffscreenPageLimit(3);

        if(onClickDispatchedCard.check){
            onClickDispatchedCard.check = false;
        }
        createPager();
    }

    private void createPager(){
        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(buyerDetails.newInstance(buyer_id), "Details");
        adapter.addFrag(buyerSale.newInstance(buyer_id), "Sale");
        adapter.addFrag(buyerLedger.newInstance(buyer_id), "Ledger");
        viewPager.setAdapter(adapter);
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

        public void addFrag(Fragment fragment, String title){
            fragmentArrayList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    private void getCommodities(){
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
        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void addNewSale(final String comm, final String weight, final String rat, final String number, final String dc, final String address, final String baddress){

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ON_CLICK_SALE_BUYER_BUTTON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                getSaleDetail(onClickBuyerCard.this);

                Log.e("erroe", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(onClickBuyerCard.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, onClickBuyerCard.this.getClass().getName(), onClickBuyerCard.this);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss aa", Locale.ENGLISH);
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

                Map<String, String> parameters = new HashMap<>();
                parameters.put("com", comm);
                parameters.put("est", weight);
                parameters.put("rate", rat);
                parameters.put("vn", number);
                parameters.put("dc", dc);
                parameters.put("da", address);
                parameters.put("ba", baddress);
                parameters.put("bid", buyer_id);
                parameters.put("ts1", dateTimeInGMT.format(new Date()));
                return parameters;
            }
        };

        ApplicationQueue.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    private void showSaleDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sale_dialog);
        final Spinner comm = dialog.findViewById(R.id.commodity);
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

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, buyerDetails.comm.split(","));
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
                String da =deliveryAddress.getText().toString();
                String ba = billingAddress.getText().toString();

                addNewSale(commodity, we, r, vn, dc, da, ba);
                dialog.dismiss();
                frequentlyUsedClass.sendOTP(buyerDetails.num[0], "Your Foodmonk verification code is "+"Dispatched"+" . Happy food ordering :)", onClickBuyerCard.this);
                frequentlyUsedClass.sendOTP(dc, "Your Foodmonk verification code is "+ da+" . Happy food ordering :)", onClickBuyerCard.this);

            }
        });

        dialog.show();

    }
}
