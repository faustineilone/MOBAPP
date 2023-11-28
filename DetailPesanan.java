package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailPesanan extends AppCompatActivity {
    TextView txtSalesID, txtNama, txtTelp, txtAddress, txtPostalCode, txtSubtotalDetailPesanan, txtOngkirDetailPesanan, txtTotalDetailPesanan, txtWaktuPemesanan;
    Button btnPesananDiterima;
    ListView lv;
    ArrayList<HashMap<String, String>> list_items_pesanan;
    int salesID, productID, price, quantity, subtotal, subtotal_final, ongkir, total;
    String statusName, username, orderTime, receiverName, receiverPhone, receiverAddress, receiverPostalCode, productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        Bundle bundle = getIntent().getExtras();
        username = (String)bundle.get("username");
        salesID = (int)bundle.get("salesID");
        statusName = (String)bundle.get("statusName");
        orderTime = (String)bundle.get("orderTime");
        receiverName = (String)bundle.get("receiverName");
        receiverPhone = (String)bundle.get("receiverPhone");
        receiverAddress = (String)bundle.get("receiverAddress");
        receiverPostalCode = (String)bundle.get("receiverPostalCode");

        txtSalesID = findViewById(R.id.txtSalesID);
        txtSalesID.setText(String.valueOf(salesID));

        txtNama = findViewById(R.id.txtNama);
        txtNama.setText(receiverName);

        txtTelp = findViewById(R.id.txtTelp);
        txtTelp.setText(receiverPhone);

        txtAddress = findViewById(R.id.txtAddress);
        txtAddress.setText(receiverAddress);

        txtPostalCode = findViewById(R.id.txtPostalCode);
        txtPostalCode.setText(receiverPostalCode);

        txtSubtotalDetailPesanan = findViewById(R.id.txtSubtotalDetailPesanan);

        ongkir = 5000;
        txtOngkirDetailPesanan = findViewById(R.id.txtOngkirDetailPesanan);
        txtOngkirDetailPesanan.setText(formatRupiah(ongkir));

        txtTotalDetailPesanan = findViewById(R.id.txtTotalDetailPesanan);

        txtWaktuPemesanan = findViewById(R.id.txtWaktuPemesanan);
        txtWaktuPemesanan.setText(orderTime);

        list_items_pesanan = new ArrayList<>();
        lv = findViewById(R.id.list_items_detail_pesanan);

        RequestQueue queue = Volley.newRequestQueue(DetailPesanan.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_GET_DETAIL_SO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);

                    int sukses = jObj.getInt("success");
                    if (sukses == 1) {
                        JSONArray member = jObj.getJSONArray("detailso");
                        int len = member.length();
                        for(int i = 0; i < len; i++) {
                            JSONObject a = member.getJSONObject(i);

                            productID = a.getInt("productID");
                            productName = a.getString("productName");
                            price = a.getInt("price");
                            quantity = a.getInt("quantity");
                            subtotal = a.getInt("subtotal");

                            HashMap<String, String> map = new HashMap<>();
                            map.put("productID", String.valueOf(productID));
                            map.put("productName", productName);
                            map.put("price", String.valueOf(price));
                            map.put("quantity", String.valueOf(quantity));
                            map.put("subtotal", String.valueOf(subtotal));
                            map.put("price_output", formatRupiah(Integer.parseInt(String.valueOf(price))));
                            map.put("subtotal_output", formatRupiah(Integer.parseInt(String.valueOf(subtotal))));
                            list_items_pesanan.add(map);
                        }
                        ListAdapter adapter_lv = new SimpleAdapter(DetailPesanan.this, list_items_pesanan, R.layout.list_cart, new String[]{"productName", "price_output", "quantity", "subtotal_output"}, new int[]{R.id.txtNamaProduk, R.id.txtPrice, R.id.txtQuantity, R.id.txtSubtotalProduk});
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
                Toast.makeText(DetailPesanan.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Bundle bundle = getIntent().getExtras();
                salesID = bundle.getInt("salesID");
                params.put("salesID", String.valueOf(salesID));
                return params;
            }
        };
        queue.add(stringRequest);

        RequestQueue queue2 = Volley.newRequestQueue(DetailPesanan.this);
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Configuration.URL_GET_SUBTOTAL_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray member = jObj.getJSONArray("detailso");
                    int len = member.length();
                    for(int i = 0; i < len; i++) {
                        JSONObject a = member.getJSONObject(i);
                        subtotal_final = a.getInt("subtotal_final");
                        txtSubtotalDetailPesanan.setText(formatRupiah(Integer.parseInt(String.valueOf(subtotal_final))));
                        total = subtotal_final + ongkir;
                        txtTotalDetailPesanan.setText(formatRupiah(Integer.parseInt(String.valueOf(total))));
                    }
                } catch (Exception ex) {
                    Log.e("Error: ", ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ",error.getMessage());
                Toast.makeText(DetailPesanan.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Bundle bundle = getIntent().getExtras();
                salesID = bundle.getInt("salesID");
                params.put("salesID", String.valueOf(salesID));
                return params;
            }
        };
        queue2.add(stringRequest2);

        btnPesananDiterima = findViewById(R.id.btnPesananDiterima);
        btnPesananDiterima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(DetailPesanan.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Configuration.URL_UPDATE_SO_STATUS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent i = new Intent(getApplicationContext(), PesananSaya.class);
                        Bundle bundle = getIntent().getExtras();
                        username = bundle.getString("username");
                        i.putExtra("username", username);
                        startActivity(i);
                        Toast.makeText(DetailPesanan.this, "Status has been updated",
                                Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ",error.getMessage());
                        Toast.makeText(DetailPesanan.this,
                                "Check your internet connection!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }){
                    protected HashMap<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("salesID", String.valueOf(salesID));
                        return map;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }

    private String formatRupiah(int num){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(num);
    }
}