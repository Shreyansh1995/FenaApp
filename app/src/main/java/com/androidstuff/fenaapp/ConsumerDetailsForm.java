package com.androidstuff.fenaapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class ConsumerDetailsForm extends AppCompatActivity {

    private EditText etname,etemail,etnum,etdisname,etagencyname;
    private String Name,Email,Number,DisName,AgencyName;
    private Button btnsubmit;


    private Locale locale;
    private static final String LOCALE_KEY = "localekey";
    private static final String HINDI_LOCALE = "hi";
    private static final String ENGLISH_LOCALE = "en_US";
    private static final String LOCALE_PREF_KEY = "localePref";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_detail_form);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etname=(EditText)findViewById(R.id.et_consumername);
        etemail=(EditText)findViewById(R.id.et_consumeremail);
        etnum=(EditText)findViewById(R.id.et_consumernum);
        etdisname=(EditText)findViewById(R.id.et_consumerdisname);
        etagencyname=(EditText)findViewById(R.id.et_consumeragencyname);
        btnsubmit=(Button)findViewById(R.id.btn_consumerdetsubmit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name=etname.getText().toString();
                Email=etemail.getText().toString();
                Number=etnum.getText().toString();
                DisName=etdisname.getText().toString();
                AgencyName=etagencyname.getText().toString();

                Intent intent= new Intent(ConsumerDetailsForm.this,RetailerDataForm.class);
                startActivity(intent);

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
