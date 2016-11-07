package com.blackboardtheory.bottombarapplication.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboardtheory.bottombarapplication.R;


public class FlightFragment extends Fragment {




    public FlightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Flight Information");
        View view = inflater.inflate(R.layout.view_flight, container, false);
        return view;
    }


}
