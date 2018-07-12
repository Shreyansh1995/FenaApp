package com.androidstuff.fenaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidstuff.fenaapp.ClassUtills.ConfigInfo;
import com.androidstuff.fenaapp.ClassUtills.LogUtils;
import com.androidstuff.fenaapp.ClassUtills.MyApplication;
import com.androidstuff.fenaapp.ClassUtills.ResponseListener;
import com.androidstuff.fenaapp.ClassUtills.Vrequest;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.androidstuff.fenaapp.ClassUtills.MyApplication.getInstance;
import static java.security.AccessController.getContext;

public class ConsumerDataForm extends AppCompatActivity {

    private ImageView ivpowder1, ivpowder, ivpowdersam1, ivpowdersam;
    private EditText etpowder1, etpowder, etpowdersam1, etpowdersam;
    private EditText etdate, etproname, etagencyname, etdisname, etmobnum, etconperson, ettown, etcity, etsrname, etsoname,
            etconname, etaddress, etphonenum;
    private Spinner spstate;
    private String Date, ProName, AgencyName, DisName, MobileNum, ContactPerson, Town, City, State, SRName, SOName, ConName, Address, PhoneNum,
            Powder1, Powder2, Powder3, Powder4, Discode;
    private Button btnsubmit, btnsig;
    private Context mcontext;
    private ProgressDialog pdLoading;
    private String Longitude, Latitude,FullAddress;
    private DatePickerDialog picker;

