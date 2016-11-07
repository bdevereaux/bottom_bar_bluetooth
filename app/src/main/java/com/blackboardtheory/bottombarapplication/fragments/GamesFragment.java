package com.blackboardtheory.bottombarapplication.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboardtheory.bottombarapplication.R;


public class GamesFragment extends Fragment {


    public GamesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Games");
        View view = inflater.inflate(R.layout.view_games, container, false);
        return view;
    }

}
