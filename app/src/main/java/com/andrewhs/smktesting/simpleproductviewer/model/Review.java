package com.andrewhs.smktesting.simpleproductviewer.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private static final String JSON_ID = "id";
    private static final String JSON_RATE = "rate";
    private static final String JSON_TEXT = "text";
    private static final String CREATED = "created_by";
    private static final String CREATED_USERNAME = "username";
    private static final String TIME = "created_at";
    private static final String PRODUCT = "product" ;

    private int id;
    private String text;
    private String createdBy;
    private String createdAt;
    private int rate;
    private int productId;


    public Review(){}
    public Review(JSONObject json){
        JSONObject jsonUser = null;
        try {
            id = json.getInt(JSON_ID);
            rate = json.getInt(JSON_RATE);
            text = json.getString(JSON_TEXT);
            jsonUser = json.getJSONObject(CREATED);
            createdBy = jsonUser.getString(CREATED_USERNAME);
            createdAt = json.getString(TIME);
            productId = json.getInt(PRODUCT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_ID,id);
            jsonObject.put(PRODUCT,productId);
            jsonObject.put(JSON_TEXT,text);
            jsonObject.put(JSON_RATE,rate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return id+text+rate+" "+createdBy+createdAt;
    }
}
