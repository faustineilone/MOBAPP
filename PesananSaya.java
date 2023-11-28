package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PesananSaya extends AppCompatActivity {
    ArrayList<Order> orders = new ArrayList<>();
    ImageView btnBack;
    ArrayList<HashMap<String, String>> list_detail_pesanan;
    ListView lv;
    int salesID;
    String statusName, username, orderTime, finishedTime, receiverName, receiverPhone, receiverAddress, receiverPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_saya);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_detail_pesanan = new ArrayList<>();
        lv = findViewById(R.id.listViewPesanan);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = list_detail_pesanan.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("salesID", Integer.parseInt(map.get("salesID")));
                bundle.putString("statusName", map.get("statusName"));
                bundle.putString("orderTime", map.get("orderTime"));
                bundle.putString("finishedTime", map.get("finishedTime"));
                bundle.putString("receiverName", receiverName);
                bundle.putString("receiverPhone", receiverPhone);
                bundle.putString("receiverAddress", receiverAddress);
                bundle.putString("receiverPostalCode", receiverPostalCode);
                Bundle b = getIntent().getExtras();
                username = b.getString("username");
                bundle.putString("username", username);
                if (map.get("statusName").equals("On-Progress")){
                    Intent i = new Intent(getApplicationContext(), DetailPesanan.class);
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (map.get("statusName").equals("Finished")) {
                    Intent i = new Intent(getApplicationContext(), DetailPesananFinished.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

        getHistory();
    }

    private void getHistory(){
        RequestQueue queue = Volley.newRequestQueue(PesananSaya.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_GET_SALES_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jObj = new JSONObject(response);
                            JSONArray member = jObj.getJSONArray("salesorder");
                            int len = member.length();
                            for(int i = 0; i < len; i++) {
                                JSONObject a = member.getJSONObject(i);

                                salesID = a.getInt("salesID");
                                receiverName = a.getString("receiverName");
                                receiverPhone = a.getString("receiverPhone");
                                receiverAddress = a.getString("receiverAddress");
                                receiverPostalCode = a.getString("receiverPostalCode");
                                statusName = a.getString("statusName");
                                orderTime = a.getString("orderTime");
                                finishedTime = a.getString("finishedTime");

                                HashMap<String, String> map = new HashMap<>();
                                map.put("salesID", String.valueOf(salesID));
                                map.put("statusName", statusName);
                                map.put("orderTime", orderTime);
                                map.put("finishedTime", finishedTime);

                                list_detail_pesanan.add(map);
                            }

                            ListAdapter adapter_lv = new SimpleAdapter(PesananSaya.this, list_detail_pesanan, R.layout.list_pesanan, new String[]{"salesID", "statusName", "orderTime"}, new int[]{R.id.txtSalesID, R.id.txtStatusName, R.id.txtOrderTime});
                            lv.setAdapter(adapter_lv);
                        }catch (Exception e){
                            Log.e("Error: ", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                params.put("username", username);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}