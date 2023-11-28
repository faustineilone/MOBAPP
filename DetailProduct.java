package com.example.projekuas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailProduct extends AppCompatActivity {
    String username, productName, image;
    int productID, stockNumber, price, quantity, subtotal;
    Context context;
    TextView txtNamaProduk, txtHargaProduk, txtStokProduk;
    Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        ImageView imageView = findViewById(R.id.imageView6);

        Bundle bundle = getIntent().getExtras();
        username = (String)bundle.get("username");
        productID = (int)bundle.get("productID");
        productName = (String)bundle.get("productName");
        price = (int)bundle.get("price");
        image = (String)bundle.get("image");
        stockNumber = (int)bundle.get("stockNumber");

        txtNamaProduk = findViewById(R.id.txtNamaProduk);
        txtHargaProduk = findViewById(R.id.txtHargaProduk);
        txtStokProduk = findViewById(R.id.txtStokProduk);

        txtNamaProduk.setText(productName);
        txtHargaProduk.setText(formatRupiah(Integer.parseInt(String.valueOf(price))));
        txtStokProduk.setText("Stock: " + String.valueOf(stockNumber));

        String url = Configuration.URL + image;
        Glide.with(DetailProduct.this).load(url).into(imageView);

        EditText edtQuantity = findViewById(R.id.edtQuantity);
        ImageButton imgPlus = findViewById(R.id.imgPlus);
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(edtQuantity.getText().toString()) + 1;
                edtQuantity.setText(String.valueOf(quantity));
                if(Integer.parseInt(edtQuantity.getText().toString()) > stockNumber) {
                    edtQuantity.setText(String.valueOf(stockNumber));
                    Toast.makeText(DetailProduct.this, "Only " +  stockNumber + " left in stock",
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
                    Toast.makeText(DetailProduct.this, "Minimum order is 1",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(edtQuantity.getText().toString()) > stockNumber) {
                    edtQuantity.setText(String.valueOf(stockNumber));
                    Toast.makeText(DetailProduct.this, "Only " +  stockNumber + " left in stock",
                            Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue queue = Volley.newRequestQueue(DetailProduct.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Configuration.URL_ADD_CART, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                Toast.makeText(DetailProduct.this,
                                        "Product has been added to cart", Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                Log.e("Error: ", ex.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error: ", error.getMessage());
                            Toast.makeText(DetailProduct.this,
                                    "Check your internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", username);
                            params.put("productID", String.valueOf(productID));
                            final int quantity = Integer.parseInt(edtQuantity.getText().toString());
                            params.put("quantity", String.valueOf(quantity));
                            return params;
                        }
                    };
                    queue.getCache().clear();
                    queue.add(stringRequest);
//                    Intent i = new Intent(getApplicationContext(), Cart.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("productID", productID);
//                    bundle.putString("productName", productName);
//                    bundle.putInt("price", price);
//                    quantity = Integer.parseInt(edtQuantity.getText().toString());
//                    bundle.putInt("quantity", quantity);
//                    subtotal = quantity * price;
//                    bundle.putInt("subtotal", subtotal);
//                    bundle.putInt("stockNumber", stockNumber);
//                    bundle.putString("image", image);
//                    Bundle b = getIntent().getExtras();
//                    username = b.getString("username");
//                    bundle.putString("username", username);
//                    i.putExtras(bundle);
//                    startActivity(i);
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