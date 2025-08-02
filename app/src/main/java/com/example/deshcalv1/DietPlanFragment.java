package com.example.deshcalv1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class DietPlanFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_plan, container, false);
        
        TextView titleText = view.findViewById(R.id.diet_plan_title);
        TextView contentText = view.findViewById(R.id.diet_plan_content);
        
        titleText.setText("Diet Plan");
        contentText.setText("Your personalized diet plan will appear here.\n\nThis feature is coming soon!");
        
        return view;
    }
}