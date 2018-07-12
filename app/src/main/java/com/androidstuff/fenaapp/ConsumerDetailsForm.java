package com.androidstuff.fenaapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidstuff.fenaapp.ClassUtills.ConfigInfo;
import com.androidstuff.fenaapp.ClassUtills.LogUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class ConsumerDetailsForm extends AppCompatActivity {

    private EditText etname,etemail,etnum,etdisname,etagencyname,ettown,etcity,etsrname,etsoname;
    private String Name,Email,Number,DisName,AgencyName,DisCode,Type,City,State,Town,SrName,SoName;
    private Button btnsubmit;
    private ProgressDialog pdLoading;
    private String refreshedToken,UID;
    private Spinner spState;


  /*  private Locale locale;
    private static final String LOCALE_KEY = "localekey";
    private static final String HINDI_LOCALE = "hi";
    private static final String ENGLISH_LOCALE = "en_US";
    private static final String LOCALE_PREF_KEY = "localePref";*/

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_detail_form);


        final SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
        UID = uid.getString("uid", null);

        if (!TextUtils.isEmpty(UID)){
            Intent intent=new Intent(ConsumerDetailsForm.this,RetailerQueryList.class);
            startActivity(intent);
            finish();
        }


       /* final SharedPreferences preflocation = getSharedPreferences("latlong", MODE_PRIVATE);
        City = preflocation.getString("city", null);
        State = preflocation.getString("state", null);*/


        final SharedPreferences prefss = getSharedPreferences("Login", MODE_PRIVATE);
        DisName = prefss.getString("rsname", null);
        DisCode = prefss.getString("rscode", null);
        Type = prefss.getString("type", null);

        if (Type.equals("Retailer")){
            Type="R";
        }else if (Type.equals("House Hold")){
            Type="C";
        }

         refreshedToken = FirebaseInstanceId.getInstance().getToken();
        LogUtils.showLog("token",""+refreshedToken);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etname=(EditText)findViewById(R.id.et_consumername);
        etemail=(EditText)findViewById(R.id.et_consumeremail);
        etnum=(EditText)findViewById(R.id.et_consumernum);
        etdisname=(EditText)findViewById(R.id.et_consumerdisname);
        etagencyname=(EditText)findViewById(R.id.et_consumeragencyname);
        ettown=(EditText)findViewById(R.id.et_consumertown);
        etcity=(EditText)findViewById(R.id.et_consumercity);
        etsrname=(EditText)findViewById(R.id.et_consumersrname);
        etsoname=(EditText)findViewById(R.id.et_consumersoname);
        spState=(Spinner) findViewById(R.id.sp_consumerstate);

        btnsubmit=(Button)findViewById(R.id.btn_consumerdetsubmit);

        etdisname.setText(DisName);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name=etname.getText().toString();
                Email=etemail.getText().toString();
                Number=etnum.getText().toString();
                DisName=etdisname.getText().toString();
                AgencyName=etagencyname.getText().toString();
                Town=ettown.getText().toString();
                City=etcity.getText().toString();
                State=spState.getSelectedItem().toString();
                SrName=etsrname.getText().toString();
                SoName=etsoname.getText().toString();

                if (Name.length() == 0){
                    etname.setError("Please enter your Name");
                }else if (Number.length() < 10 || Number.length() > 10){
                    etnum.setError("Please enter 10 Digits Number");
                }else if (Town.length() ==  0){
                    ettown.setError("Plaese enter Town");
                }else if (City.length()==0){
                    etcity.setError("Please enter City");
                }else if (State.length() == 0){
                    Toast.makeText(ConsumerDetailsForm.this, "Please Choose State", Toast.LENGTH_SHORT).show();
                }else if (AgencyName.length() == 0){
                    etagencyname.setError("Please enter Agegncy Name");
                }else if (SrName.length() == 0){
                    etsrname.setError("Please enter SR Name ");
                }else if (SoName.length() == 0){
                    etsoname.setError("Please Enter SO Name");
                }else {
                    SubmitForm();
                }


            }
        });
    }

    private void SubmitForm() {


        pdLoading = new ProgressDialog(ConsumerDetailsForm.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("DistributarCode",DisCode);
            object.put("Name",Name);
            object.put("mobile",Number );
            object.put("DistributarName",DisName );
            object.put("State",State);
            object.put("Town",Town);
            object.put("Type",Type );
            object.put("FenaSRName",SrName );
            object.put("FenaSOName",SoName );
            object.put("AgencyName",AgencyName );
            object.put("City",City);
            object.put("DeviceID",refreshedToken );



            Log.i("conregisterjson", "" + object);
            final String requestBody = object.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.ConFENARegister, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    pdLoading.dismiss();
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String Message=jsonObject.getString("Message");
                        String UID=jsonObject.getString("UID");

                        if (Message.equals("Success")){
                            Intent intent=new Intent(ConsumerDetailsForm.this,RetailerQueryList.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(ConsumerDetailsForm.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                        SharedPreferences sharedPreferencess = getApplicationContext().getSharedPreferences("uid", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorr = sharedPreferencess.edit();
                        editorr.putString("uid", UID);
                        editorr.commit();
                    }catch (Exception ex){

                    }

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
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
