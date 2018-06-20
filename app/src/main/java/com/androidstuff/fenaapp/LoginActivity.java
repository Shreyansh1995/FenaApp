package com.androidstuff.fenaapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements LocationListener {

    private RadioGroup radiotype;
    private RadioButton rbretailer, rbhousehold;
    private RadioGroup radiolanguage;
    private RadioButton rbhindi, rbenglish;
    private Button btncontinue;
    private EditText etcode;
    private String Code;
    private String Type, Language;


    private static final String LOCALE_KEY = "localekey";
    private static final String HINDI_LOCALE = "hi";
    private static final String ENGLISH_LOCALE = "en_US";
    private static final String LOCALE_PREF_KEY = "localePref";
    private Locale locale;

    private LocationManager locationManager;

    private double Longitude,Latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getLocation();

        locale = new Locale(ENGLISH_LOCALE);

        //        Fetching sharedpreferences to get Locale stored in them
        SharedPreferences sp = getSharedPreferences(LOCALE_PREF_KEY, MODE_PRIVATE);
        String localeString = sp.getString(LOCALE_KEY, ENGLISH_LOCALE);

        final Resources resources = getResources();
        final SharedPreferences sharedPreferences = getSharedPreferences("localePref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();




        //===============================================================================================================

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }



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

                Intent intent = new Intent(LoginActivity.this, ConsumerDetailsForm.class);
                startActivity(intent);

                locale.setDefault(locale);
                Resources resources = getResources();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());


                Log.i("RadioValues", "Type" + Type + "Language" + Language);

            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
     //   locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        Longitude=location.getLongitude();
        Latitude=location.getLatitude();

        String lon, lat;
        lat = String.valueOf(Latitude);
        lon = String.valueOf(Longitude);

        SharedPreferences sharedPreferencess = getApplicationContext().getSharedPreferences("latlong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorr = sharedPreferencess.edit();
        editorr.putString("lat", lat);
        editorr.putString("laong", lon);
        editorr.commit();




        Toast.makeText(this, "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            /*locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));*/
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(LoginActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

}
