package com.blackboardtheory.bottombarapplication.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboardtheory.bottombarapplication.R;


public class CabinFragment extends Fragment {


    public CabinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Cabin Control");
        View view = inflater.inflate(R.layout.view_cabin, container, false);
        return view;
    }

}
