package mayank.example.zendor.navigationDrawerOption;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mayank.example.zendor.ApplicationQueue;
import mayank.example.zendor.LoadingClass;
import mayank.example.zendor.R;
import mayank.example.zendor.URLclass;
import mayank.example.zendor.landingPageFragment.booked;
import mayank.example.zendor.landingPageFragment.picked;
import xendorp1.application_classes.AppController;

import static mayank.example.zendor.MainActivity.showError;
import static mayank.example.zendor.MainActivity.showToast;

public class addCommodities extends AppCompatActivity {

    private ListView commodityListView;
    private List<String> commodityList;
    private String[] commTypes;
    private TextView addComm;
    private LoadingClass ld;
    private ImageView compic;
    private boolean photoChanged = false;
    private String imgPath;
    private LinearLayout layout;
    private TextView textView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodities);
        commodityListView = findViewById(R.id.commodityList);
        addComm = findViewById(R.id.addNewComm);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout = findViewById(R.id.noDataLayout);
        textView = findViewById(R.id.text);


        commodityList = new ArrayList<>();

        ld = new LoadingClass(this);

        getCommodity();

        addComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommDialog();
            }
        });


        commodityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String co = commodityList.get(position);
                Intent intent = new Intent(addCommodities.this, onClickCommodityList.class);
                intent.putExtra("comm", co);
                startActivity(intent);
            }
        });


    }

    private void getCommodity() {
        ld.showDialog();
        layout.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLclass.GET_COMMODITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("")) {
                    String[] COMM = response.split(",");
                    commodityList = Arrays.asList(COMM);
                }

                commodityListView.setAdapter(new ArrayAdapter<String>(addCommodities.this,
                        android.R.layout.simple_list_item_1, commodityList));

                ld.dismissDialog();

                if(commodityList.size() == 0){
                    layout.setVisibility(View.VISIBLE);
                    textView.setText("No Commodities Available.");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(addCommodities.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, addCommodities.this.getClass().getName(), addCommodities.this);

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void addCommDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_comm_dialog);

        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView submit = dialog.findViewById(R.id.submit);
        final TextView cname = dialog.findViewById(R.id.cname);
        final Spinner type = dialog.findViewById(R.id.comType);
        compic = dialog.findViewById(R.id.imageView);

        commTypes = new String[]{"Vegetables", "Pulses", "Fruits", "Cereals", "Cash Crops", "Oil Seeds", "Spices", "Forage Crops", "Flowers", "Dairy", "Live Stocks"};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, commTypes);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(spinnerArrayAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoChanged) {
                    ld.showDialog();
                    final String commName = cname.getText().toString();
                    final String typeSelected = type.getSelectedItem().toString();

                    long time = System.currentTimeMillis();
                    final String path = commName+"_"+time+imgPath.substring(imgPath.lastIndexOf("."));
                    try {

                        new MultipartUploadRequest(addCommodities.this, URLclass.UPLOAD_IMAGES)
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
                                        Toast.makeText(addCommodities.this, "Error Occure. Please Retry.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                        addNewCommodity(commName, typeSelected, path);

                                    }

                                    @Override
                                    public void onCancelled(Context context, UploadInfo uploadInfo) {

                                    }
                                })
                                .startUpload();

                    } catch (Exception exc) {
                        Log.e("erroe is", exc + "");
                        Toast.makeText(addCommodities.this, "Error Occured.", Toast.LENGTH_SHORT).show();
                    }


                    dialog.dismiss();
                } else
                    Toast.makeText(addCommodities.this, "Add Commodity Pic.", Toast.LENGTH_SHORT).show();
            }
        });

        compic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 5) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    String filename = System.currentTimeMillis()+".jpg";
                    File file = new File(getFilesDir(), filename);

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);

                    imgPath = file.getPath();
                    compic.setScaleType(ImageView.ScaleType.FIT_XY);
                    compic.setImageBitmap(bitmap);
                    photoChanged = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "An error Occured.", Toast.LENGTH_SHORT).show();
        }

    }

    private void openImageIntent() {
        Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(120, 120)
                .getIntent(this);
        startActivityForResult(intent, 5);
    }

    private void addNewCommodity(final String cname, final String type, final String path) {
        ld.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLclass.ADD_NEW_COMM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getCommodity();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(addCommodities.this, "Time out. Reload.", Toast.LENGTH_SHORT).show();
                } else
                    showError(error, booked.class.getName(), addCommodities.this);

                ld.dismissDialog();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parameters = new HashMap<>();
                parameters.put("cname", cname);
                parameters.put("type", type);
                parameters.put("path", path);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
