package com.andrewhs.smktesting.simpleproductviewer.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.andrewhs.smktesting.simpleproductviewer.R;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductDownloader;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductLab;
import com.andrewhs.smktesting.simpleproductviewer.util.UserUtil;

public class LoginFragment extends DialogFragment {
    private static final String TAG = "LoginFragment";
    EditText login;
    EditText password;
    Context ctx;
    ProductLab productLab = ProductLab.getInstance(getActivity());

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.login_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ctx = getActivity();
                        String logintxt;
                        String passwordtxt;
                        login = (EditText)getDialog().findViewById(R.id.username);
                        password = (EditText)getDialog().findViewById(R.id.password);
                        logintxt = login.getText().toString();
                        passwordtxt = password.getText().toString();
                        if(productLab.getUser()==null){
                            Toast toast = Toast.makeText(getActivity(),"register first",Toast.LENGTH_LONG);
                            toast.show();
                        }else
                            if( productLab.getUser().getUsername().equals(logintxt)&&
                                    (productLab.getUser().getPassword().equals(passwordtxt) )) {
                                RequestTask task = new RequestTask(getActivity());
                                task.execute("login", logintxt, passwordtxt);
                            }else {
                                Toast toast = Toast.makeText(getActivity(),"wrong login or password",Toast.LENGTH_LONG);
                                toast.show();
                            }
                    }
                })
                .setNeutralButton(R.string.sign_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        login = (EditText) getDialog().findViewById(R.id.username);
                        password = (EditText) getDialog().findViewById(R.id.password);
                        System.out.println(login.getText() + " " + password.getText());
                        RequestTask task = new RequestTask(getActivity());
                        task.execute("register", login.getText().toString(), password.getText().toString());

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private class RequestTask extends AsyncTask<String,Void,String[]>{
        private Context context;

        private RequestTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected String[] doInBackground(String... params) {
            ProductDownloader pd = new ProductDownloader();
            String jsonstring="";
            String [] sarr= new String[3];

            if(params[0].equals("login")){
                 {
                     jsonstring = pd.login(productLab.getUser());
                     sarr[0]=jsonstring;
                     sarr[2]=null;
                     Log.i(TAG,productLab.getUser().getAccessToken()+"exp: "+productLab.getUser().getExpires());
                }
            }else if (params[0].equals("register")){
                jsonstring = pd.signUp(params[1],params[2]);
                sarr[0]=params[1];
                sarr[1]=params[2];
                sarr[2]=jsonstring;
            }

            return sarr;
        }

        @Override
        protected void onPostExecute(String[] params) {
            UserUtil userUtil = new UserUtil(context);
            if(params[2]!=null)
                    productLab.setUser(userUtil.parseRegisterJSON(params[0],params[1],params[2]));
            else {
                userUtil.parseLoginJSON(productLab.getUser(), params[0]);
                ((Activity) ctx).invalidateOptionsMenu();
            }

            Log.i(TAG,"user logged/registered: "+productLab.getUser().toString());
           // getTargetFragment().getActivity().invalidateOptionsMenu();
        }

    }
}
