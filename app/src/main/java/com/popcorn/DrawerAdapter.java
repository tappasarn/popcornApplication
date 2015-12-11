package com.popcorn;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DrawerAdapter extends BaseAdapter {

    private Context context;
    private SimpleDrawerItem[] items;

    public DrawerAdapter(Context context, SimpleDrawerItem[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(context, R.layout.element_drawer_list, null);

        ImageView iconImage = (ImageView) view.findViewById(R.id.drawerListIcon);
        TextView menuText = (TextView) view.findViewById(R.id.drawerListText);

        iconImage.setImageDrawable(ContextCompat.getDrawable(context, items[position].getIconResId()));
        menuText.setText(items[position].getText());

        return view;
    }

}
