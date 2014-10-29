package com.andrewhs.smktesting.simpleproductviewer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewhs.smktesting.simpleproductviewer.R;
import com.andrewhs.smktesting.simpleproductviewer.model.Product;
import com.andrewhs.smktesting.simpleproductviewer.model.Review;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductDownloader;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductLab;

import java.util.List;

public class ProductDetailsFragment extends Fragment {

    public static final String EXTRA_PRODUCT_ID = "ProductID" ;
    private static final String TAG = "ProductDetailsFragment";
    private static final int REQ_CODE = 33;
    Product product;
    TextView title;
    TextView detailsText;
    TextView reviewsTextCount;
    ImageView imageView;
    Button buttonAddReview;
    List<Review> reviews;
    ListView reviewList;
    ProductLab productLab = ProductLab.getInstance(getActivity());
    Callbacks Callbacks;

    public interface Callbacks {
        void onProductUpdated(Product p);
    }


    public static ProductDetailsFragment newInstance(int id){
        Bundle args = new Bundle();
        args.putInt(EXTRA_PRODUCT_ID,id);
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = productLab.getProductById(getArguments().getInt(EXTRA_PRODUCT_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_details,null);

        Log.i(TAG,"product id:"+product.getId());
        ReviewDownloaderTask task = new ReviewDownloaderTask();
        task.execute();
        reviewList = (ListView)v.findViewById(R.id.reviewList);
        title = (TextView)v.findViewById(R.id.product_details_title);
        detailsText = (TextView)v.findViewById(R.id.product_desc_textView);
        reviewsTextCount = (TextView)v.findViewById(R.id.productTextReviews);
        title.setText(product.getTitle());
        detailsText.setText(product.getText());
        imageView = (ImageView)v.findViewById(R.id.product_details_imageView);
        imageView.setImageResource(android.R.drawable.btn_star);
        Bitmap bm;
        if((bm=productLab.getImageBitmap(product.getImageURL()))!=null)
            imageView.setImageBitmap(bm);

        buttonAddReview = (Button)v.findViewById(R.id.buttonAddReview);
        buttonAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productLab.getUser()==null||productLab.getUser().getAccessToken()==null){
                    Toast.makeText(getActivity(),"You need to be logged in to post a review",Toast.LENGTH_LONG).show();
                }else {
                    PostReviewFragment postReview = PostReviewFragment.newInstance(product.getId());
                    postReview.setTargetFragment(ProductDetailsFragment.this, REQ_CODE);
                    postReview.show(getActivity().getSupportFragmentManager(), TAG);
                }

            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQ_CODE) {
            ReviewDownloaderTask reviewDownloaderTask = new ReviewDownloaderTask();
            reviewDownloaderTask.execute();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupAdapter(){
        if(getActivity()!=null && reviews!=null) {
            reviewList.setAdapter(new ReviewAdapter(getActivity(), android.R.layout.simple_list_item_1, reviews));
            reviewsTextCount.setText("Reviews (" + reviews.size() + ")");
        }

    }

    private class ReviewAdapter extends ArrayAdapter<Review>{

        public ReviewAdapter(Context context, int resource, List<Review> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_review,null);
            }
            Review review = getItem(position);
            TextView userAndDate = (TextView) convertView.findViewById(R.id.review_username_date);
            TextView reviewText = (TextView) convertView.findViewById(R.id.review_text);
            RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.reviewRatingBar);
            try {
                String s = review.getCreatedAt().substring(0, 19);
                userAndDate.setText(review.getCreatedBy() + " at " + s);
            }catch (Exception e){
                userAndDate.setText(review.getCreatedBy() + " at " + review.getCreatedAt());
            }
            reviewText.setText(review.getText());
            ratingBar.setRating(review.getRate());
            return convertView;
        }
    }

    private class ReviewDownloaderTask extends AsyncTask<Void,Void,Void>{
        ProductDownloader pd = new ProductDownloader();
        @Override
        protected Void doInBackground(Void... params) {
            productLab.loadReviewFromJSONStr(product.getId(),pd.getReviews(product.getId()));
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            reviews = productLab.getProductReview(product.getId());
            setupAdapter();
        }
    }

}
