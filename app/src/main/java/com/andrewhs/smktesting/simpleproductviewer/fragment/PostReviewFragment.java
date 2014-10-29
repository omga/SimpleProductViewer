package com.andrewhs.smktesting.simpleproductviewer.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RatingBar;

import com.andrewhs.smktesting.simpleproductviewer.R;
import com.andrewhs.smktesting.simpleproductviewer.model.Review;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductDownloader;
import com.andrewhs.smktesting.simpleproductviewer.util.ProductLab;

import org.json.JSONException;
import org.json.JSONObject;

public class PostReviewFragment extends DialogFragment {
    private static final String TAG = "PostReviewFragment";
    private static final String EXTRA_PRODUCT_ID = "product_id";
    EditText comment;
    RatingBar rate;
    int productId;
   // Context ctx;

    public static PostReviewFragment newInstance(int id){
        Bundle args = new Bundle();
        args.putInt(EXTRA_PRODUCT_ID,id);
        PostReviewFragment fragment = new PostReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productId = getArguments().getInt(EXTRA_PRODUCT_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.add_review_dialog,null))
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comment = (EditText) getDialog().findViewById(R.id.addReview_commentText);
                        rate = (RatingBar) getDialog().findViewById(R.id.addReview_ratingBar);
                        Log.i(TAG, "positive button is clicked: text = " + comment.getText() + ", rate = " + rate.getRating() + ", product = " + productId);
                        //ctx=getActivity();
                        if (comment.getText().toString() != null) {
                            Review review = new Review();
                            review.setRate(Float.valueOf(rate.getRating()).intValue());
                            review.setText(comment.getText().toString());
                            review.setProductId(productId);
                            PostAsyncTask task = new PostAsyncTask();
                            task.execute(review);

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().cancel();
                    }
                });

        return builder.create();

    }
    private void notifyTarget(int resultCode){
        Fragment fragment = getTargetFragment();
        if(fragment!=null)
            fragment.onActivityResult(getTargetRequestCode(),resultCode,null);
    }

    private class PostAsyncTask extends AsyncTask<Review,Void,String>{
        Review review;

        @Override
        protected String doInBackground(Review... params) {
            ProductDownloader pd = new ProductDownloader();
            review = params[0];
            return pd.postReview(review, ProductLab.getInstance(getActivity()).getUser());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("success")){
                   // review.setId(jsonObject.getInt("review_id"));
                   // ProductLab.getInstance(ctx).getProductReview(productId).add(review);
                    notifyTarget(Activity.RESULT_OK);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
