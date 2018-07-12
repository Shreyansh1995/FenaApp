package com.androidstuff.fenaapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private RadioGroup radiotype;
    private RadioButton rbretailer, rbhousehold;
    private RadioGroup radiolanguage;
    private RadioButton rbhindi, rbenglish;
    private Button btncontinue;
    private EditText etcode;
    private String Code;
    private String Type, Language;
    private Context ctx;


    private static final String LOCALE_KEY = "localekey";
    private static final String HINDI_LOCALE = "hi";
    private static final String ENGLISH_LOCALE = "en_US";
    private static final String LOCALE_PREF_KEY = "localePref";
    private Locale locale;

    private LocationManager locationManager;

    private double Longitude, Latitude;

    private ProgressDialog pdLoading, pdLoadingg;
    private List<Address> addresses;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private String DisName, DisCode;


    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
           // Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            turnGPSOn();
        }

        final SharedPreferences prefss = getSharedPreferences("Login", MODE_PRIVATE);
        DisName = prefss.getString("rsname", null);
        DisCode = prefss.getString("rscode", null);
        if (!TextUtils.isEmpty(DisName)) {
            Intent intent = new Intent(LoginActivity.this, ConsumerDetailsForm.class);
            startActivity(intent);
            finish();
        }


        locale = new Locale(ENGLISH_LOCALE);

        //        Fetching sharedpreferences to get Locale stored in them
        SharedPreferences sp = getSharedPreferences(LOCALE_PREF_KEY, MODE_PRIVATE);
        String localeString = sp.getString(LOCALE_KEY, ENGLISH_LOCALE);

        final Resources resources = getResources();
        final SharedPreferences sharedPreferences = getSharedPreferences("localePref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();


        //===============================================================================================================

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(CAMERA);


        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        if (NetworkUtils.isConnected(LoginActivity.this)) {
//            getLocation();
            buildGoogleApiClient();

        } else {
            Toast.makeText(this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        //================================================================================================================================


        radiotype = (RadioGroup) findViewById(R.id.radiotype);
        radiolanguage = (RadioGroup) findViewById(R.id.radiolanguage);
        rbretailer = (RadioButton) findViewById(R.id.radioretailer);
        rbhousehold = (RadioButton) findViewById(R.id.radiohousehold);
        rbhindi = (RadioButton) findViewById(R.id.radiorehindi);
        rbenglish = (RadioButton) findViewById(R.id.radioenglish);
        etcode = (EditText) findViewById(R.id.et_retailercode);
        btncontinue = (Button) findViewById(R.id.btn_logincontinue);

        //-----------------------------------------------------------------------------------------------------------------------
        radiotype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioretailer) {
                    Type = "Retailer";
                } else if (i == R.id.radiohousehold) {
                    Type = "House Hold";
                }
            }
        });

        radiolanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radiorehindi) {
                    //Language="Hindi";
                    locale = new Locale(HINDI_LOCALE);
                    editor.putString(LOCALE_KEY, HINDI_LOCALE);
                } else if (i == R.id.radioenglish) {
                    //Language="English";
                    locale = new Locale(ENGLISH_LOCALE);
                    editor.putString(LOCALE_KEY, ENGLISH_LOCALE);
                }
                editor.apply();
            }
        });


        //------------------------------------------------------------------------------------------------------------------
        btncontinue.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {


                locale.setDefault(locale);
                Resources resources = getResources();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());

                if (radiotype.getCheckedRadioButtonId() == -1) {
                    // no radio buttons are checked
                    Toast.makeText(LoginActivity.this, "Please choose Sampling Type", Toast.LENGTH_SHORT).show();
                } else if (NetworkUtils.isConnected(LoginActivity.this)) {
                    SubmitRsCode();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void SubmitRsCode() {

        pdLoading = new ProgressDialog(LoginActivity.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("RSCODE", etcode.getText().toString());

            Log.i("SignInjson", "" + object);
            final String requestBody = object.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.SignIn, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    pdLoading.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Message = jsonObject.getString("Message");
                        String RSCODE = jsonObject.getString("RSCODE");
                        String RS_Name = jsonObject.getString("RS_Name");
                        String Status = jsonObject.getString("Status");

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("rscode", RSCODE);
                        editor.putString("rsname", RS_Name);
                        editor.putString("type", Type);
                        editor.commit();

                        if (Message.equals("Success")) {
                            Intent intent = new Intent(LoginActivity.this, ConsumerDetailsForm.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please Enter Correct Distributor Code", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {

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


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        Latitude=location.getLatitude();
        Longitude=location.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String City = addresses.get(0).getLocality();
            String State = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            LogUtils.showLog("addressgps","area"+State+"city"+City);

            SharedPreferences sharedPreferencess = getApplicationContext().getSharedPreferences("latlong", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorr = sharedPreferencess.edit();
            editorr.putString("lat", lat);
            editorr.putString("laong", lon);
            editorr.putString("city", City);
            editorr.putString("state", State);
            editorr.putString("fulladd", address+","+City+","+State+","+country);
            editorr.commit();
        } catch (Exception e) {
        }


        updateUI();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());

            Latitude=mLastLocation.getLatitude();
            Longitude=mLastLocation.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String City = addresses.get(0).getLocality();
                String State = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();

                LogUtils.showLog("addressgps","area"+State+"city"+City);

                SharedPreferences sharedPreferencess = getApplicationContext().getSharedPreferences("latlong", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedPreferencess.edit();
                editorr.putString("lat", lat);
                editorr.putString("laong", lon);
                editorr.putString("city", City);
                editorr.putString("state", State);
                editorr.commit();
            } catch (Exception e) {
            }

        }
        updateUI();

    }

    private void updateUI() {

       // Toast.makeText(this, "" + lat + "" + lon, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
    private void turnGPSOn(){
        Toast.makeText(LoginActivity.this, "Please turn on your location.", Toast.LENGTH_SHORT).show();
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);

    }
}