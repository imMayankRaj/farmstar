package mayank.example.zendor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayank.example.zendor.R;
import mayank.example.zendor.landingPageFragment.booked;
import mayank.example.zendor.landingPageFragment.picked;
import mayank.example.zendor.landingPageFragment.sellerAdapter;
import mayank.example.zendor.landingPageFragment.sellerClass;
import mayank.example.zendor.landingPageFragment.sellers;
import mayank.example.zendor.navigationDrawerOption.addCommodities;
import mayank.example.zendor.navigationDrawerOption.allPurchases;
import mayank.example.zendor.navigationDrawerOption.paymentRequest;
import mayank.example.zendor.navigationDrawerOption.sale;
import mayank.example.zendor.navigationDrawerOption.wallet;
import xendorp1.fragments.buyers;
import xendorp1.fragments.executive_zonal_manager;
import xendorp1.fragments.executives;
import xendorp1.fragments.workforce;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private ImageView pic;
    private TextView Name;
    private TextView position;
    private SharedPreferences sharedPreferences;
    private String zone;
    public static TabLayout tabLayout;
    private TextView contact;
    public static EditText searchSeller;
    public static ArrayList<searchClass> searchList;
    private ArrayList<sellerClass> sellerList;
    public static ArrayList<searchClass> searchList1;
    private ImageView cut;
    private searchSellerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);
        searchSeller = findViewById(R.id.search);
        contact = findViewById(R.id.contact);

        tabLayout.setupWithViewPager(viewPager);
        pic = findViewById(R.id.imageView);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        position = header.findViewById(R.id.position);
        Name = header.findViewById(R.id.name);
        ImageView imageView = header.findViewById(R.id.imageView);
        cut = findViewById(R.id.cut);

        searchSeller.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        searchList = new ArrayList<>();
        sellerList = new ArrayList<>();
        searchList1 = new ArrayList<>();


        viewPager.setOffscreenPageLimit(3);

        searchList = new ArrayList<>();

        getSearchData();
        getSearchData2();

        createPager();
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        final String pos = sharedPreferences.getString("position", "");
        String zone = sharedPreferences.getString("zone", "");
        final String name = sharedPreferences.getString("name", "");
        String path = sharedPreferences.getString("path", "");

        Glide.with(this).load(URLclass.PROFILEPIC + path).into(imageView);


        cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerAdapter adapter = new sellerAdapter(MainActivity.this, sellers.arrayList);
                sellers.recyclerView.setAdapter(adapter);
                searchSeller.setText("");
                cut.setImageDrawable(null);
                cut.setBackgroundResource(R.drawable.ic_search_black_24dp);
            }
        });


        searchSeller.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() != 0) {
                    cut.setImageDrawable(null);
                    cut.setBackgroundResource(R.drawable.ic_cancel_black_24dp);

                    ArrayList<sellerClass> sellerList = new ArrayList<>();

                    for (int i = 0; i < sellers.arrayList.size(); i++) {
                        String num[] = sellers.arrayList.get(i).getNumber().split(",");
                        for (int k = 0; k < num.length; k++) {
                            if (num[k].contains(s)) {
                                sellerList.add(sellers.arrayList.get(i));
                            }
                        }
                    }

                    sellerAdapter adapter = new sellerAdapter(MainActivity.this, sellerList);
                    sellers.recyclerView.setAdapter(adapter);
                } else {
                    cut.setImageDrawable(null);
                    cut.setBackgroundResource(R.drawable.ic_search_black_24dp);
                }


            }
        });


        switch (pos) {
            case "0":
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.admin_menu);
                position.setText("Admin " + zone);
                Name.setText(name);
                break;
            case "1":
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.zonal_manager_menu);
                position.setText("Zonal Manager " + zone);
                Name.setText(name);
                break;
            case "2":
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.executives_menu);
                position.setText("Executive " + zone);
                Name.setText(name);
                break;
        }

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.contact_dialog);
                TextView c_zonal = dialog.findViewById(R.id.contactZonal);
                TextView c_zendor = dialog.findViewById(R.id.contactZendor);
                TextView mail = dialog.findViewById(R.id.mail);
                if (!pos.equals("2"))
                    c_zonal.setVisibility(View.GONE);

                c_zendor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+918874444111"));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                    2);
                            return;
                        } else
                            startActivity(intent);
                        dialog.dismiss();
                    }
                });

                mail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "abhikuiitk@gmail.com", null));
                        startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                        dialog.dismiss();

                    }
                });


                dialog.show();
                drawer.closeDrawer(GravityCompat.START);

            }
        });

        navigationView.setNavigationItemSelectedListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    0);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout) {
            sharedPreferences.edit().clear().apply();
            sharedPreferences.edit().commit();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass;
        int id = item.getItemId();

        if (id == R.id.all_purchase) {
            startActivity(new Intent(this, allPurchases.class));
        } else if (id == R.id.workforce) {
            fragmentClass = workforce.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.commodities) {
            startActivity(new Intent(this, addCommodities.class));
        } else if (id == R.id.wallet) {
            startActivity(new Intent(this, wallet.class));
        } else if (id == R.id.buyers) {
            fragmentClass = buyers.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.sale) {

            startActivity(new Intent(this, sale.class));
        } else if (id == R.id.paymentRequest) {
            startActivity(new Intent(this, paymentRequest.class));
        } else if (id == R.id.executives) {
            String id1 = sharedPreferences.getString("id", "");
            String zid = sharedPreferences.getString("zid", "");
            Bundle bundle = new Bundle();
            bundle.putString("id", id1);
            fragmentClass = executives.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.drawer_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void createPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new sellers(), "Sellers");
        adapter.addFrag(new booked(), "Booked");
        adapter.addFrag(new picked(), "Picked");

        viewPager.setAdapter(adapter);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+918874444111"));
                startActivity(intent);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getSearchData() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLclass.GET_SELLER_DROPDOWN_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray jArray = json.getJSONArray("seller_details");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject details = jArray.getJSONObject(i);
                        String name = details.getString("name");
                        String number = details.getString("number");
                        String id = details.getString("seller_id");
                        searchList.add(new searchClass(name, number, id));
                    }

                } catch (JSONException e) {
                    Log.e("adapter error", e + "");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, MainActivity.this.getClass().getName(), MainActivity.this);

            }

        });

        ApplicationQueue.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getSearchData2() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLclass.GET_SELLER_DROPDOWN_DETAILS,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray jArray = json.getJSONArray("seller_details");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject details = jArray.getJSONObject(i);
                        String name = details.getString("name");
                        String number = details.getString("number");
                        String id = details.getString("seller_id");
                        searchList1.add(new searchClass(name, number, id));
                    }

                } catch (JSONException e) {
                    Log.e("adapter error", e + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, MainActivity.this.getClass().getName(), MainActivity.this);

            }
        });

        ApplicationQueue.getInstance(this).addToRequestQueue(stringRequest);

    }


    public static class searchClass {

        private String name;
        private String number;
        private String id;

        public searchClass(String name, String number, String id) {
            this.name = name;
            this.number = number;
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }

    public static void showError(final VolleyError error, final String string, final Activity activity) {
        if (error.getClass() == TimeoutError.class) {
            Toast.makeText(activity, "Time Out. Please Reload.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setTitle("Error.");
            builder.setMessage("Some Error Occured. Please Report.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", "bugs.codebuckets@gmail.com", null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report Error.");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, error + "\nMessage : "+ error.getMessage()+"\n" + string);
                            activity.startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }


}
