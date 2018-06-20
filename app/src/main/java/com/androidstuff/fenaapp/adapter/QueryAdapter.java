package com.androidstuff.fenaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstuff.fenaapp.R;
import com.androidstuff.fenaapp.model.QueryListModel;

import java.util.ArrayList;

/**
 * Created by Flash_Netcomm on 6/16/2018.
 */

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.QueryListHolder> {
    private ArrayList<QueryListModel> queryListModelArrayList;
    Context context;
    private Activity activity;

    public QueryAdapter(ArrayList<QueryListModel> queryListModelArrayList, Context context, Activity activity) {
        this.queryListModelArrayList = queryListModelArrayList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public QueryAdapter.QueryListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.querylist_row, parent, false);
        return new QueryListHolder(itemView, context, queryListModelArrayList);
    }

    @Override
    public void onBindViewHolder(QueryAdapter.QueryListHolder holder, int position) {

        QueryListModel queryListModel=queryListModelArrayList.get(position);
        holder.tvname.setText(queryListModelArrayList.get(position).getName());
        holder.tvdate.setText(queryListModelArrayList.get(position).getDate());
        holder.tvid.setText(queryListModelArrayList.get(position).getId());
        holder.tvadd.setText(queryListModelArrayList.get(position).getAddress());

    }

    @Override
    public int getItemCount() {
        if (queryListModelArrayList == null)
            return 0;
        return queryListModelArrayList.size();
    }

    public class QueryListHolder extends RecyclerView.ViewHolder {
        TextView tvdate,tvname,tvid,tvadd;
        ImageView ivedit,ivdelete;

        public QueryListHolder(View itemView, Context context, ArrayList<QueryListModel> queryListModelArrayList) {
            super(itemView);

            tvname=(TextView)itemView.findViewById(R.id.tv_namelist);
            tvdate=(TextView)itemView.findViewById(R.id.tv_datelist);
            tvid=(TextView)itemView.findViewById(R.id.tv_id);
            tvadd=(TextView)itemView.findViewById(R.id.tv_addlist);
            ivedit=(ImageView)itemView.findViewById(R.id.iv_editlist);
            ivdelete=(ImageView)itemView.findViewById(R.id.iv_deletelist);


        }
    }
}
