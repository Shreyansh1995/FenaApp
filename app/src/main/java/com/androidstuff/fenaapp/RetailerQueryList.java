package com.androidstuff.fenaapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.androidstuff.fenaapp.adapter.QueryAdapter;
import com.androidstuff.fenaapp.model.QueryListModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static java.security.AccessController.getContext;

public class RetailerQueryList extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private RecyclerView recyclerView;
    private QueryAdapter queryAdapter;
    private ArrayList<QueryListModel> queryListModelArrayList;
    private Context mContext;
    private Button btnadd;
    private ProgressDialog pdLoading;
    private String Type, UID;
    private Toolbar toolbar;


    private double Longitude, Latitude;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    private List<Address> addresses;
    private ImageView ivlogout;
    private TextView tvtnq;

    private String AlertMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_query_list);

        final SharedPreferences prefss = getSharedPreferences("Login", MODE_PRIVATE);
        Type = prefss.getString("type", null);
        if (Type.equals("Retailer")) {
            Type = "R";
        } else if (Type.equals("House Hold")) {
            Type = "C";
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarretlist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (NetworkUtils.isConnected(RetailerQueryList.this)) {
//            getLocation();
            buildGoogleApiClient();

        } else {
            Toast.makeText(this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
        }


        final SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
        UID = uid.getString("uid", null);


        recyclerView = (RecyclerView) findViewById(R.id.rv_querylist);
        btnadd = (Button) findViewById(R.id.btn_addnew);
        ivlogout = (ImageView) findViewById(R.id.iv_logout);
        tvtnq = (TextView) findViewById(R.id.tv_tnq);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.setAdapter(allergyAdapter);
        queryListModelArrayList = new ArrayList<>();
        queryAdapter = new QueryAdapter(queryListModelArrayList, mContext, this);


        if (NetworkUtils.isConnected(RetailerQueryList.this)) {
            GetListC();
            GetCount();
            GetMsg();
        } else {
            Toast.makeText(RetailerQueryList.this, "Please Check your internet connection!", Toast.LENGTH_SHORT).show();
        }


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Type.equals("R")) {
                    Intent intent = new Intent(RetailerQueryList.this, RetailerDataForm.class);
                    startActivity(intent);
                } else if (Type.equals("C")) {
                    Intent intent = new Intent(RetailerQueryList.this, ConsumerDataForm.class);
                    startActivity(intent);
                }
            }
        });

        ivlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog();
            }
        });
    }

    private void GetCount() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("Type",Type);
            object.put("UID",UID);


            Log.i("countjson", "" + object);
            final String requestBody = object.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.count, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYCount", response);
                    try {

                        JSONObject jsonObject=new JSONObject(response);
                        String Count=jsonObject.getString("RSCODE");
                        tvtnq.setText(Count);

                    }catch (Exception ex){

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEYCount", error.toString());
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

    private void OpenDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(AlertMsg);
        alertDialogBuilder.setPositiveButton("Log Out",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                       Logout();

                       Intent intent=new Intent(RetailerQueryList.this,LoginActivity.class);
                       startActivity(intent);
                       finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void Logout() {

        SharedPreferences preferences =getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        SharedPreferences preferen =getSharedPreferences("uid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorr = preferen.edit();
        editorr.clear();
        editorr.commit();


    }

    private void GetListC() {

        queryListModelArrayList.clear();
        pdLoading = new ProgressDialog(RetailerQueryList.this);
        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("TYPE", Type);
            object.put("UID", UID);


            Log.i("listjson", "" + object);
            final String requestBody = object.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.GetList, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    pdLoading.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("ConsumerandRetailerInquery");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String CID = jsonObject1.getString("CID");
                            String Consumer_Name = jsonObject1.getString("Consumer_Name");
                            String SubmitDate = jsonObject1.getString("SubmitDate");
                            String Town = jsonObject1.getString("Town");

                            StringTokenizer tk = new StringTokenizer(SubmitDate);

                            String date = tk.nextToken();  // <---  yyyy-mm-dd
                            String time = tk.nextToken();

                            QueryListModel queryListModel = new QueryListModel();
                            queryListModel.setName(Consumer_Name);
                            queryListModel.setDate(date);
                            queryListModel.setId(CID);
                            queryListModel.setAddress(Town);
                            queryListModelArrayList.add(queryListModel);
                        }
                        recyclerView.setAdapter(queryAdapter);
                        queryAdapter.notifyDataSetChanged();

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GetListC();
        GetCount();
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

            Latitude = mLastLocation.getLatitude();
            Longitude = mLastLocation.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String City = addresses.get(0).getLocality();
                String State = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();

                LogUtils.showLog("addressgps", "area" + State + "city" + City);

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        Latitude = location.getLatitude();
        Longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String City = addresses.get(0).getLocality();
            String State = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            LogUtils.showLog("addressgps", "area" + State + "city" + City);

            SharedPreferences sharedPreferencess = getApplicationContext().getSharedPreferences("latlong", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorr = sharedPreferencess.edit();
            editorr.putString("lat", lat);
            editorr.putString("laong", lon);
            editorr.putString("city", City);
            editorr.putString("state", State);
            editorr.putString("fulladd", address + "," + City + "," + State + "," + country);
            editorr.commit();
        } catch (Exception e) {
        }


        updateUI();
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

    private void updateUI() {

        // Toast.makeText(this, "" + lat + "" + lon, Toast.LENGTH_SHORT).show();
    }

    private void GetMsg() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject object = new JSONObject();
            object.put("TokenNo", "smn@ncl#fena1234");
            object.put("TYPE", Type);
            object.put("UID", UID);


            Log.i("alertjson", "" + object);
            final String requestBody = object.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.alert, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEYalert", response);

                   try {
                       JSONObject jsonObject=new JSONObject(response);
                       AlertMsg=jsonObject.getString("Message");

                   }catch (Exception ex){

                   }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEYalert", error.toString());
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
