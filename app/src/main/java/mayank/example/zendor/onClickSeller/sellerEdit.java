package mayank.example.zendor.onClickSeller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.Constants;
import mayank.example.zendor.FetchAddressIntentService;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.apiConnect;
import mayank.example.zendor.navigationDrawerOption.addCommodities;
import mayank.example.zendor.sellerDetailActivity;
import mayank.example.zendor.sellerExtraData;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class sellerEdit extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private EditText accoutNumber, ifscCode, accountName;
    private EditText n1, n2, n3, n4, n5, n6, n7;
    private TextView skip;
    private TextView submit;
    private ImageView camera, addnumber;
    private Toolbar toolbar;
    private boolean photoChanged;
    private String azone_id;
    private String comm;
    private LinearLayout alternate;
    private LoadingClass lc;
    private String seller_id;
    private String imgPath;
    private String NUM[];
    private TextView locate;
    private boolean isCheck;
    private LocationRequest locationRequest;
    private ProgressBar load;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient googleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 10;
    private LocationCallback mLocationCallback;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private Intent intent;
    private String mAddressOutput = null;
    private TextView gpsAddress;
    private String gps;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_extra_data);

        accoutNumber = findViewById(R.id.accountNumber);
        accountName = findViewById(R.id.accountName);
        ifscCode = findViewById(R.id.ifscCode);
        skip = findViewById(R.id.skip);
        submit = findViewById(R.id.submit);
        camera = findViewById(R.id.camera);
        addnumber = findViewById(R.id.addNumber);
        toolbar = findViewById(R.id.toolbar);
        alternate = findViewById(R.id.alternateNo);
        locate = findViewById(R.id.locate);
        load = findViewById(R.id.load);
        gpsAddress = findViewById(R.id.gpsAddress);


        n1 = findViewById(R.id.ed1);
        n2 = findViewById(R.id.ed2);
        n3 = findViewById(R.id.ed3);
        n4 = findViewById(R.id.ed4);
        n5 = findViewById(R.id.ed5);
        n6 = findViewById(R.id.ed6);
        n7 = findViewById(R.id.ed7);

        skip.setVisibility(View.GONE);
        //  alternate.setVisibility(View.GONE);

        lc = new LoadingClass(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        seller_id = getIntent().getStringExtra("seller_id");
        getSellerData();

        googleApiClient = new GoogleApiClient.Builder(sellerEdit.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(sellerEdit.this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                getAddress(locationResult.getLastLocation());
            }
        };
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheck = true;

                GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                int responseCode = apiAvailability.isGooglePlayServicesAvailable(sellerEdit.this);
                if (apiAvailability.isGooglePlayServicesAvailable(sellerEdit.this) == 0) {
                    getAddressFromGps();
                    Log.e("hi", "hello0");

                } else {
                    Dialog dialog = apiAvailability.getErrorDialog(sellerEdit.this, responseCode, 0);
                    dialog.setCancelable(false);
                    dialog.show();
                }

            }
        });

        camera.setScaleType(ImageView.ScaleType.CENTER);
        camera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_a_photo_black_24dp));
        photoChanged = false;

        addnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (n2.getVisibility() == View.GONE)
                    n2.setVisibility(View.VISIBLE);
                else if (n3.getVisibility() == View.GONE)
                    n3.setVisibility(View.VISIBLE);
                else if (n4.getVisibility() == View.GONE)
                    n4.setVisibility(View.VISIBLE);
                else if (n5.getVisibility() == View.GONE)
                    n5.setVisibility(View.VISIBLE);
                else if (n6.getVisibility() == View.GONE)
                    n6.setVisibility(View.VISIBLE);
                else if (n7.getVisibility() == View.GONE)
                    n7.setVisibility(View.VISIBLE);
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoChanged = false;
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(sellerEdit.this);
                    builder.setTitle("Permission to read and write to storage");
                    builder.setMessage("This app needs permission to read and write images to storage");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(sellerEdit.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                    2);
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    openImageIntent();
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String an = accoutNumber.getText().toString();
                final String aname = accountName.getText().toString();
                final String ifsc = ifscCode.getText().toString();

                String text[] = new String[]{n1.getText().toString(), n2.getText().toString(), n3.getText().toString(), n4.getText().toString(), n5.getText().toString(), n6.getText().toString(), n7.getText().toString()};
                String finalNumber = NUM[0];
                String c = NUM[0];
                for (int i = 0; i < 7; i++) {
                    int l = text[i].length();
                    if (l != 0) {
                        c = c.concat(text[i]);
                        finalNumber = finalNumber.concat("," + text[i]);
                    }
                }


                if (c.length() % 10 != 0)
                    Toast.makeText(sellerEdit.this, "Incorrect Number Entered", Toast.LENGTH_SHORT).show();
                else {
                    stopLocationUpdates();
                    if (photoChanged) {
                        lc.showDialog();
                        long time = System.currentTimeMillis();
                        final String path = "_" + time + imgPath.substring(imgPath.lastIndexOf("."));

                        try {

                            final String finalNumber1 = finalNumber;
                            new MultipartUploadRequest(sellerEdit.this, URLclass.UPLOAD_IMAGES)
                                    .addFileToUpload(imgPath, "image")
                                    .addParameter("name", path)
                                    .setNotificationConfig(new UploadNotificationConfig())
                                    .setMaxRetries(10)
                                    .setDelegate(new UploadStatusDelegate() {
                                        @Override
                                        public void onProgress(Context context, UploadInfo uploadInfo) {

                                        }

                                        @Override
                                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                            Toast.makeText(sellerEdit.this, "Error Occure. Please Retry.", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                            pushExtraData(an, aname, ifsc, finalNumber1, path);

                                        }

                                        @Override
                                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                                        }
                                    })
                                    .startUpload();

                        } catch (Exception exc) {
                            Toast.makeText(sellerEdit.this, "Error Occured.", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        pushExtraData(an, aname, ifsc, finalNumber, "");

                }

            }
        });

    }

    private void getAddress(Location location){
        String lat = location.getLatitude()+"";
        String longitude = location.getLongitude()+"";
        Log.e("resposnes", lat+" "+longitude);
        String API_KEY = "AIzaSyAbIngBpBEV0DE4nfMGHfJURuyeZwJXixU ";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+longitude+"&key="+API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray jsonObject = json.getJSONArray("results");
                    JSONObject jsonObject1 = jsonObject.getJSONObject(0);
                    mAddressOutput = jsonObject1.getString("formatted_address");
                    load.setVisibility(View.GONE);
                    gpsAddress.setVisibility(View.VISIBLE);
                    gpsAddress.setText(mAddressOutput);
                    stopLocationUpdates();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
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

    public void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public class AddressResultReceiver extends ResultReceiver {

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
                Toast.makeText(sellerEdit.this, "Click On \'Locate On Map\' if you are doubtful about address.", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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

    private void getLocationDialog() {

        load.setVisibility(View.VISIBLE);


        getLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {


                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(sellerEdit.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getAddressFromCoordinates();
            }
        });


    }
    @SuppressLint("MissingPermission")
    private void getAddressFromCoordinates() {


        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(sellerEdit.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;


                        if (!Geocoder.isPresent()) {
                            Toast.makeText(sellerEdit.this,
                                    "Network Error. Try again",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        startLocationUpdates();
                    }
                });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    private void getLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10*1000);
        locationRequest.setFastestInterval(1000);
    }

    private void openImageIntent() {
        Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(120, 120)
                .getIntent(this);
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        getAddressFromCoordinates();

                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(sellerEdit.this, "Requested feature declined by user.", Toast.LENGTH_SHORT).show();
                        load.setVisibility(View.GONE);


                        break;
                    default:
                        break;
                }
                break;
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == 5) {
                Log.e("here", "here111");
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    String filename = System.currentTimeMillis() + ".jpg";
                    File file = new File(getFilesDir(), filename);

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

                    imgPath = file.getPath();
                    camera.setScaleType(ImageView.ScaleType.FIT_XY);
                    camera.setImageBitmap(bitmap);
                    photoChanged = true;

                } catch (Exception e) {
                    Log.e("qwrtyuikjhgfds", e + "");
                }

            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "Try Again.", Toast.LENGTH_SHORT).show();
        }


    }

    private void pushExtraData(final String an, final String aname, final String ifsc, final String mob, final String path) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.UPDATE_SELLER_BANK_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                lc.dismissDialog();
                sellerDetails.click.performClick();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(sellerEdit.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerEdit.this.getClass().getName(), sellerEdit.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String GPS = mAddressOutput == null ? gps : mAddressOutput;

                HashMap<String, String> map = new HashMap<>();
                map.put("sid", seller_id);
                map.put("acnumber", an);
                map.put("acname", aname);
                map.put("ifsc", ifsc);
                map.put("mob", mob);
                map.put("gps", GPS);

                if (photoChanged) {
                    map.put("path", path);
                }

                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 0:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationDialog();
                } else
                    Toast.makeText(sellerEdit.this, "Requested feature declined by user.", Toast.LENGTH_SHORT).show();


                break;

            case 2: {
                Map<String, Integer> perms = new HashMap<String, Integer>();

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    openImageIntent();
                } else {

                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void getSellerData() {

        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.SELLER_BANK_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String an = jsonObject.getString("accountNo");
                    String IFSC = jsonObject.getString("ifsc");
                    String acHolder = jsonObject.getString("ah");
                    String checkPicPath = jsonObject.getString("checkPicPath");
                    String number = jsonObject.getString("mob");
                    gps = jsonObject.getString("gps");

                    accountName.setText(acHolder);
                    accoutNumber.setText(an);
                    ifscCode.setText(IFSC);
                    if (checkPicPath.length() != 0)
                        Glide.with(sellerEdit.this).load(URLclass.CHEQUE_PIC_PATH + checkPicPath).into(camera);

                    NUM = number.split(",");

                    for (int i = 1; i < NUM.length; i++) {
                        int a = getResources().getIdentifier("ed" + (i), "id", getPackageName());
                        EditText ed = findViewById(a);
                        ed.setVisibility(View.VISIBLE);
                        ed.setText(NUM[i]);
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
                    Toast.makeText(sellerEdit.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerEdit.this.getClass().getName(), sellerEdit.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sid", seller_id);
                return params;
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
}
