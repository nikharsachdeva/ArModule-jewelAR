package com.raywenderlich.facespotter.BottomSheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raywenderlich.facespotter.R;

import java.util.ArrayList;
import java.util.List;


public class NecklaceFragment extends Fragment {

    RecyclerView virtualtry_recycler_neck;
    View view;
    NecklaceAdapterVT necklaceAdapterVT;
    List<VirtualTryModel> virtualTryModelList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_necklace, container, false);
        init();
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {

        necklaceAdapterVT = new NecklaceAdapterVT(getActivity(), virtualTryModelList);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        virtualtry_recycler_neck.setLayoutManager(gridLayoutManager);
        virtualtry_recycler_neck.setAdapter(necklaceAdapterVT);

    }

    private void init() {
        virtualtry_recycler_neck = view.findViewById(R.id.virtualtry_recycler_neck);
        virtualTryModelList = new ArrayList<>();
        virtualTryModelList.add(new VirtualTryModel(1, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(2, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(3, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(4, "http://laundrybuoy.com/api/newcknew.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(5, "http://laundrybuoy.com/api/newcknew.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(6, "http://laundrybuoy.com/api/newcknew.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(14, "http://laundrybuoy.com/api/prodddd.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(14, "http://laundrybuoy.com/api/prodddd.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(14, "http://laundrybuoy.com/api/prodddd.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(13, "http://laundrybuoy.com/api/5.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(14, "http://laundrybuoy.com/api/5.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(14, "http://laundrybuoy.com/api/5.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(1, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(1, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(1, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(1, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
        virtualTryModelList.add(new VirtualTryModel(1, "http://laundrybuoy.com/api/neckkk.png", "necklace"));
    }
}