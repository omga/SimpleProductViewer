package com.andrewhs.smktesting.simpleproductviewer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.andrewhs.smktesting.simpleproductviewer.fragment.ProductDetailsFragment;
import com.andrewhs.smktesting.simpleproductviewer.fragment.ProductListFragment;
import com.andrewhs.smktesting.simpleproductviewer.R;

public class ProductListActivity extends SingleFragmentActivity implements ProductListFragment.OnFragmentInteractionListener {

    private static final String TAG = "ProductListActivity";

    public void onFragmentInteraction(int id) {
        Intent intent = new Intent(this,ProductPagerActivity.class);
        intent.putExtra(ProductDetailsFragment.EXTRA_PRODUCT_ID, id);
        startActivity(intent);
        Log.i(TAG, "Item was clicked: "+id);
    }
    public void onCreate(Bundle savedInstanceState) {
        if(!isNetworkConnected()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("nononono")
                    .setMessage("You need internet connection")
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    protected Fragment createFragment() {
        ProductListFragment productListFragment = new ProductListFragment();
        return productListFragment;
    }
}
