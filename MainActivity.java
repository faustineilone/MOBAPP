package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnHome, btnLocation, btnProfile, imgCart;
    ListView lv;
    ArrayList<HashMap<String, String>> list_produk;
    String username, productName, image;
    int productID, stockNumber, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgCart = findViewById(R.id.imgCart);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent C = new Intent(getApplicationContext(), Cart.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                C.putExtra("username", username);
                startActivity(C);
            }
        });

        btnHome = (ImageButton) findViewById(R.id.Home);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent A = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(A);
            }
        });

        btnLocation = (ImageButton) findViewById(R.id.Location);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Maps.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        btnProfile = (ImageButton) findViewById(R.id.Profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent C = new Intent(getApplicationContext(), ProfilePage.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                C.putExtra("username", username);
                startActivity(C);
            }
        });

        list_produk = new ArrayList<>();
        lv = findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), DetailProduct.class);
                HashMap<String, String> map = list_produk.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("productID", Integer.parseInt(map.get("productID")));
                bundle.putString("productName", map.get("productName"));
                bundle.putInt("price", Integer.parseInt(map.get("price")));
                bundle.putInt("stockNumber", Integer.parseInt(map.get("stockNumber")));
                bundle.putString("image", map.get("image"));
                Bundle b = getIntent().getExtras();
                username = b.getString("username");
                bundle.putString("username", username);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_GET_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray member = jObj.getJSONArray("product");
                    int len = member.length();
                    for(int i = 0; i < len; i++) {
                        JSONObject a = member.getJSONObject(i);

                        productID = a.getInt("productID");
                        productName = a.getString("productName");
                        stockNumber = a.getInt("stockNumber");
                        price = a.getInt("price");
                        image = a.getString("image");

                        HashMap<String, String> map = new HashMap<>();
                        map.put("productID", String.valueOf(productID));
                        map.put("productName", productName);
                        map.put("price", String.valueOf(price));
                        map.put("price_output", formatRupiah(Integer.parseInt(String.valueOf(price))));
                        map.put("stockNumber", String.valueOf(stockNumber));
                        map.put("image", image);
                        list_produk.add(map);
                    }
                    ListAdapter adapter_lv = new SimpleAdapter(MainActivity.this, list_produk, R.layout.list_items, new String[]{"productName", "price_output"}, new int[]{R.id.txtNamaProduk, R.id.txtHargaProduk});
                    lv.setAdapter(adapter_lv);
                } catch (Exception ex) {
                    Log.e("Error: ", ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ",error.getMessage());
                Toast.makeText(MainActivity.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private String formatRupiah(int num){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(num);
    }
}