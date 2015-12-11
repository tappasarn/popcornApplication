package com.popcorn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.popcorn.config.Configurations;

import java.util.List;

public class FriendsListAdapter extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater = null;

    private List<String> myList;
    private List<String> imageList;
    private List<Integer> reviewCountList;
    private RequestQueue requestQueue;
    private Integer count;

    public FriendsListAdapter(Context context,List<String> entryList, List<String> imageList, List<Integer> reviewCountList) {
        this.context = context;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.myList = entryList;
        this.imageList = imageList;
        this.reviewCountList = reviewCountList;

        requestQueue = Volley.newRequestQueue(context);
        count = 0;
    }

    public int getCount() {
        return myList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.element_friend_info_cell, null);
        }

        TextView nameText = (TextView) vi.findViewById(R.id.friendListName);
        ImageView profileImage = (ImageView) vi.findViewById(R.id.friendsListImage);
        TextView reviewCountText = (TextView) vi.findViewById(R.id.reviewCountText);

        Log.d("myList",myList.toString());
        Log.d("imageList", imageList.toString());
        Log.d("reviewCountList", reviewCountList.toString());

        // Set nameText and picture
        nameText.setText(String.valueOf(myList.get(position)));
        reviewCountText.setText(String.format("Points: %d Pts", reviewCountList.get(position)));
        initializeImageViews(profileImage, imageList.get(position));

        return vi;
    }

    private void initializeImageViews(final ImageView image, String currentImg) {

        Log.d("SUPERMAN", currentImg+count.toString());
        count++;

        // Call to an API to get profileImage
        ImageRequest imageRequest = new ImageRequest(
                String.format(Configurations.API.RESOURCE.PROFILE_IMG_URL, currentImg),

                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        image.setImageBitmap(response);
                    }
                },
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ALPHA_8,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Drawable myDrawable =  ContextCompat.getDrawable(context, R.drawable.ic_error_outline_black_24dp);
                        image.setImageDrawable(myDrawable);
                    }
                }
        );
        requestQueue.add(imageRequest);

    }
}
