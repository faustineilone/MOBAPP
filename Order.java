package com.example.projekuas;

public class Order {
    String namaProduk, image;
    double quantity, price;

    public Order(String namaProduk, double quantity, double price, String image) {
        this.namaProduk = namaProduk;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getImage() {
        return image;
    }

    public int getQuantity() {
        return (int) quantity;
    }

    public int getPrice() {
        return (int) price;
    }


    public Order get(int position) {
        return null;
    }

}
