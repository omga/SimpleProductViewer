package com.andrewhs.smktesting.simpleproductviewer.util;

import android.content.Context;
import android.widget.Toast;

import com.andrewhs.smktesting.simpleproductviewer.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Calendar;

public class UserUtil {
    Context context;

    public UserUtil(Context context) {
        this.context = context;
    }

    public User parseRegisterJSON(String name, String pass, String jsonstr){
        User user= new User();
        try {
            JSONObject jo = new JSONObject(jsonstr);
            if(((Boolean)jo.getBoolean("success")).equals(false)) {
                Toast toast = Toast.makeText(context,jo.getString("message"),Toast.LENGTH_LONG);
                toast.show();
            }
            user.setUsername(name);
            user.setPassword(pass);
            user.setId(jo.getInt("id"));
            user.setClientKey(jo.getString("client_key"));
            user.setClientSecret(jo.getString("client_secret"));
            saveUserToFile(user);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    public User parseLoginJSON(User user,String jsonstr){

        try {
            JSONObject jo = new JSONObject(jsonstr);
            user.setAccessToken(jo.getString("access_token"));
            user.setExpires(Calendar.getInstance().getTimeInMillis()/1000 + jo.getInt("expires_in"));
            saveUserToFile(user);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void saveUserToFile(User user) throws IOException {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = context.openFileOutput("user.ser", Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(oos!=null)
                oos.close();
        }
    }

    public User readUserFromFile() throws IOException {
        ObjectInputStream ios = null;
        User user = null;
        try{
            FileInputStream fis = context.openFileInput("user.ser");
            ios = new ObjectInputStream(fis);
            user = (User)ios.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(ios != null)
                ios.close();
        }
        return user;
    }


}
