package com.popcorn;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendsListAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater=null;
    private List<String> myList;

    public FriendsListAdapter(Activity a,List<String> entryList) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myList = entryList;
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

        TextView title = (TextView)vi.findViewById(R.id.friendListName);
        ImageView image =(ImageView)vi.findViewById(R.id.friendsListImage);

        //get drawable for image view
        Drawable myDrawable =  activity.getResources().getDrawable(R.drawable.ic_lock_open_white_24dp);

        //set title and picture here
        title.setText(String.valueOf(myList.get(position)));
        image.setImageDrawable(myDrawable);

        return vi;
    }
}
