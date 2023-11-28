package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditCart extends AppCompatActivity {
    String username, productName, image;
    int productID, stockNumber, price, quantity, cartID;
    Context context;
    TextView txtNamaProduk, txtHargaProduk, txtStokProduk;
    Button btnUpdateCart;
    EditText edtQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cart);

        ImageView imageView = findViewById(R.id.imageView6);

        Bundle bundle = getIntent().getExtras();
        username = (String)bundle.get("username");
        cartID = (int)bundle.get("cartID");
        productID = (int)bundle.get("productID");
        productName = (String)bundle.get("productName");
        price = (int)bundle.get("price");
        quantity = (int)bundle.get("quantity");
        image = (String)bundle.get("image");
        stockNumber = (int)bundle.get("stockNumber");

        txtNamaProduk = findViewById(R.id.txtNamaProduk);
        txtHargaProduk = findViewById(R.id.txtHargaProduk);
        txtStokProduk = findViewById(R.id.txtStokProduk);
        edtQuantity = findViewById(R.id.edtQuantity);

        txtNamaProduk.setText(productName);
        txtHargaProduk.setText(formatRupiah(Integer.parseInt(String.valueOf(price))));
        txtStokProduk.setText("Stock: " + String.valueOf(stockNumber));
        edtQuantity.setText(String.valueOf(quantity));

        EditText edtQuantity = findViewById(R.id.edtQuantity);
        ImageButton imgPlus = findViewById(R.id.imgPlus);
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(edtQuantity.getText().toString()) + 1;
                edtQuantity.setText(String.valueOf(quantity));
                if(Integer.parseInt(edtQuantity.getText().toString()) > stockNumber) {
                    edtQuantity.setText(String.valueOf(stockNumber));
                    Toast.makeText(EditCart.this, "Only " +  stockNumber + " left in stock",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton imgMin = findViewById(R.id.imgMin);
        imgMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(edtQuantity.getText().toString()) - 1;
                edtQuantity.setText(String.valueOf(quantity));

                if(Integer.parseInt(edtQuantity.getText().toString()) < 1) {
                    edtQuantity.setText("1");

                    Toast.makeText(EditCart.this, "Minimum order is 1",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpdateCart = findViewById(R.id.btnUpdateCart);
        btnUpdateCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(edtQuantity.getText().toString()) > stockNumber) {
                    edtQuantity.setText(String.valueOf(stockNumber));
                    Toast.makeText(EditCart.this, "Only " +  stockNumber + " left in stock",
                            Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue queue = Volley.newRequestQueue(EditCart.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Configuration.URL_UPDATE_CART, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                Toast.makeText(EditCart.this,
                                        "Cart has been updated", Toast.LENGTH_SHORT).show();
                                Intent C = new Intent(getApplicationContext(), Cart.class);
                                Bundle bundle = getIntent().getExtras();
                                username = bundle.getString("username");
                                C.putExtra("username", username);
                                startActivity(C);
                            } catch (Exception ex) {
                                Log.e("Error: ", ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error: ", error.getMessage());
                            Toast.makeText(EditCart.this,
                                    "Check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("cartID", String.valueOf(cartID));
                            params.put("price", String.valueOf(price));
                            final int quantity = Integer.parseInt(edtQuantity.getText().toString());
                            params.put("quantity", String.valueOf(quantity));
                            return params;
                        }
                    };
                    queue.getCache().clear();
                    queue.add(stringRequest);
                }
            }
        });
    }

    private String formatRupiah(int num){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(num);
    }
}