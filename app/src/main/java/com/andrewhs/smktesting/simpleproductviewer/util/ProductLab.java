package com.andrewhs.smktesting.simpleproductviewer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.andrewhs.smktesting.simpleproductviewer.model.Product;
import com.andrewhs.smktesting.simpleproductviewer.model.Review;
import com.andrewhs.smktesting.simpleproductviewer.model.User;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductLab {

    private static ProductLab ourInstance;
    private List<Product> products;
    private LruCache<String,Bitmap> imagesCache =new LruCache<String,Bitmap>(1024*64);
    private User user;
    private Context appContext;
    private Map<Integer,List<Review>> reviewsMap = new HashMap<Integer, List<Review>>();


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        ourInstance.products = products;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        ourInstance.user = user;
    }

    public static ProductLab getInstance(Context appCtx) {
        if(ourInstance==null)
            ourInstance = new ProductLab(appCtx.getApplicationContext());
        return ourInstance;
    }

    public Product getProductById(int id){
        for(Product p:products){
            if(p.getId()==id)
                return p;
        }
        return null;
    }

    public List<Product> loadFromJSONString(String str){
        JSONArray ja = null;
        try {
            ja = (JSONArray) new JSONTokener(str).nextValue();
            products = new ArrayList<Product>();
            for(int i=0;i<ja.length();i++){

                products.add(new Product(ja.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Review> getProductReview(int id){
        return reviewsMap.get(id);
    }

    public void addProductReviewList(int id,List<Review> reviews){
        reviewsMap.put(id,reviews);
    }

    public void loadReviewFromJSONStr(int id, String str){
        JSONArray ja = null;
        List<Review> reviewsList = new ArrayList<Review>();
        try {
            ja = (JSONArray)new JSONTokener(str).nextValue();
            for (int i = 0; i < ja.length(); i++) {
                Review r = new Review(ja.getJSONObject(i));
                reviewsList.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addProductReviewList(id, reviewsList);
    }

    public void addImageBitmap(String imgURL,Bitmap bitmap){
        imagesCache.put(imgURL,bitmap);
    }

    public Bitmap getImageBitmap(String imgURL){
        return imagesCache.get(imgURL);
    }
    public void readUser(){
        try {
            user = new UserUtil(appContext).readUserFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ProductLab(Context appCtx) {
        appContext = appCtx;
        readUser();

    }
}
