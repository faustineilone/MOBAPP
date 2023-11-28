package com.example.projekuas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
private final RecyclerViewInterface recyclerViewInterface;

        Context context;
        ArrayList<Order> orders;

public ListAdapter(Context context, ArrayList<Order> order, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.orders = order;
        this.recyclerViewInterface = recyclerViewInterface;
        }


@NonNull
@Override
public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ListAdapter.MyViewHolder(view, recyclerViewInterface);
        }

@Override
public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {
        final Order order = orders.get(position);

        holder.namaProduk.setText(order.getNamaProduk());
        holder.price.setText("Rp. " + order.getPrice());
        holder.qty.setText(Integer.toString(order.getQuantity()));
        Glide.with(context).load(order.getImage()).into(holder.imgProduk);
        }

    @Override
    public int getItemCount() {
            return orders.size();
            }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduk;
        TextView namaProduk, qty, price;

    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);

        imgProduk = itemView.findViewById(R.id.imgProduk);
        qty = itemView.findViewById(R.id.qty);
        price = itemView.findViewById(R.id.Price);
        namaProduk = itemView.findViewById(R.id.txtNamaProduk);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            }
        });
    }
}}
