package com.popcorn;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
    private TextView title;
    private ImageView image;
    private Activity activity;
    private static LayoutInflater inflater=null;
    private List<String> myList;
    private RequestQueue requestQueue;
    private List<String> imageList;

    public FriendsListAdapter(Activity a,List<String> entryList, List<String> imageList) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myList = entryList;
        this.imageList = imageList;
        requestQueue = Volley.newRequestQueue(a);
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
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_friends_info, null);

        title = (TextView)vi.findViewById(R.id.friendListName);
        image =(ImageView)vi.findViewById(R.id.friendsListImage);

        Log.d("myList",myList.toString());
        Log.d("imageList", imageList.toString());
        //set title and picture here
        title.setText(String.valueOf(myList.get(position)));
        initializeImageViews(image, imageList.get(position));

        return vi;
    }

    private void initializeImageViews(final ImageView image, String currentImg) {
        Log.d("DEBUG", currentImg);

        // Call to an API to get image
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
                        Drawable myDrawable =  activity.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp);
                        image.setImageDrawable(myDrawable);
                    }
                }
        );
        requestQueue.add(imageRequest);

    }
}
