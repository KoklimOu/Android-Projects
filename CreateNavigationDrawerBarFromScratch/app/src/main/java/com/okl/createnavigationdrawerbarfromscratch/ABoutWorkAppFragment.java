package com.okl.createnavigationdrawerbarfromscratch;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ABoutWorkAppFragment extends Fragment {
    private static final String TAG = "ABoutWorkAppFragment";
    String[] policies;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_a_bout_work_app, container, false);

//        if (getArguments() != null) {
//            policies = getArguments().getStringArray("policies");
//            Log.e("TAG", "onCreateView: "+policies);
//        }

        mainActivity = new MainActivity();
        policies = mainActivity.getPolicy();

//        Log.e(TAG, "onCreateView: "+po);

        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ListPolicyAdapter(policies));


        return view;
    }
}