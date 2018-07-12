package com.androidstuff.fenaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidstuff.fenaapp.ClassUtills.ConfigInfo;
import com.androidstuff.fenaapp.ClassUtills.LogUtils;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RetailerDataForm extends AppCompatActivity {
    private TextView tvpowder, tvcake, tvladi;
    private EditText etpowder, etcake, etladi;
    private String Powder, Cake, Ladi;
    private EditText etdate, etpromname, etagencyname, etdisname, etmobnum, etconperson, ettown,
            etcity, etsrname, etsoname, etretname, etadd, etretphonenum;
    private Spinner spState;
    private Button btnsubmit;
    private DatePickerDialog picker;
    private ImageView ivpowder, ivcake, ivladi;
    private String Date, ProName, AgencyName, DisName, MobileNum, ContactPerson, Town, City, State, SRName, SOName, RetName, Address, RetMobileNUm;

    private Button btnimage1, btnimage2, btnsig;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private String encodedResume1, encodedResume2, encodedResumesignature, Uid;
    private Bitmap bitmap1, bitmap2, signatureBitmap;
    private int CAMERA1 = 1, CAMERA2 = 2;
    private ProgressDialog pdLoading;
    private String Longitude, Latitude, Discode, Id,FullAddress;
    private Context mcontext;
    private String Edit;
    String[] Stateee = {"Select State",
            "Andaman and Nicobar Islands",
            "Andhra Pradesh",
            "Arunachal Pradesh",
            "Assam",
            "Bihar",
            "Chandigarh",
            "Chhattisgarh",
            "Dadra and Nagar Haveli",
            "Daman and Diu",
            "Delhi",
            "Goa",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jammu and Kashmir",
            "Jharkhand",
            "Karnataka",
            "Kerala",
            "Lakshadweep",
            "Madhya Pradesh",
            "Maharashtra",
            "Manipur",
            "Meghalaya",
            "Mizoram",
            "Nagaland",
            "Orissa",
            "Pondicherry",
            "Punjab",
            "Rajasthan",
            "Sikkim",
            "Tamil Nadu",
            "Telangana",
            "Tripura",
            "Uttaranchal",
            "Uttar Pradesh",
            "West Bengal",};
    private ArrayAdapter state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_data_form);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarret);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            turnGPSOn();
        }


        mcontext = this;

        final SharedPreferences prefsss = getSharedPreferences("Login", MODE_PRIVATE);
        Discode = prefsss.getString("rscode", null);

        final SharedPreferences prefss = getSharedPreferences("latlong", MODE_PRIVATE);
        Longitude = prefss.getString("lat", null);
        Latitude = prefss.getString("laong", null);
        FullAddress = prefss.getString("fulladd", null);

        if (TextUtils.isEmpty(Longitude)) {
            Longitude = "";
        }
        if (TextUtils.isEmpty(Latitude)) {
            Latitude = "";
        }

        final SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
        Uid = uid.getString("uid", null);

        Intent intent = getIntent();
        Edit = intent.getStringExtra("edit");
        Id = intent.getStringExtra("id");

        if (TextUtils.isEmpty(Edit)) {


        } else if (Edit.equals("EDIT")) {
            GetEditData();
        }

        tvpowder = (TextView) findViewById(R.id.tv_powderret);
        tvcake = (TextView) findViewById(R.id.tv_cakeret);
        tvladi = (TextView) findViewById(R.id.tv_ladiret);
        etpowder = (EditText) findViewById(R.id.et_powderret);
        etcake = (EditText) findViewById(R.id.et_cakeret);
        etladi = (EditText) findViewById(R.id.et_ladiret);

        ivpowder = (ImageView) findViewById(R.id.iv_powderret);
        ivcake = (ImageView) findViewById(R.id.iv_cakeret);
        ivladi = (ImageView) findViewById(R.id.iv_ladiret);


        etdate = (EditText) findViewById(R.id.et_date);
        etpromname = (EditText) findViewById(R.id.et_promotornameret);
        etagencyname = (EditText) findViewById(R.id.et_agencynameret);
        etdisname = (EditText) findViewById(R.id.et_disnamenameret);
        etmobnum = (EditText) findViewById(R.id.et_mobnumret);
        etconperson = (EditText) findViewById(R.id.et_contactperret);
        ettown = (EditText) findViewById(R.id.et_townret);
        etcity = (EditText) findViewById(R.id.et_cityret);
        etsrname = (EditText) findViewById(R.id.et_srnameret);
        etsoname = (EditText) findViewById(R.id.et_sonameret);
        etretname = (EditText) findViewById(R.id.et_retnameret);
        etadd = (EditText) findViewById(R.id.et_retaddret);
        etretphonenum = (EditText) findViewById(R.id.et_retnumret);
        spState = (Spinner) findViewById(R.id.sp_stateret);
        btnsubmit = (Button) findViewById(R.id.btn_submitret);
        btnimage1 = (Button) findViewById(R.id.btn_image1ret);
        btnimage2 = (Button) findViewById(R.id.btn_image2ret);
        btnsig = (Button) findViewById(R.id.btn_signatureret);

        //-------------------------------------------------------------------------------------------------------------------

        ivpowder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etpowder.setVisibility(View.VISIBLE);
                ivpowder.setVisibility(View.GONE);
            }
        });
        ivcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etcake.setVisibility(View.VISIBLE);
                ivcake.setVisibility(View.GONE);
            }
        });
        ivladi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etladi.setVisibility(View.VISIBLE);
                ivladi.setVisibility(View.GONE);
            }
        });


        state = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Stateee);
        state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(state);

        //----------------------------------------------------------------------------------------------------------------

        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RetailerDataForm.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etdate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        btnsig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignatureDialog();
            }
        });

        btnimage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
            }
        });

        btnimage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera2();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date = etdate.getText().toString();
                ProName = etpromname.getText().toString();
                AgencyName = etagencyname.getText().toString();
                DisName = etdisname.getText().toString();
                MobileNum = etmobnum.getText().toString();
                ContactPerson = etconperson.getText().toString();
                Town = ettown.getText().toString();
                City = etcity.getText().toString();
                State = spState.getSelectedItem().toString();
                SRName = etsrname.getText().toString();
                SOName = etsoname.getText().toString();
                RetName = etretname.getText().toString();
                Address = etadd.getText().toString();
                RetMobileNUm = etretphonenum.getText().toString();
                Powder = etpowder.getText().toString();
                Cake = etcake.getText().toString();
                Ladi = etladi.getText().toString();


                if (TextUtils.isEmpty(encodedResume1)) {
                    encodedResume1 = " ";
                }
                if (TextUtils.isEmpty(encodedResume2)) {
                    encodedResume2 = " ";
                }
                if (TextUtils.isEmpty(encodedResumesignature)) {
                    encodedResumesignature = " ";
                }
                if (RetName.length() < 0) {
                    etretname.setError("Please enter name");
                } else if (Address.length() < 0) {
                    etadd.setError("Please enter Address");
                } else if (Town.length() < 0) {
                    ettown.setError("Please enter town");
                } else if (City.length() < 0) {
                    etcity.setError("Please enter city");
                }else if (State.equals("Select State")){
                    Toast.makeText(RetailerDataForm.this, "Please Select State", Toast.LENGTH_SHORT).show();
                } else if (MobileNum.length() < 10) {
                    etmobnum.setError("Please enter 10 digits");
                } else if (Powder.length() < 0) {
                    etpowder.setError("Please enter quantity");
                } else if (Cake.length() < 0) {
                    etcake.setError("Please enter quantity");
                } else if (Ladi.length() < 0) {
                    etladi.setError("Please enter quantity");
                } else if (NetworkUtils.isConnected(RetailerDataForm.this)) {
                    if (TextUtils.isEmpty(Edit)) {
                        Submit();
                    } else if (Edit.equals("EDIT")) {
                        Update();
                    }
                } else {
                    Toast.makeText(RetailerDataForm.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void Submit() {

        String LatLong=Latitude+","+Longitude;


        pdLoading = new ProgressDialog(RetailerDataForm.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("Town", Town);
            object.put("City", City);
            object.put("State", State);
            object.put("ConsumerName", RetName);
            object.put("ConsumerAddress", Address);
            object.put("ConsumerPhoneno", MobileNum);
            object.put("ProductSamplepowder", Powder);
            object.put("ProductSamplecake", Cake);
            object.put("ProductSampleLadi", Ladi);
            object.put("lat", LatLong);
            object.put("lon", FullAddress);
            object.put("UID", Uid);
            object.put("DistributorCode", Discode);
            object.put("Image1", encodedResume1);
            object.put("Image2", encodedResume2);
            object.put("Signature", encodedResumesignature);
            object.put("Ext1", ".jpg");
            object.put("Ext2", ".jpg");
            object.put("Ext3", ".jpg");


            Log.i("dattatjsonRet", "" + object);
            final String requestBody = object.toString();

            Log.i("rbody", "" + requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.RetailerAdd, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYResponceRet", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Message = jsonObject.getString("Message");
                        if (Message.equals("Success")) {
                            finish();
                        }
                    } catch (Exception Ex) {

                    }
                    pdLoading.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());

                    pdLoading.dismiss();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA1);
    }

    private void takePhotoFromCamera2() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA2);
    }


    private void SignatureDialog() {
        //Alert Dialog
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.signature_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        mSignaturePad = (SignaturePad) promptsView.findViewById(R.id.signature_pad);
        mClearButton = (Button) promptsView.findViewById(R.id.clear_button);
        mSaveButton = (Button) promptsView.findViewById(R.id.save_button);


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // show it
        alertDialog.show();
        //alertDialog.getWindow().setLayout(600, 470);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // Toast.makeText(RetailerDataForm.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureBitmap = mSignaturePad.getSignatureBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedResumesignature = Base64.encodeToString(byteArray, Base64.DEFAULT);

                alertDialog.dismiss();

                LogUtils.showLog("bitmapsig", "" + encodedResumesignature);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA1) {
            if (data != null) {
                bitmap1 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedResume1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                LogUtils.showLog("bitmap1", "" + encodedResume1);

                btnimage1.setText("Selected");
                btnimage2.setVisibility(View.VISIBLE);
            }

        } else if (requestCode == CAMERA2) {
            if (data != null) {
                bitmap2 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedResume2 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                LogUtils.showLog("bitmap2", "" + encodedResume2);
                btnimage2.setText("Selected");
                // String path = saveImage(bitmap);
            }


        }
    }

    public void GetEditData() {
        final SharedPreferences prefss = getSharedPreferences("Login", MODE_PRIVATE);
        String Type = prefss.getString("type", null);


        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("TYPE", "R");
            object.put("ID", Id);


            Log.i("EidtjsonRet", "" + object);
            final String requestBody = object.toString();

            Log.i("rsdbody", "" + requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.RetGetEditData, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYResponceeditret", response);

                    btnimage1.setVisibility(View.GONE);
                    btnsig.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String cityy = jsonObject.getString("City");
                        String Consumer_Addresss = jsonObject.getString("Consumer_Address");
                        String Consumer_Namee = jsonObject.getString("Consumer_Name");
                        String Consumer_Phonenoo = jsonObject.getString("Consumer_Phoneno");
                        String Distributor_Codee = jsonObject.getString("Distributor_Code");
                        String IDdd = jsonObject.getString("ID");
                        String StateeString = jsonObject.getString("State");
                        String Townn = jsonObject.getString("Town");
                        String ProductSampleLadi = jsonObject.getString("ProductSampleLadi");
                        String ProductSamplecake = jsonObject.getString("ProductSamplecake");
                        String Product_Sold_1kg = jsonObject.getString("ProductSamplepowder");

                        etcity.setText(cityy);
                        etadd.setText(Consumer_Addresss);
                        etretname.setText(Consumer_Namee);
                        etmobnum.setText(Consumer_Phonenoo);
                        ettown.setText(Townn);
                        etladi.setText(ProductSampleLadi);
                        etcake.setText(ProductSamplecake);
                        etpowder.setText(Product_Sold_1kg);
                        int pos = new ArrayList<String>(Arrays.asList(Stateee)).indexOf(StateeString);
                        spState.setSelection(pos);


                    } catch (Exception ex) {

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Update() {
        pdLoading = new ProgressDialog(RetailerDataForm.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("Town", Town);
            object.put("City", City);
            object.put("State", State);
            object.put("ID", Id);
            object.put("ConsumerName", RetName);
            object.put("ConsumerAddress", Address);
            object.put("ConsumerPhoneno", MobileNum);
            object.put("Type", "R");
            object.put("ProductSamplepowder", Powder);
            object.put("ProductSamplecake", Cake);
            object.put("ProductSampleLadi", Ladi);


            Log.i("dattatjkjghdkjson", "" + object);
            final String requestBody = object.toString();

            Log.i("rbody", "" + requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.updatedate, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYResponce,jgads", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Message = jsonObject.getString("Message");
                        if (Message.equals("Success")) {
                            finish();
                        }
                    } catch (Exception Ex) {

                    }
                    pdLoading.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());

                    pdLoading.dismiss();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }

                }


            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(200000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void turnGPSOn() {
        Toast.makeText(RetailerDataForm.this, "Please turn on your location.", Toast.LENGTH_SHORT).show();
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);

    }
}
