package com.andrewhs.smktesting.simpleproductviewer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.andrewhs.smktesting.simpleproductviewer.model.Review;
import com.andrewhs.smktesting.simpleproductviewer.model.User;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductDownloader {
    public static final String SERVICE_URL = "http://smktesting.herokuapp.com/api/";
    public static final String JSON_PRODUCT_LIST = "products/";
    public static final String REGISTER = "register/";
    public static final String GET_AUTH_TOKEN = "oauth2/access_token/";
    public static final String PARAM_FOR_JSON = "json";
    public static final String GET_REVIEWS = "reviews/";
    private static final String GET_IMAGES = "http://smktesting.herokuapp.com/static/";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getProducts() {
        String url = Uri.parse(SERVICE_URL + JSON_PRODUCT_LIST).buildUpon().appendQueryParameter("format", PARAM_FOR_JSON).toString();
        try {
            String str= new String(getUrlBytes(url));
            System.out.println(str);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }
    public Bitmap getImage(String img){
        try {
            final Bitmap bitmap;
            byte[] bitmapBytes = getUrlBytes(GET_IMAGES+img);
            bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getReviews(int id){
        String url = Uri.parse(SERVICE_URL + GET_REVIEWS +id).buildUpon().appendQueryParameter("format", PARAM_FOR_JSON).toString();
        try {
            String str= new String(getUrlBytes(url));
            System.out.println(str);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }

    public String signUp (String name, String pass){
        HttpClient httpclient  = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVICE_URL+REGISTER);
        String response="";
        try {
            JSONObject json = new JSONObject();
            json.put("username",name);
            json.put("password",pass);
            StringEntity se = new StringEntity(json.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            ResponseHandler responseHandler = new BasicResponseHandler();
            //httpclient.execute(httpPost, responseHandler);
            response = responseHandler.handleResponse(httpclient.execute(httpPost)).toString();
            System.out.println(response);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public String login (User user){
        HttpClient httpclient  = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVICE_URL+GET_AUTH_TOKEN);
        String response="";
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("client_id",user.getClientKey()));//"7a23faf3ea7d8332f7d5"));
            nameValuePairs.add(new BasicNameValuePair("client_secret",user.getClientSecret()));//"93376623b21aa8cc195434d04959d087aaff1e78"));
            nameValuePairs.add(new BasicNameValuePair("grant_type","password"));
            nameValuePairs.add(new BasicNameValuePair("username",user.getUsername()));
            nameValuePairs.add(new BasicNameValuePair("password",user.getPassword()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            ResponseHandler responseHandler = new BasicResponseHandler();
            //httpclient.execute(httpPost, responseHandler);
            response = responseHandler.handleResponse(httpclient.execute(httpPost)).toString();
            System.out.println(response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public String postReview(Review review, User user){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(SERVICE_URL+GET_REVIEWS+review.getProductId());
        String responseStr="";
        HttpResponse response;
        try {
            StringEntity stringEntity = new StringEntity(review.toJSON().toString());
            httpPost.setEntity(stringEntity);
            if(user.getAccessToken()!=null)
                httpPost.setHeader("Authorization","Bearer "+user.getAccessToken());
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            ResponseHandler responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()==200) {
                responseStr = responseHandler.handleResponse(response).toString();
                System.out.println(responseStr);
            }else {
                System.out.println(response.getStatusLine().getReasonPhrase());
                System.out.println(response.getStatusLine().getStatusCode());
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseStr;
    }

}
