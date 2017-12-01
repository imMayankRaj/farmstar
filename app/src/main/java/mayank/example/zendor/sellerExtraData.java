package mayank.example.zendor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class sellerExtraData extends AppCompatActivity {

    private EditText accoutNumber, ifscCode, accountName;
    private EditText n1,n2,n3,n4,n5,n6,n7;
    private TextView skip;
    private TextView submit;
    private ImageView camera, addnumber;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private boolean photoChanged;
    private ProgressDialog progressDialog;
    private String azone_id;
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(sellerExtraData.this, LoginActivity.class));
            }
        });
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);
        addnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(n2.getVisibility() == View.GONE)
                    n2.setVisibility(View.VISIBLE);
                else if(n3.getVisibility() == View.GONE)
                    n3.setVisibility(View.VISIBLE);
                else if(n4.getVisibility() == View.GONE)
                    n4.setVisibility(View.VISIBLE);
                else if(n5.getVisibility() == View.GONE)
                    n5.setVisibility(View.VISIBLE);
                else if(n6.getVisibility() == View.GONE)
                    n6.setVisibility(View.VISIBLE);
                else if(n7.getVisibility() == View.GONE)
                    n7.setVisibility(View.VISIBLE);
            }
        });


        camera.setScaleType(ImageView.ScaleType.CENTER);
        camera.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_add_a_photo_black_24dp));
        photoChanged=false;

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
                }
                else
                {
                    openImageIntent();
                }
            }
        });


        apiConnect connect = new apiConnect(this, "detail");
        requestQueue = connect.getRequestQueue();
        final Bundle bundle =getIntent().getBundleExtra("sellerDetail");

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = bundle.getString("phone");
                String name = bundle.getString("name");
                String address = bundle.getString("address");
                azone_id = bundle.getString("zoneid");
                String pin = bundle.getString("pincode");
                pushExtraData("","","", phone, name, address, pin);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String an = accoutNumber.getText().toString();
                String aname = accountName.getText().toString();
                String ifsc = ifscCode.getText().toString();
                String phone = bundle.getString("phone");
                String name = bundle.getString("name");
                String address = bundle.getString("address");
                String pin = bundle.getString("pincode");

                azone_id = bundle.getString("zoneid");


                String text[] = new String[]{n1.getText().toString(),n2.getText().toString(),n3.getText().toString(),n4.getText().toString(),n5.getText().toString(),n6.getText().toString(),n7.getText().toString()};
                String finalNumber = "";
                for(int i =0;i<7;i++){
                    int l =text[i].length();
                    if(l!=0){
                        finalNumber = finalNumber + "," +text[i];
                    }
                }
                finalNumber = phone + finalNumber;

                String c="";
                String check[] = finalNumber.split(",");
                for(int j=0; j<check.length; j++){
                    c=c+check[j];
                }
                if(c.length()%10 !=0)
                    Toast.makeText(sellerExtraData.this, "Incorrect Number Entered", Toast.LENGTH_SHORT).show();
                else
                    pushExtraData(an, aname, ifsc, finalNumber, name, address, pin);
            }
        });

    }

    private void openImageIntent() {
        Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(120,120)
                .getIntent(this);
        startActivityForResult(intent, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 5) {
                Log.d("here","here111");
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                        File file = new File(resultUri.getPath());
                        camera.setScaleType(ImageView.ScaleType.FIT_XY);
                        camera.setImageBitmap(bitmap);
                        photoChanged=true;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            }

        }

    }
    private void pushExtraData(final String an, final String aname, final String ifsc, final String finalNumber, final String name, final String address, final String pincode){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.PUSHDATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(sellerExtraData.this, "Seller Added Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(sellerExtraData.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String zid;
                String id = sharedPreferences.getString("id","");
                String pos = sharedPreferences.getString("position","");

                if(pos.equals("0"))
                    zid = azone_id;
                else
                    zid  = sharedPreferences.getString("zid","");

                HashMap<String, String> map = new HashMap<>();
                map.put("accNumber", an);
                map.put("accName", aname);
                map.put("ifsc", ifsc);
                map.put("name", name);
                map.put("address", address);
                map.put("pincode", pincode);
                map.put("zid", zid);
                map.put("othermob", finalNumber);
                map.put("addedBy",id);
                if(photoChanged) {
                    Bitmap drawable=((BitmapDrawable)camera.getDrawable()).getBitmap();
                    map.put("checkpath", getStringImage(drawable));
                }

                return map;
            }
        };
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
}
