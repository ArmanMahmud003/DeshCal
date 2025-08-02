package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    
    private TextView greetingText, bmiValueText, bmiCategoryText, bmrValueText;
    private Button logFoodButton, viewDietPlanButton, healthTipsButton;
    private CardView guestBannerCard;
    private TextView guestBannerText;
    private Button createAccountButton;
    
    private boolean isGuestMode = false;
    private String[] healthTips = {
        "Drink at least 8 glasses of water daily for better metabolism.",
        "Include protein in every meal to maintain muscle mass.",
        "Take a 10-minute walk after each meal to aid digestion.",
        "Eat colorful fruits and vegetables for essential vitamins.",
        "Get 7-9 hours of quality sleep for better health.",
        "Practice portion control to maintain a healthy weight.",
        "Choose whole grains over refined grains for better nutrition.",
        "Limit processed foods and cook fresh meals at home."
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize Firebase and SharedPreferences
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        
        // Check if user is in guest mode
        isGuestMode = sharedPreferences.getBoolean("guest_mode", false);
        
        // Initialize UI components
        initializeViews(view);
        loadUserData();
        setupClickListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        greetingText = view.findViewById(R.id.greeting_text);
        bmiValueText = view.findViewById(R.id.bmi_value_text);
        bmiCategoryText = view.findViewById(R.id.bmi_category_text);
        bmrValueText = view.findViewById(R.id.bmr_value_text);
        logFoodButton = view.findViewById(R.id.log_food_button);
        viewDietPlanButton = view.findViewById(R.id.view_diet_plan_button);
        healthTipsButton = view.findViewById(R.id.health_tips_button);
        guestBannerCard = view.findViewById(R.id.guest_banner_card);
        guestBannerText = view.findViewById(R.id.guest_banner_text);
        createAccountButton = view.findViewById(R.id.create_account_button);
    }
    
    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        
        if (currentUser != null && !isGuestMode) {
            // User is logged in - load data from Firebase
            greetingText.setText("Hi, " + (currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "User") + "!");
            guestBannerCard.setVisibility(View.GONE);
            loadFirebaseData(currentUser.getUid());
        } else {
            // Guest mode - load data from SharedPreferences
            greetingText.setText("Hi, Guest!");
            guestBannerCard.setVisibility(View.VISIBLE);
            loadSharedPreferencesData();
        }
    }
    
    private void loadFirebaseData(String uid) {
        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double bmi = dataSnapshot.child("bmi").getValue(Double.class);
                    Double bmr = dataSnapshot.child("bmr").getValue(Double.class);
                    
                    if (bmi != null && bmr != null) {
                        displayMetrics(bmi, bmr);
                    } else {
                        showDefaultValues();
                    }
                } else {
                    showDefaultValues();
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                showDefaultValues();
            }
        });
    }
    
    private void loadSharedPreferencesData() {
        float bmi = sharedPreferences.getFloat("bmi", 0);
        float bmr = sharedPreferences.getFloat("bmr", 0);
        
        if (bmi > 0 && bmr > 0) {
            displayMetrics((double) bmi, (double) bmr);
        } else {
            showDefaultValues();
        }
    }
    
    private void displayMetrics(double bmi, double bmr) {
        bmiValueText.setText(String.format("%.1f", bmi));
        bmrValueText.setText(String.format("%.0f cal/day", bmr));
        
        String bmiCategory = getBMICategory(bmi);
        bmiCategoryText.setText(bmiCategory);
        
        if (bmiCategory.equals("Normal")) {
            bmiCategoryText.setTextColor(Color.GREEN);
        } else {
            bmiCategoryText.setTextColor(Color.RED);
        }
    }
    
    private void showDefaultValues() {
        bmiValueText.setText("--");
        bmiCategoryText.setText("Not calculated");
        bmiCategoryText.setTextColor(Color.GRAY);
        bmrValueText.setText("-- cal/day");
    }
    
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 25) {
            return "Normal";
        } else if (bmi >= 25 && bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
    
    private void setupClickListeners() {
        logFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Log Food - Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });
        
        viewDietPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "View Diet Plan - Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });
        
        healthTipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRandomHealthTip();
            }
        });
        
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void showRandomHealthTip() {
        Random random = new Random();
        int randomIndex = random.nextInt(healthTips.length);
        String tip = healthTips[randomIndex];
        Toast.makeText(getActivity(), "💡 " + tip, Toast.LENGTH_LONG).show();
    }
}