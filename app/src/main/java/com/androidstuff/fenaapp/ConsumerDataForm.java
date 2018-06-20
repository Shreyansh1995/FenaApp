package com.androidstuff.fenaapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.androidstuff.fenaapp.ClassUtills.MyApplication;
import com.androidstuff.fenaapp.ClassUtills.ResponseListener;
import com.androidstuff.fenaapp.ClassUtills.Vrequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
            Powder1, Powder2, Powder3, Powder4;
    private Button btnsubmit;
    private Context mcontext;
    private ProgressDialog pdLoading;
    private String Longitude,Latitude;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_data_form);

        mcontext=this;

        final SharedPreferences prefss = getSharedPreferences("latlong", MODE_PRIVATE);
        Longitude = prefss.getString("lat", null);
        Latitude = prefss.getString("laong", null);

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

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date=etdate.getText().toString();
                ProName=etproname.getText().toString();
                AgencyName=etagencyname.getText().toString();
                DisName=etdisname.getText().toString();
                MobileNum=etmobnum.getText().toString();
                ContactPerson=etconperson.getText().toString();
                Town=ettown.getText().toString();
                City=etcity.getText().toString();
                State=spstate.getSelectedItem().toString();
                SRName=etsrname.getText().toString();
                SOName=etsoname.getText().toString();
                ConName=etconname.getText().toString();
                Address=etaddress.getText().toString();
                PhoneNum=etphonenum.getText().toString();
                Powder1=etpowder1.getText().toString();
                Powder2=etpowder.getText().toString();
                Powder3=etpowdersam1.getText().toString();
                Powder4=etpowdersam.getText().toString();

                if (Date.length() < 0){
                    etdate.setError("Please Select Date");
                }else if (ProName.length() < 0 ){
                    etproname.setError("Please enter Promotor's Name");
                }else if (AgencyName.length() < 0){
                    etagencyname.setError("Please enter Agency Name");
                } else if (DisName.length() < 0){
                    etdisname.setError("Please enter Distributor's Name");
                }else if (MobileNum.length() < 10){
                    etmobnum.setError("Please enter 10 digit Number");
                }else if (ContactPerson.length() < 0){
                    etconperson.setError("Please enter Contact Person Name");
                }else if (Town.length() < 0){
                    ettown.setError("Please enter Town");
                }else if (City.length() < 0){
                    etcity.setError("Please enter City");
                }else if (Address.length() < 0){
                    etaddress.setError("Please enter Address");
                }else if (SRName.length() < 0){
                    etsrname.setError("Please enter SR Name");
                }else if (SOName.length() < 0){
                    etsoname.setError("Please enter SO Name");
                }else if (ConName.length() < 0){
                    etconname.setError("Please enter Consumer Name");
                }else if (
                        PhoneNum.length() < 10){
                    etphonenum.setError("Please enter Phone Number");
                }else {

                    Submit();
                }

            }
        });


    }



   public void Submit() {

       try {
           RequestQueue requestQueue = Volley.newRequestQueue(this);
           JSONObject object = new JSONObject();
           object.put("token", "qwertyuiop");
           object.put("date", Date);
           object.put("unique_id", "1");
           object.put("latitude", "");
           object.put("longitude", "");
           object.put("promoter_name", ProName);
           object.put("agency_name", AgencyName);
           object.put("distributor_name", DisName);
           object.put("distributor_mobile", MobileNum);
           object.put("contact_person", ContactPerson);
           object.put("locality", Town);
           object.put("city", City);
           object.put("state", State);
           object.put("sr_name", SRName);
           object.put("so_name", SOName);
           object.put("consumer_name", ConName);
           object.put("consumer_address", Address);
           object.put("consumer_mobile", PhoneNum);
           object.put("prod_sold_1", Powder1);
           object.put("prod_sold_2", Powder2);
           object.put("prod_sample_1", Powder3);
           object.put("prod_sample_2", Powder4);

           Log.i("conformdatajson", "" + object);
           final String requestBody = object.toString();

           StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.ConsumerDataForm, new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   Log.i("VOLLEY", response);
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

           requestQueue.add(stringRequest);
       } catch (JSONException e) {
           e.printStackTrace();
       }
   }


}
