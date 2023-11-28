package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class DaftarAlamat extends AppCompatActivity {
    Button btnTambahAlamat;
    ListView lv;
    ArrayList<HashMap<String, String>> list_address;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_alamat);

        btnTambahAlamat = findViewById(R.id.btnTambahAlamat);
        btnTambahAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TambahAlamat.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        list_address = new ArrayList<>();
        lv = findViewById(R.id.listViewAlamat);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), EditAlamat.class);
                HashMap<String, String> map = list_address.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("addressID", Integer.parseInt(map.get("addressID")));
                bundle.putInt("custID", Integer.parseInt(map.get("custID")));
                bundle.putString("receiverName", map.get("receiverName"));
                bundle.putString("receiverPhone", map.get("receiverPhone"));
                bundle.putString("receiverAddress", map.get("receiverAddress"));
                bundle.putString("receiverPostalCode", map.get("receiverPostalCode"));
                Bundle b = getIntent().getExtras();
                username = b.getString("username");
                bundle.putString("username", username);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(DaftarAlamat.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_GET_ADDRESS,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    int sukses = jObj.getInt("success");
                    if (sukses == 1) {
                        JSONArray member = jObj.getJSONArray("custaddress");
                        int len = member.length();
                        for (int i = 0; i < len; i++) {
                            JSONObject a = member.getJSONObject(i);

                            int addressID = a.getInt("addressID");
                            int custID = a.getInt("custID");
                            String receiverName = a.getString("receiverName");
                            String receiverPhone = a.getString("receiverPhone");
                            String receiverAddress = a.getString("receiverAddress");
                            String receiverPostalCode = a.getString("receiverPostalCode");

                            HashMap<String, String> map = new HashMap<>();
                            map.put("addressID", String.valueOf(addressID));
                            map.put("custID", String.valueOf(custID));

                            map.put("receiverName", receiverName);
                            map.put("receiverPhone", receiverPhone);
                            map.put("receiverAddress", receiverAddress);
                            map.put("receiverPostalCode", receiverPostalCode);
                            list_address.add(map);
                        }

                        ListAdapter adapter_lv = new SimpleAdapter(DaftarAlamat.this,
                                list_address, R.layout.list_alamat, new String[]{"receiverName",
                                "receiverPhone", "receiverAddress", "receiverPostalCode"},
                                new int[]{R.id.txtNama, R.id.txtTelp, R.id.txtAlamat, R.id.txtKodePos});
                        lv.setAdapter(adapter_lv);
                    }


                } catch (Exception ex) {
                    Log.e("Error: ", ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ",error.getMessage());
                Toast.makeText(DaftarAlamat.this, "Check your internet connection!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }) {
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