package mayank.example.zendor.navigationDrawerOption;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import mayank.example.zendor.onClickBuyer.buyerSale;
import mayank.example.zendor.onClickBuyer.saleAdapter;
import mayank.example.zendor.onClickSeller.OnClickSellerCard;
import mayank.example.zendor.onClickSeller.sellerDetails;
import mayank.example.zendor.onClickSeller.sellerLedger;
import mayank.example.zendor.onClickSeller.sellerPurchases;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class sale extends AppCompatActivity {


    private ArrayList<buyerSale.saleClass> dispatchedList;
    private ArrayList<buyerSale.saleClass> deliveredlist;
    private ArrayList<buyerSale.saleClass> paymentReceivedList;
    private ArrayList<buyerSale.saleClass> cancelledList;
    private ViewPager viewPager;
    public static TabLayout headerSale;
    private Toolbar toolbar;
    LoadingClass lc ;
    public static TextView recreate;
    private SwipeRefreshLayout swipe;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        viewPager = findViewById(R.id.viewPager);
        headerSale = findViewById(R.id.header);
        headerSale.setupWithViewPager(viewPager);
        toolbar = findViewById(R.id.toolbar);
        swipe = findViewById(R.id.swipe);
        recreate = findViewById(R.id.recreate);

        lc = new LoadingClass(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("buyerSales");
        mDatabase.addValueEventListener(valueEventListener);

        recreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        viewPager.setOffscreenPageLimit(3);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.removeEventListener(valueEventListener);
                finish();
            }
        });

        dispatchedList = new ArrayList<>();
        deliveredlist = new ArrayList<>();
        paymentReceivedList = new ArrayList<>();
        cancelledList = new ArrayList<>();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSaleDetail();
                swipe.setRefreshing(true);
            }
        });

        getSaleDetail();

     //   createpager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDatabase.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getSaleDetail();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void createpager(){
        int page = viewPager.getCurrentItem();
        OnClickSellerCard.viewPagerAdapter adapter = new OnClickSellerCard.viewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new saleDispatched(dispatchedList),"Dispatched");
        adapter.addFrag(new saleDelivered(deliveredlist), "Delivered");
        adapter.addFrag(new salePaymentReceived(paymentReceivedList), "Payment Received");
        adapter.addFrag(new saleCancelled(cancelledList), "Cancelled");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(page);
    }

    private void getSaleDetail(){
        lc.showDialog();

        dispatchedList.clear();
        deliveredlist.clear();
        paymentReceivedList.clear();
        cancelledList.clear();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ALL_SALES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray saleArray = json.getJSONArray("values");
                    for (int i =0;i<saleArray.length();i++){
                        JSONObject sale = saleArray.getJSONObject(i);
                        String bname = sale.getString("bname");
                        String sid = sale.getString("saleid");
                        String commodity = sale.getString("commodities");
                        String rate = sale.getString("rate");
                        String flag = sale.getString("flag");
                        String aa = sale.getString("aa");
                        String ts = null;
                        String weight = null;
                        String delWeight = null;
                        switch (flag){

                            case "di":
                                ts = sale.getString("ts1");
                                weight = sale.getString("w1");
                               // delWeight = sale.getString("w2");
                                dispatchedList.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts, delWeight,aa));
                                break;

                            case "de":
                                ts = sale.getString("ts2");
                                weight = sale.getString("w1");
                                delWeight = sale.getString("w2");
                                deliveredlist.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts, delWeight,aa));
                                break;

                            case "ps":
                                ts = sale.getString("ts1");
                                weight = sale.getString("w1");
                                delWeight = sale.getString("w2");
                                paymentReceivedList.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts, delWeight,aa));
                                break;

                            case "cn":
                                ts = sale.getString("canAt");
                                weight = sale.getString("w1");
                                delWeight = sale.getString("w2");
                                cancelledList.add(new buyerSale.saleClass(bname, sid, commodity, weight, rate, flag, ts, delWeight,aa));
                                break;

                        }
                    }
                } catch (JSONException e) {
                    lc.dismissDialog();
                }

                createpager();
                lc.dismissDialog();
                swipe.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipe.setRefreshing(false);

                if (error instanceof TimeoutError) {
                    Toast.makeText(sale.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sale.this.getClass().getName(), sale.this);


                lc.dismissDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(valueEventListener);
    }
}
