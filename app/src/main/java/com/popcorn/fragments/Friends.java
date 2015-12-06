package com.popcorn.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.popcorn.FriendsListAdapter;
import com.popcorn.R;

import java.util.ArrayList;
import java.util.List;


public class Friends extends Fragment {
    private List<String>  myDataSet;
    private FriendsListAdapter listAdapter;
    private ListView friendsListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myDataSet = new ArrayList<>();
        //replease this list with json
        myDataSet.add("Time");
        myDataSet.add("Nut");
        myDataSet.add("Tae");

        //get layout inflator
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListView = (ListView)view.findViewById(R.id.friendsListView);

        listAdapter = new FriendsListAdapter(getActivity(), myDataSet);
        friendsListView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        return view;
    }
}
