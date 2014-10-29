package com.andrewhs.smktesting.simpleproductviewer.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_TEXT = "text";
    private static final String JSON_PHOTO = "img";

    private int id;
    private String title;
    private String imageURL;
    private String text;

    public Product(){}
    public Product(JSONObject json){
        try {
            id = json.getInt(JSON_ID);
            title = json.getString(JSON_TITLE);
            text = json.getString(JSON_TEXT);
            imageURL = json.getString(JSON_PHOTO);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_ID,id);
            json.put(JSON_TITLE,title);
            json.put(JSON_TEXT,text);
            json.put(JSON_PHOTO,imageURL);
        } catch (JSONException e) {
            e.printStackTrace();        }
        return json;

    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return title+text+imageURL;
    }
}
