package com.example.lacho.billscanner;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by lacho on 4/29/17.
 */

public class Bill extends GenericJson { // For Serialization

    @Key("_id")
    private String id;
    @Key
    private String bill;
    @Key
    private String ownerID;
    @Key
    private String product;
    @Key
    private int productAmount;
    @Key
    private double productPrice;
    @Key
    private double totalPrice;
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL

    public Bill() {}  //GenericJson classes must have a public empty constructor

    public void setBill(String bill) {
        this.bill = bill;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBill() {
        return bill;
    }

    public String getProduct() {
        return product;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public KinveyMetaData getMeta() {
        return meta;
    }
}
