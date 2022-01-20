package com.example.pcplay.Fragment.BottomNavigation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pcplay.Adapter.FeatureMovieAdapter;
import com.example.pcplay.Adapter.SeriesMovieAdapter;
import com.example.pcplay.Adapter.SliderAdapter;
import com.example.pcplay.Model.DataModel;
import com.example.pcplay.Model.FeatureMovieModel;
import com.example.pcplay.Model.SeriesMovieModel;
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

    private List<SeriesMovieModel> seriesMovieModels;
    private List<FeatureMovieModel> featureMovieModels;


    private RecyclerView rcvSeriesMovies;
    private RecyclerView rcvFeatureMovies;

    private SliderAdapter sliderAdapter;
    private SeriesMovieAdapter seriesMovieAdapter;
    private FeatureMovieAdapter featureMovieAdapter;

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
        //loadData();
        loadSeriesMovieData();


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

    private void loadData() {
        loadSeriesMovieData();
        loadMovieData();
    }

    private void loadSeriesMovieData() {
        //load data from firebase
        DatabaseReference seriesRef = database.getReference("series");
        rcvSeriesMovies = mView.findViewById(R.id.recycleview_series);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        //reverse layout
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcvSeriesMovies.setLayoutManager(linearLayoutManager);

        seriesMovieModels = new ArrayList<>();
        seriesMovieAdapter= new SeriesMovieAdapter(seriesMovieModels);
        rcvSeriesMovies.setAdapter(seriesMovieAdapter);

        seriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot contentSnapshot: snapshot.getChildren()){
                    SeriesMovieModel dataModel = contentSnapshot.getValue(SeriesMovieModel.class);
                    seriesMovieModels.add(dataModel);
                }
                seriesMovieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //when series adapter is loaded, load feature adapter after that
        loadFeatureMovieData();

    }

    private void loadFeatureMovieData() {
        DatabaseReference featureRef = database.getReference("feature");
        rcvFeatureMovies = mView.findViewById(R.id.recycleview_feature);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        //reverse layout
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rcvFeatureMovies.setLayoutManager(linearLayoutManager);

        featureMovieModels = new ArrayList<>();
        featureMovieAdapter= new FeatureMovieAdapter(featureMovieModels);
        rcvFeatureMovies.setAdapter(featureMovieAdapter);

        featureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot contentSnapshot: snapshot.getChildren()){
                    FeatureMovieModel dataModel = contentSnapshot.getValue(FeatureMovieModel.class);
                    featureMovieModels.add(dataModel);
                }
                featureMovieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadMovieData() {
        //implement
    }
}