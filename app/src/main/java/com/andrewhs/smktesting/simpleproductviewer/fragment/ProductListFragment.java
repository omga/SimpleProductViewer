package com.andrewhs.smktesting.simpleproductviewer.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewhs.smktesting.simpleproductviewer.R;
import com.andrewhs.smktesting.simpleproductviewer.model.Product;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductDownloader;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductLab;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the Callbacks
 * interface.
 */
public class ProductListFragment extends ListFragment {

    private static final String TAG = "ProductListFragment";
    private List<Product> productList;
    private OnFragmentInteractionListener mListener;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadItemsTask(getActivity()).execute();
        setupAdapter();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(ProductLab.getInstance(getActivity()).getProducts().get(position).getId());
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int id);
    }

    private class ProductAdapter extends ArrayAdapter<Product>{
        public ProductAdapter(List<Product> items) {
            super(getActivity(), android.R.layout.simple_list_item_1, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_product,null);
            }
           Product product = getItem(position);
            TextView tittle = (TextView)convertView.findViewById(R.id.list_itemTitle);
            tittle.setText(product.getTitle());
            TextView text = (TextView)convertView.findViewById(R.id.list_itemText);
            String txt = product.getText();
            if(txt.length()>35)
                txt=txt.substring(0,35);
            text.setText(txt);
            ImageView image = (ImageView)convertView.findViewById(R.id.list_ImageView);
            image.setImageResource(android.R.drawable.ic_dialog_alert);
            Bitmap bm;
            if((bm=ProductLab.getInstance(getActivity()).getImageBitmap(product.getImageURL()))!=null)
                image.setImageBitmap(bm);

            return convertView;
        }
    }
    private class DownloadItemsTask extends AsyncTask<Void,Void,String>{
        Context context;
        public DownloadItemsTask(Context ctx){
            context=ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            ProductDownloader pd = new ProductDownloader();
            return pd.getProducts();
        }

        @Override
        protected void onPostExecute(String JSONString) {
            if(JSONString!=null) {
                super.onPostExecute(JSONString);
                Log.i(TAG, JSONString);
                productList = ProductLab.getInstance(getActivity()).loadFromJSONString(JSONString);
                setupAdapter();
                new DownloadImagesTask().execute();
            }else {
                Toast toast= Toast.makeText(context, "Couldn't load data. Please check your connection.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class DownloadImagesTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            ProductDownloader pd = new ProductDownloader();
            for(Product p:productList){
                if (ProductLab.getInstance(getActivity()).getImageBitmap(p.getImageURL())!=null) continue;
                Bitmap bitmap =  pd.getImage(p.getImageURL());
                if(bitmap!=null)
                    ProductLab.getInstance(getActivity()).addImageBitmap(p.getImageURL(),bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupAdapter();
        }
    }

    private void setupAdapter(){
        if(getActivity()==null||productList==null){
            return;
        }else {
            ProductAdapter adapter = new ProductAdapter(productList);
            setListAdapter(adapter);
        }

    }

}
