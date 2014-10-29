package com.andrewhs.smktesting.simpleproductviewer.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.andrewhs.smktesting.simpleproductviewer.R;
import com.andrewhs.smktesting.simpleproductviewer.fragment.LoginFragment;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductLab;

import java.util.Calendar;


public abstract class SingleFragmentActivity extends ActionBarActivity {


    protected int getLayoutResId() {
        return R.layout.activity_single_fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_login) {
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.show(getSupportFragmentManager(),"GG");
            return true;
        }else if (id == R.id.action_logout) {
            ProductLab.getInstance(this).getUser().setExpires(0);
            ProductLab.getInstance(this).getUser().setAccessToken(null);
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem milogin = menu.findItem(R.id.action_login);
        MenuItem milogout = menu.findItem(R.id.action_logout);
        super.onPrepareOptionsMenu(menu);
        if(ProductLab.getInstance(this).getUser()==null||(ProductLab.getInstance(this).getUser().getExpires()- (Calendar.getInstance().getTimeInMillis()/1000))<0){
            milogin.setTitle(R.string.action_login);
            milogin.setEnabled(true);
            milogout.setEnabled(false);
        } else {
            milogin.setTitle(ProductLab.getInstance(this).getUser().getUsername());
            milogin.setEnabled(false);
            milogout.setEnabled(true);
        }
        return true;
    }
    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}
