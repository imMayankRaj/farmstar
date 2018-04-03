package mayank.example.zendor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import mayank.example.zendor.navigationDrawerOption.addCommodities;
import mayank.example.zendor.onClickBuyer.onClickBuyerCard;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;

public class sellerExtraData extends AppCompatActivity {

    private EditText accoutNumber, ifscCode, accountName;
    private EditText n1, n2, n3, n4, n5, n6, n7;
    private TextView skip;
    private TextView submit;
    private ImageView camera, addnumber;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private boolean photoChanged;
    private String azone_id;
    private String comm;
    private String imgPath;
    private LoadingClass lc;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_extra_data);

        accoutNumber = findViewById(R.id.accountNumber);
        accountName = findViewById(R.id.accountName);
        ifscCode = findViewById(R.id.ifscCode);
        n1 = findViewById(R.id.ed1);
        n2 = findViewById(R.id.ed2);
        n3 = findViewById(R.id.ed3);
        n4 = findViewById(R.id.ed4);
        n5 = findViewById(R.id.ed5);
        n6 = findViewById(R.id.ed6);
        n7 = findViewById(R.id.ed7);
        skip = findViewById(R.id.skip);
        submit = findViewById(R.id.submit);
        camera = findViewById(R.id.camera);
        addnumber = findViewById(R.id.addNumber);
        toolbar = findViewById(R.id.toolbar);
        lc = new LoadingClass(this);


        findViewById(R.id.locate).setVisibility(View.GONE);
        findViewById(R.id.ll).setVisibility(View.GONE);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(sellerExtraData.this, LoginActivity.class));
            }
        });
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        String zid = sharedPreferences.getString("zid", "");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("sellers").child(zid);

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


        camera.setScaleType(ImageView.ScaleType.CENTER);
        camera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_a_photo_black_24dp));
        photoChanged = false;

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(sellerExtraData.this);
                    builder.setTitle("Permission to read and write to storage");
                    builder.setMessage("This app needs permission to read and write images to storage");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(sellerExtraData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
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


        final Bundle bundle = getIntent().getBundleExtra("sellerDetail");
        final Bundle bun = getIntent().getBundleExtra("comm");

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = bundle.getString("phone");
                final String name = bundle.getString("name");
                final String address = bundle.getString("address");
                azone_id = bundle.getString("zoneid");
                final String pin = bundle.getString("pincode");
                comm = bun.getString("commodities");
                String gps = bundle.getString("gpsAddress");

                if (gps == null) {
                    gps = "";
                }

                pushExtraData("", "", "", phone, name, address, pin, gps, "");


            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String an = accoutNumber.getText().toString();
                final String aname = accountName.getText().toString();
                final String ifsc = ifscCode.getText().toString();
                final String phone = bundle.getString("phone");
                final String name = bundle.getString("name");
                final String address = bundle.getString("address");
                final String pin = bundle.getString("pincode");
                String gps = bundle.getString("gpsAddress");

                azone_id = bundle.getString("zoneid");

                comm = bun.getString("commodities");

                if (gps == null) {
                    gps = "";
                }

                String text[] = new String[]{n1.getText().toString(), n2.getText().toString(), n3.getText().toString(), n4.getText().toString(), n5.getText().toString(), n6.getText().toString(), n7.getText().toString()};
                String finalNumber = "";
                for (int i = 0; i < 7; i++) {
                    int l = text[i].length();
                    if (l != 0) {
                        finalNumber = finalNumber.concat("," + text[i]);
                    }
                }
                finalNumber = phone + finalNumber;

                String c = "";
                String check[] = finalNumber.split(",");
                for (int j = 0; j < check.length; j++) {
                    c = c.concat(check[j]);
                }
                if (c.length() % 10 != 0)
                    Toast.makeText(sellerExtraData.this, "Incorrect Number Entered", Toast.LENGTH_SHORT).show();
                else {
                    if (photoChanged){
                        lc.showDialog();
                        long time = System.currentTimeMillis();
                        final String path =   "_" + time + imgPath.substring(imgPath.lastIndexOf("."));

                        try {

                            final String finalNumber1 = finalNumber;
                            final String finalGps1 = gps;
                            new MultipartUploadRequest(sellerExtraData.this, URLclass.UPLOAD_IMAGES)
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
                                            Toast.makeText(sellerExtraData.this, "Error Occured. Please Retry.", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                            pushExtraData(an, aname, ifsc, finalNumber1, name, address, pin, finalGps1,path);

                                        }

                                        @Override
                                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                                        }
                                    })
                                    .startUpload();

                        } catch (Exception exc) {
                            Toast.makeText(sellerExtraData.this, "Error Occured.", Toast.LENGTH_SHORT).show();
                        }
                    }else
                        pushExtraData(an, aname, ifsc, finalNumber, name, address, pin, gps, "");


                }
            }
        });

    }


    private void openImageIntent() {
        Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(120, 120)
                .getIntent(this);
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 5) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        String filename = System.currentTimeMillis()+".jpg";
                        File file = new File(getFilesDir(), filename);

                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

                        imgPath = file.getPath();
                        camera.setScaleType(ImageView.ScaleType.FIT_XY);
                        camera.setImageBitmap(bitmap);
                        photoChanged = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            }

        }

    }

    private void pushExtraData(final String an, final String aname, final String ifsc, final String finalNumber, final String name, final String address, final String pincode, final String gpsAddress, final String path) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PUSHDATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String seller_id = jsonObject.getString("seller_id");
                    storeCommodities(comm, seller_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(sellerExtraData.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerExtraData.this.getClass().getName(), sellerExtraData.this);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String zid;
                String id = sharedPreferences.getString("id", "");
                String pos = sharedPreferences.getString("position", "");

                if (pos.equals("0"))
                    zid = azone_id;
                else
                    zid = sharedPreferences.getString("zid", "");


                HashMap<String, String> map = new HashMap<>();
                map.put("accNumber", an);
                map.put("accName", aname);
                map.put("ifsc", ifsc);
                map.put("name", name);
                map.put("address", address);
                map.put("pincode", pincode);
                map.put("zid", zid);
                map.put("othermob", finalNumber);
                map.put("addedBy", id);
                map.put("gps", gpsAddress);
                map.put("path", path);

                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
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


    private void storeCommodities(final String comm, final String seller_id) {
        lc.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_COMMODITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               lc.dismissDialog();

               long time = System.currentTimeMillis();
               mDatabase.setValue(time+"");

                Toast.makeText(sellerExtraData.this, "Seller Added Successfully", Toast.LENGTH_SHORT).show();
               /* Intent intent = new Intent(sellerExtraData.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lc.dismissDialog();

                if (error instanceof TimeoutError) {
                    Toast.makeText(sellerExtraData.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, sellerExtraData.this.getClass().getName(), sellerExtraData.this);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.e("commodities", comm);

                Map<String, String> parameters = new HashMap<>();
                parameters.put("seller_id", seller_id);
                parameters.put("str", comm);
                return parameters;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
