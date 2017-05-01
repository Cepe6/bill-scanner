package com.example.lacho.billscanner;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

import org.json.JSONObject;

/**
 * Created by lacho on 4/29/17.
 */

public class Bills extends GenericJson { // For Serialization

    @Key("_id")
    private String id;
    @Key
    private String bill;
    @Key
    private double totalPrice;
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL


    public Bills() {}  //GenericJson classes must have a public empty constructor

    public void setBill(String bill) {
        this.bill = bill;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
