package com.example.pcplay.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pcplay.Adapter.SliderAdapter;
import com.example.pcplay.Model.DataModel;
import com.example.pcplay.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.animation.type.SlideAnimation;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private View mView;
    private List<DataModel> dataModels;
    private SliderAdapter sliderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FirebaseApp.initializeApp(getContext());

        mView =  inflater.inflate(R.layout.fragment_home, container, false);

        SliderView sliderView = mView.findViewById(R.id.slideView_Home_Fragment);
        sliderAdapter = new SliderAdapter(getContext());
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.NONE);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setScrollTimeInSec(6);
        renewItems(sliderView);

        //Load data from firebase
        loadFirebaseForSlider();


        return mView;

    }

    private void renewItems(View view) {
        dataModels = new ArrayList<>();
        DataModel dataItems = new DataModel();
        dataModels.add(dataItems);

        sliderAdapter.renewItems(dataModels);
        sliderAdapter.deleteItem(0);
    }

    private void loadFirebaseForSlider() {
        myRef.child("trailer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot contentSlider : snapshot.getChildren()){
                    DataModel sliderItem = contentSlider.getValue(DataModel.class);
                    dataModels.add(sliderItem);

                }
                sliderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
    }

}