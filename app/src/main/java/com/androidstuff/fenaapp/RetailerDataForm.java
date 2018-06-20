package com.androidstuff.fenaapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_data_form);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarret);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                Intent intent= new Intent(RetailerDataForm.this,ConsumerDataForm.class);
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