    private Button btnimage1, btnimage2;
    private int CAMERA1 = 1, CAMERA2 = 2;
    private Bitmap bitmap1, bitmap2, signatureBitmap;
    private String encodedResume1, encodedResume2, encodedResumesignature, Uid,Id;

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    ImageView imageView;
    private  String Edit;

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
            "West Bengal"};
    private ArrayAdapter stateAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_data_form);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            turnGPSOn();
        }

        mcontext = this;

        final SharedPreferences prefsss = getSharedPreferences("Login", MODE_PRIVATE);
        Discode = prefsss.getString("rscode", null);

        final SharedPreferences prefss = getSharedPreferences("latlong", MODE_PRIVATE);
        Longitude = prefss.getString("lat", null);
        Latitude = prefss.getString("laong", null);
        FullAddress = prefss.getString("fulladd", null);

        LogUtils.showLog("fulladdress",""+FullAddress);

        if (TextUtils.isEmpty(Longitude)) {
            Longitude = "";
        }
        if (TextUtils.isEmpty(Latitude)) {
            Latitude = "";
        }
        final SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
        Uid = uid.getString("uid", null);


        Intent intent=getIntent();
         Edit=intent.getStringExtra("edit");
        Id=intent.getStringExtra("id");

        if (TextUtils.isEmpty(Edit)){

        }else if (Edit.equals("EDIT")){
            GetEditData();

        }

        imageView=(ImageView)findViewById(R.id.imageee);

        ivpowder1 = (ImageView) findViewById(R.id.iv_powdercon1);
        ivpowder = (ImageView) findViewById(R.id.iv_powdercon);
        ivpowdersam1 = (ImageView) findViewById(R.id.iv_powdercon1sam);
        ivpowdersam = (ImageView) findViewById(R.id.iv_powderconsam);

        etpowder1 = (EditText) findViewById(R.id.et_powdercon1);
        etpowder = (EditText) findViewById(R.id.et_powdercon);
        etpowdersam1 = (EditText) findViewById(R.id.et_powdercon1sam);
        etpowdersam = (EditText) findViewById(R.id.et_powderconsam);


        etdate = (EditText) findViewById(R.id.et_datecon);
        etproname = (EditText) findViewById(R.id.et_promotornamecon);
        etagencyname = (EditText) findViewById(R.id.et_agencynamecon);
        etdisname = (EditText) findViewById(R.id.et_disnamenamecon);
        etmobnum = (EditText) findViewById(R.id.et_mobnumcon);
        etconperson = (EditText) findViewById(R.id.et_contactpercon);
        ettown = (EditText) findViewById(R.id.et_towncon);
        etcity = (EditText) findViewById(R.id.et_citycon);
        etsrname = (EditText) findViewById(R.id.et_srnamecon);
        etsoname = (EditText) findViewById(R.id.et_sonamecon);
        etconname = (EditText) findViewById(R.id.et_retnamecon);
        etaddress = (EditText) findViewById(R.id.et_retaddcon);
        etphonenum = (EditText) findViewById(R.id.et_retnumcon);
        spstate = (Spinner) findViewById(R.id.sp_statecon);
        btnsubmit = (Button) findViewById(R.id.btn_submitcon);
        btnsig = (Button) findViewById(R.id.btn_signaturecon);

        btnimage1 = (Button) findViewById(R.id.btn_image1);
        btnimage2 = (Button) findViewById(R.id.btn_image2);


        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ConsumerDataForm.this,
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
                OpenSignatureDialog();
            }
        });

        stateAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Stateee);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spstate.setAdapter(stateAdapter);


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date = etdate.getText().toString();
                ProName = etproname.getText().toString();
                AgencyName = etagencyname.getText().toString();
                DisName = etdisname.getText().toString();
                MobileNum = etmobnum.getText().toString();
                ContactPerson = etconperson.getText().toString();
                Town = ettown.getText().toString();
                City = etcity.getText().toString();
                State = spstate.getSelectedItem().toString();
                SRName = etsrname.getText().toString();
                SOName = etsoname.getText().toString();
                ConName = etconname.getText().toString();
                Address = etaddress.getText().toString();
                PhoneNum = etphonenum.getText().toString();
                Powder1 = etpowder1.getText().toString();
                Powder2 = etpowder.getText().toString();
                Powder3 = etpowdersam1.getText().toString();
                Powder4 = etpowdersam.getText().toString();

                if (TextUtils.isEmpty(encodedResume1)) {
                    encodedResume1 = " ";
                }
                if (TextUtils.isEmpty(encodedResume2)) {
                    encodedResume2 = " ";
                }
                if (TextUtils.isEmpty(encodedResumesignature)) {
                    encodedResumesignature = " ";
                }



                if (DisName.length() < 0) {
                    etdisname.setError("Please Enter Distributor");
                } else if (Address.length() < 0) {
                    etaddress.setError("Please Enter Address");
                } else if (Town.length() < 0) {
                    ettown.setError("Please Enter Town");
                } else if (City.length() < 0) {
                    etcity.setError("Please Enter City");
                } else if (State.equals("Select State")){
                    Toast.makeText(ConsumerDataForm.this, "Please Select State", Toast.LENGTH_SHORT).show();
                }else if (MobileNum.length() < 10) {
                    etmobnum.setError("Please enter 10 digits");
                } else if (Powder1.length() < 0) {
                    etpowder1.setError("Please enter details");
                } else if (Powder2.length() < 0) {
                    etpowder.setError("Please enter details");
                } else if (Powder3.length() < 0) {
                    etpowdersam1.setError("Please enter details");
                } else if (Powder4.length() < 0) {
                    etpowdersam.setError("Please enter details");
                } else if (NetworkUtils.isConnected(ConsumerDataForm.this)){
                    if (TextUtils.isEmpty(Edit)){
                        Submit();
                    }else if (Edit.equals("EDIT")){
                       Update();
                    }
                }else {
                    Toast.makeText(ConsumerDataForm.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                }


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


    }

    private void OpenSignatureDialog() {

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
                //Toast.makeText(ConsumerDataForm.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
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

    public void Submit() {

        String LatLong=Latitude+","+Longitude;

        LogUtils.showLog("latlongg",""+LatLong);

        pdLoading = new ProgressDialog(ConsumerDataForm.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("Town",Town);
            object.put("City",City);
            object.put("State",State);
            object.put("ConsumerName",DisName);
            object.put("ConsumerAddress",Address);
            object.put("ConsumerPhoneno",MobileNum);
            object.put("ProductSold1kg",Powder1);
            object.put("ProductSold500g",Powder2);
            object.put("ProductSample1kg",Powder3);
            object.put("ProductSample500g",Powder4);
            object.put("lat",LatLong);
            object.put("lon",FullAddress);
            object.put("UID",Uid);
            object.put("DistributorCode",Discode);
            object.put("Image1", encodedResume1);
            object.put("Image2",encodedResume2);
            object.put("Signature",encodedResumesignature);
            object.put("Ext1",".jpg");
            object.put("Ext2",".jpg");
            object.put("Ext3",".jpg");

            Log.i("dattatjson", "" + object);
            final String requestBody = object.toString();

            Log.i("rbody", "" + requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.ConsumerDataForm, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYResponce", response);

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
                bitmap1.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                encodedResume1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                LogUtils.showLog("bitmap1", "" + encodedResume1);


                imageView.setImageBitmap(bitmap1);
                btnimage1.setText("Selected");
                btnimage2.setVisibility(View.VISIBLE);
            }

        } else if (requestCode == CAMERA2) {
            if (data != null) {

                bitmap2 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encodedResume2 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                LogUtils.showLog("bitmap2", "" + encodedResume2);
                btnimage2.setText("Selected");
                // String path = saveImage(bitmap);
            }


        }
    }

    public void GetEditData(){
        final SharedPreferences prefss = getSharedPreferences("Login", MODE_PRIVATE);
        String Type = prefss.getString("type", null);


        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("TYPE","C");
            object.put("ID", Id);


            Log.i("EidtjsonResasbt", "" + object);
            final String requestBody = object.toString();

            Log.i("rsdbody", "" + requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.RetGetEditData, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYResponceeditret", response);
                    btnimage1.setVisibility(View.GONE);
                    btnsig.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String cityy=jsonObject.getString("City");
                        String Consumer_Addresss=jsonObject.getString("Consumer_Address");
                        String Consumer_Namee=jsonObject.getString("Consumer_Name");
                        String Consumer_Phonenoo=jsonObject.getString("Consumer_Phoneno");
                        String Distributor_Codee=jsonObject.getString("Distributor_Code");
                        String ID=jsonObject.getString("ID");
                        String Product_Sample_1kg=jsonObject.getString("Product_Sample_1kg");
                        String Product_Sample_500g=jsonObject.getString("Product_Sample_500g");
                        String Product_Sold_1kg=jsonObject.getString("Product_Sold_1kg");
                        String Product_Sold_500g=jsonObject.getString("Product_Sold_500g");
                        String Townn=jsonObject.getString("Town");
                        String StateString=jsonObject.getString("State");

                        etcity.setText(cityy);
                        etaddress.setText(Consumer_Addresss);
                        etdisname.setText(Consumer_Namee);
                        etmobnum.setText(Consumer_Phonenoo);
                        etpowder1.setText(Product_Sold_1kg);
                        etpowder.setText(Product_Sold_500g);
                        etpowdersam1.setText(Product_Sample_1kg);
                        etpowdersam.setText(Product_Sample_500g);
                        ettown.setText(Townn);
                        int pos = new ArrayList<String>(Arrays.asList(Stateee)).indexOf(StateString);
                        spstate.setSelection(pos);


                    }catch (Exception ex){

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
    public  void Update(){
        pdLoading = new ProgressDialog(ConsumerDataForm.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("Town",Town);
            object.put("City",City);
            object.put("State",State);
            object.put("ID",Id);
            object.put("ConsumerName",DisName);
            object.put("ConsumerAddress",Address);
            object.put("ConsumerPhoneno",MobileNum);
            object.put("Type","C");
            object.put("ProductSold1kg",Powder1);
            object.put("ProductSold500g",Powder2);
            object.put("ProductSample1kg",Powder3);
            object.put("ProductSample500g",Powder4);


            Log.i("dattatjkjghdkjsason", "" + object);
            final String requestBody = object.toString();

            Log.i("rbody", "" + requestBody);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.updatedate, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYResponcesajhjgads", response);

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

    private void turnGPSOn(){
        Toast.makeText(ConsumerDataForm.this, "Please turn on your location.", Toast.LENGTH_SHORT).show();
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);

    }


}