package com.androidstuff.fenaapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.androidstuff.fenaapp.adapter.QueryAdapter;
import com.androidstuff.fenaapp.model.QueryListModel;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class RetailerQueryList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QueryAdapter queryAdapter;
    private ArrayList<QueryListModel> queryListModelArrayList;
    private Context mContext;
    private Button btnadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_query_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarretlist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rv_querylist);
        btnadd=(Button)findViewById(R.id.btn_addnew);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.setAdapter(allergyAdapter);
        queryListModelArrayList = new ArrayList<>();
        queryAdapter = new QueryAdapter(queryListModelArrayList, mContext, this);

        QueryListModel queryListModel=new QueryListModel();
        queryListModel.setName("Shreyansh srivastava");
        queryListModel.setDate("1 Sept 95");
        queryListModel.setId("1");
        queryListModel.setAddress("Noida Sec 19");

        queryListModelArrayList.add(queryListModel);
        recyclerView.setAdapter(queryAdapter);
        queryAdapter.notifyDataSetChanged();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RetailerQueryList.this,ConsumerDataForm.class);
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
