package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/21 0021.
 */
public class CartItemEntity_Medicine implements Serializable{
    private String id;
    private String name;
    private String price;
    private String image;
    private String brand;
    private String efficient;
    private String cart_id;
    private String quantity;
    private String addtime;
    private String store_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEfficient() {
        return efficient;
    }

    public void setEfficient(String efficient) {
        this.efficient = efficient;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public int getIntegerCartValue(){
        return Integer.parseInt(cart_id);
    }

    public int getIntegerQuanity(){
        return Integer.parseInt(quantity);
    }

    public double getDoublePrice(){
        return Double.parseDouble(price);
    }
}
