package com.example.pcplay.Fragment.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.pcplay.Activity.LogActivity.SignInActivity;
import com.example.pcplay.R;

public class OnBoardingFragment3 extends Fragment {

    private Button btnStart;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_on_boarding3, container, false);

        btnStart = mView.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), SignInActivity.class));
                getActivity().finish();
            }
        });

        return mView;

    }
}