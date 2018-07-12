package com.androidstuff.fenaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.androidstuff.fenaapp.ConsumerDataForm;
import com.androidstuff.fenaapp.R;
import com.androidstuff.fenaapp.RetailerDataForm;
import com.androidstuff.fenaapp.model.QueryListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Flash_Netcomm on 6/16/2018.
 */

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.QueryListHolder> {
    private ArrayList<QueryListModel> queryListModelArrayList;
    Context context;
    private Activity activity;
    private String Type;

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

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        if (holder.tvdate.getText().equals(date)){
            holder.ivedit.setVisibility(View.VISIBLE);
            holder.ivdelete.setVisibility(View.VISIBLE);

            LogUtils.showLog("visi","date"+date+"dateid"+holder.tvdate.getText());
        }


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

        public QueryListHolder(View itemView, Context context, final ArrayList<QueryListModel> queryListModelArrayList) {
            super(itemView);

            tvname=(TextView)itemView.findViewById(R.id.tv_namelist);
            tvdate=(TextView)itemView.findViewById(R.id.tv_datelist);
            tvid=(TextView)itemView.findViewById(R.id.tv_id);
            tvadd=(TextView)itemView.findViewById(R.id.tv_addlist);
            ivedit=(ImageView)itemView.findViewById(R.id.iv_editlist);
            ivdelete=(ImageView)itemView.findViewById(R.id.iv_deletelist);
            
            ivdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final SharedPreferences prefss = activity.getSharedPreferences("Login", MODE_PRIVATE);
                    Type = prefss.getString("type", null);

                    if (Type.equals("Retailer")){
                        Type="R";
                    }else if (Type.equals("House Hold")){
                        Type="C";
                    }

                    DeleteList();
                }
            });

            ivedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final SharedPreferences prefss = activity.getSharedPreferences("Login", MODE_PRIVATE);
                    Type = prefss.getString("type", null);
                    //Toast.makeText(activity, "Clicked"+Type, Toast.LENGTH_SHORT).show();

                    int Poss=getAdapterPosition();

                    if (Type.equals("Retailer")){
                        Intent intent = new Intent(activity,RetailerDataForm.class);
                        intent.putExtra("edit","EDIT");
                        intent.putExtra("id",queryListModelArrayList.get(Poss).getId());
                        activity.startActivity(intent);
                    }else if (Type.equals("House Hold")){
                        Intent intent = new Intent(activity,ConsumerDataForm.class);
                        intent.putExtra("edit","EDIT");
                        intent.putExtra("id",queryListModelArrayList.get(Poss).getId());
                        activity.startActivity(intent);
                    }




                }
            });
            
        }

        private void DeleteList() {

            int pos=getAdapterPosition();

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(activity);
                JSONObject object = new JSONObject();
                object.put("TokenNo", "smn@ncl#fena1234");
                object.put("TYPE", Type);
                object.put("ID",queryListModelArrayList.get(pos).getId());


                Log.i("listjson", "" + object);
                final String requestBody = object.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfigInfo.DeleteList, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                        removeAt(getAdapterPosition());
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



    public void removeAt(int position) {
        queryListModelArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, queryListModelArrayList.size());
    }
}
