package com.andrewhs.smktesting.simpleproductviewer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.andrewhs.smktesting.simpleproductviewer.fragment.ProductDetailsFragment;
import com.andrewhs.smktesting.simpleproductviewer.R;
import com.andrewhs.smktesting.simpleproductviewer.model.Product;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductLab;

import java.util.List;

public class ProductPagerActivity extends SingleFragmentActivity implements ProductDetailsFragment.Callbacks {
    ViewPager mViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final List<Product> products = ProductLab.getInstance(this).getProducts();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return products.size();
            }
            @Override
            public Fragment getItem(int pos) {
                int id =  products.get(pos).getId();
                return ProductDetailsFragment.newInstance(id);
            }
        });

        int productId = getIntent().getIntExtra(ProductDetailsFragment.EXTRA_PRODUCT_ID,0);
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId()==productId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public void onProductUpdated(Product product) {
        // do nothing
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            NavUtils.navigateUpFromSameTask(this);
        return super.onOptionsItemSelected(item);
    }
}

