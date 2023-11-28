package com.example.projekuas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Cart extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btnCancel, btnConfirm, btnEditCart, btnDeleteCart;
    Spinner spinnerAlamat;
    ListView lv;
    TextView txtSubtotal, txtOngkir, txtTotal;
    ArrayList<String> custaddress;
    ArrayList<HashMap<String, String>> list_cart_items;
    String username, productName, image, receiverName, receiverPhone, receiverAddress, receiverPostalCode;
    int cartID, productID, quantity, price, subtotal, subtotal_cart, total, stockNumber, addressID, ongkir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtOngkir = findViewById(R.id.txtOngkir);
        txtTotal = findViewById(R.id.txtTotal);

        ongkir = 5000;
        txtOngkir.setText(formatRupiah(ongkir));

        custaddress = new ArrayList<String>();
        spinnerAlamat = findViewById(R.id.spinnerAlamat);
        spinnerAlamat.setOnItemSelectedListener(Cart.this);
        getSpinnerData();

        list_cart_items = new ArrayList<>();
        lv = findViewById(R.id.list_cart_all);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = list_cart_items.get(position);
                cartID = Integer.parseInt(map.get("cartID"));
                quantity = Integer.parseInt(map.get("quantity"));
                price = Integer.parseInt(map.get("price"));

                final Dialog dialog = new Dialog(Cart.this);

                LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
                View v = layoutInflater.inflate(R.layout.dialog_edit_cart, null);

                dialog.setContentView(v);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_popup));
                }
                dialog.setCancelable(true);
                dialog.show();

                btnEditCart= v.findViewById(R.id.btnEditCart);
                btnEditCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> map = list_cart_items.get(position);
                        Intent i = new Intent(getApplicationContext(), EditCart.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("cartID", Integer.parseInt(map.get("cartID")));
                        bundle.putInt("productID", Integer.parseInt(map.get("productID")));
                        bundle.putString("productName", map.get("productName"));
                        bundle.putInt("price", Integer.parseInt(map.get("price")));
                        bundle.putInt("quantity", Integer.parseInt(map.get("quantity")));
                        bundle.putInt("stockNumber", Integer.parseInt(map.get("stockNumber")));
                        bundle.putString("image", map.get("image"));
                        Bundle b = getIntent().getExtras();
                        username = b.getString("username");
                        bundle.putString("username", username);
                        i.putExtras(bundle);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });

                btnDeleteCart= v.findViewById(R.id.btnDeleteCart);
                btnDeleteCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openDialogDeleteItemFromCart();
                    }
                });

                return true;
            }
        });

        RequestQueue queue = Volley.newRequestQueue(Cart.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_GET_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);

                    int sukses = jObj.getInt("success");
                    if (sukses == 1) {
                        JSONArray member = jObj.getJSONArray("cart");
                        int len = member.length();
                        for(int i = 0; i < len; i++) {
                            JSONObject a = member.getJSONObject(i);

                            cartID = a.getInt("cartID");
                            productID = a.getInt("productID");
                            quantity = a.getInt("quantity");
                            productName = a.getString("productName");
                            price = a.getInt("price");
                            stockNumber = a.getInt("stockNumber");
                            subtotal = a.getInt("subtotal");
                            image = a.getString("image");

                            HashMap<String, String> map = new HashMap<>();
                            map.put("cartID", String.valueOf(cartID));
                            map.put("productID", String.valueOf(productID));
                            map.put("quantity", String.valueOf(quantity));
                            map.put("productName", productName);
                            map.put("price", String.valueOf(price));
                            map.put("stockNumber", String.valueOf(stockNumber));
                            map.put("subtotal", String.valueOf(subtotal));
                            map.put("price_output", formatRupiah(Integer.parseInt(String.valueOf(price))));
                            map.put("subtotal_output", formatRupiah(Integer.parseInt(String.valueOf(subtotal))));
                            map.put("image", image);
                            list_cart_items.add(map);
                        }
                        ListAdapter adapter_lv = new SimpleAdapter(Cart.this, list_cart_items, R.layout.list_cart, new String[]{"productName", "price_output", "quantity", "subtotal_output"}, new int[]{R.id.txtNamaProduk, R.id.txtPrice, R.id.txtQuantity, R.id.txtSubtotalProduk});
                        lv.setAdapter(adapter_lv);
                    } else {
                        openDialogNoItemInCart();
                    }
                } catch (Exception ex) {
                    Log.e("Error: ", ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ",error.getMessage());
                Toast.makeText(Cart.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
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

        RequestQueue queue2 = Volley.newRequestQueue(Cart.this);
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Configuration.URL_GET_SUBTOTAL_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray member = jObj.getJSONArray("cart");
                    int len = member.length();
                    for(int i = 0; i < len; i++) {
                        JSONObject a = member.getJSONObject(i);
                        subtotal_cart = a.getInt("subtotal_cart");
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("subtotal_cart", String.valueOf(subtotal_cart));
                        txtSubtotal.setText(formatRupiah(Integer.parseInt(String.valueOf(subtotal_cart))));
                        total = subtotal_cart + ongkir;
                        txtTotal.setText(formatRupiah(Integer.parseInt(String.valueOf(total))));
                    }
                } catch (Exception ex) {
                    Log.e("Error: ", ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ",error.getMessage());
                Toast.makeText(Cart.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
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
        queue2.add(stringRequest2);

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = spinnerAlamat.getSelectedItem().toString();
                String[] splited_address = address.split(", ");
                receiverName = splited_address[0];
                receiverPhone = splited_address[1];
                receiverAddress = splited_address[2];
                receiverPostalCode = splited_address[3];

                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");

                RequestQueue queue = Volley.newRequestQueue(Cart.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Configuration.URL_ADD_SALES_ORDER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            Toast.makeText(Cart.this,
                                    "Your order has been placed", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), ThankYou.class);
                            Bundle bundle = getIntent().getExtras();
                            username = bundle.getString("username");
                            i.putExtra("username", username);
                            startActivity(i);
                        } catch (Exception ex) {
                            Log.e("Error: ", ex.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ", error.getMessage());
                        Toast.makeText(Cart.this,
                                "Check your internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("receiverName", receiverName);
                        params.put("receiverPhone", receiverPhone);
                        params.put("receiverAddress", receiverAddress);
                        params.put("receiverPostalCode", receiverPostalCode);
                        return params;
                    }
                };
                queue.getCache().clear();
                queue.add(stringRequest);
            }
        });
    }

    private void getSpinnerData() {
        RequestQueue queue = Volley.newRequestQueue(Cart.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.URL_GET_ADDRESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);

                    int sukses = jObj.getInt("success");
                    if (sukses == 1) {
                        JSONArray member = jObj.getJSONArray("custaddress");
                        getAddress(member);
                    } else {
                        openDialogNoAddressInCart();
                    }
                } catch (Exception ex) {
                    Log.e("Error: ", ex.toString());
                }
            }

            private void getAddress(JSONArray member) {
                for(int i=0; i<member.length(); i++) {
                    try {
                        JSONObject jObj = member.getJSONObject(i);
                        addressID = jObj.getInt("addressID");

                        custaddress.add(jObj.getString("receiverName") +
                                ", " + jObj.getString("receiverPhone") +
                                ", " + jObj.getString("receiverAddress") +
                                ", " + jObj.getString("receiverPostalCode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Cart.this, android.R.layout.simple_spinner_dropdown_item, custaddress);
                spinnerAlamat.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ",error.getMessage());
                Toast.makeText(Cart.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
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

    private void openDialogNoAddressInCart() {
        final Dialog dialog = new Dialog(Cart.this);

        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog_no_address_in_cart, null);

        dialog.setContentView(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_popup));
        }

        Button btnOK = v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void openDialogNoItemInCart() {
        final Dialog dialog = new Dialog(Cart.this);

        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog_no_item_in_cart, null);

        dialog.setContentView(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_popup));
        }

        Button btnOK = v.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = getIntent().getExtras();
                username = bundle.getString("username");
                i.putExtra("username", username);
                startActivity(i);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void openDialogDeleteItemFromCart() {
        final Dialog dialog = new Dialog(Cart.this);

        LayoutInflater layoutInflater = (LayoutInflater) getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog_delete_item_from_cart, null);

        dialog.setContentView(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_popup));
        }

        Button btnCancel = v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnDelete = v.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(Cart.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Configuration.URL_DELETE_CART, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        Bundle bundle = getIntent().getExtras();
                        username = bundle.getString("username");
                        i.putExtra("username", username);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ",error.getMessage());
                        Toast.makeText(Cart.this,
                                "Check your internet connection!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }){
                    protected HashMap<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("cartID", String.valueOf(cartID));
                        return map;
                    }
                };
                queue.add(stringRequest);
                Toast.makeText(Cart.this, "Item has been deleted from cart",
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private String formatRupiah(int num){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(num);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}