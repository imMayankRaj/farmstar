package mayank.example.zendor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import mayank.example.zendor.onClickBuyer.onClickBuyerCard;
import mayank.example.zendor.onClickSeller.sellerLedger;
import xendorp1.adapters.zone_card_spinner_adapter;
import xendorp1.application_classes.AppConfig;
import xendorp1.application_classes.AppController;
import xendorp1.cards.zone_card;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_LONG;
import static mayank.example.zendor.MainActivity.showError;

public class sellerDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText name;
    private EditText address;
    private EditText phone;
    private TextView proceed;
    private EditText pincode;
    public static RequestQueue requestQueue;
    private apiConnect connect;
    private View view;
    private Spinner zone;
    private SharedPreferences sharedPreferences;
    private static final String userName = "2000148140";
    private static final String password = "hvDjCw";
    private String zone_id = "";
    private boolean check = true;
    private List<zone_card> zonelist;
    private LinearLayout zoneLayout;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mAddressOutput;
    private TextView locate;
    private Intent intent;
    private boolean isCheck = false;
    private TextView gpsAddress;
    private ProgressBar load;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;
    private GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 10;
    private LoadingClass lc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        proceed = findViewById(R.id.proceed);
        view = findViewById(R.id.sellerView);
        pincode = findViewById(R.id.pincode);
        zone = findViewById(R.id.zone);
        zoneLayout = findViewById(R.id.zonelayout);
        locate = findViewById(R.id.locate);
        gpsAddress = findViewById(R.id.gpsAddress);
        load = findViewById(R.id.load);

        zonelist = new ArrayList<>();


        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        lc = new LoadingClass(this);

        connect = new apiConnect(sellerDetailActivity.this, "pushSellerData");
        requestQueue = connect.getRequestQueue();

        googleApiClient = new GoogleApiClient.Builder(sellerDetailActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(sellerDetailActivity.this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String pos = sharedPreferences.getString("position", "");



        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                    startIntentService();

                }
            }
        };


        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck = true;
                GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                int responseCode = apiAvailability.isGooglePlayServicesAvailable(sellerDetailActivity.this);
                if (apiAvailability.isGooglePlayServicesAvailable(sellerDetailActivity.this) == 0) {
                    getAddressFromGps();
                } else {
                    Dialog dialog = apiAvailability.getErrorDialog(sellerDetailActivity.this, responseCode, 0);
                    dialog.setCancelable(false);
                    dialog.show();
                }

            }
        });


        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    checkNumber(s.toString());
                }
            }
        });

        if (pos.equals("0")) {
            zoneLayout.setVisibility(View.VISIBLE);
           lc.showDialog();
            getZones();
        } else
            zone_id = " ";

        zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    zone_id = zonelist.get(position).getZone_id();
                } else
                    zone_id = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pincode.getText().toString();
                String n = name.getText().toString();
                String a = address.getText().toString();
                String p = phone.getText().toString();

                if (n.length() == 0 || a.length() == 0 || p.length() == 0 || pin.length() == 0 || zone_id.length() == 0)
                    Toast.makeText(sellerDetailActivity.this, "Some Fields Are Left Empty", Toast.LENGTH_LONG).show();
                else if (p.length() < 10)
                    Toast.makeText(sellerDetailActivity.this, "Incorrect Number Entered.", Toast.LENGTH_LONG).show();
                else if (!check)
                    Toast.makeText(sellerDetailActivity.this, "Number Already Exists.", Toast.LENGTH_LONG).show();
                else {
                    stopLocationUpdates();
                    Random rand = new Random();
                    String otp = String.format(Locale.ENGLISH, "%04d", rand.nextInt(10000));
                    Intent intent = new Intent(sellerDetailActivity.this, sellerOtpVerify.class);

                    Log.e("otp", otp);
                    frequentlyUsedClass.sendOTP(p, "Code : " + otp + " प्रिय किसान भाई, फ़ार्मस्टार परिवार में आपका स्वागत है,\n" +
                            "आपका कृषक पंजीकरण पूरा करने के लिये ऊपर लिखा कोड कंपनी प्रतिनिधि से साझा करें", sellerDetailActivity.this);

                    intent.putExtra("otp", otp);
                    intent.putExtra("name", n);
                    intent.putExtra("address", a);
                    intent.putExtra("phone", p);
                    intent.putExtra("zoneid", zone_id);
                    intent.putExtra("gpsAddress", mAddressOutput);
                    intent.putExtra("pincode", pin);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCheck)
            stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCheck)
            stopLocationUpdates();
    }

    private void getZones() {
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
                    zone_card_spinner_adapter adapter = new zone_card_spinner_adapter(sellerDetailActivity.this, R.layout.spinner_zone, zonelist, getLayoutInflater());
                    zone.setAdapter(adapter);
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
                    Toast.makeText(sellerDetailActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), sellerDetailActivity.this);


            }
        });
        AppController.getInstance().addToRequestQueue(strReq, "getzones");
    }

    public void checkNumber(final String number) {
       lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.CHECKNUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("exists")) {
                        check = false;
                        Toast.makeText(sellerDetailActivity.this, "Number Already Exists.", Toast.LENGTH_LONG).show();
                    } else {
                        check = true;
                    }
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
                    Toast.makeText(sellerDetailActivity.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerLedger.class.getName(), sellerDetailActivity.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("number", number);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                load.setVisibility(View.GONE);
                gpsAddress.setVisibility(View.VISIBLE);
                gpsAddress.setText(mAddressOutput);
                stopLocationUpdates();
                Toast.makeText(sellerDetailActivity.this, "Click On \'Locate On Map\' if you are doubtful about address.", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    private void getAddressFromGps() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            getLocationDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationDialog();
            } else
                Toast.makeText(sellerDetailActivity.this, "Requested feature declined by user.", Toast.LENGTH_SHORT).show();

        }
    }


    private void getLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(1000);
    }

    private void getLocationDialog() {

        load.setVisibility(View.VISIBLE);


        getLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    getAddressFromCoordinates();

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(
                                        sellerDetailActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                Toast.makeText(sellerDetailActivity.this, "Error Occured. Try Again.", Toast.LENGTH_SHORT).show();
                            } catch (ClassCastException e) {
                                Toast.makeText(sellerDetailActivity.this, "Error Occured. Try Again.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(sellerDetailActivity.this, "Error Occured. Try Again.", Toast.LENGTH_SHORT).show();

                            break;
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        getAddressFromCoordinates();

                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(sellerDetailActivity.this, "Requested feature declined by user.", Toast.LENGTH_SHORT).show();
                        load.setVisibility(View.GONE);


                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @SuppressLint("MissingPermission")
    private void getAddressFromCoordinates() {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(sellerDetailActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;


                        if (!Geocoder.isPresent()) {
                            Toast.makeText(sellerDetailActivity.this,
                                    "Network Error. Try again",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        startLocationUpdates();

                    }
                });

    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}

