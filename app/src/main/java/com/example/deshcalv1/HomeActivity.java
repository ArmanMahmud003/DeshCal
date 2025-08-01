package com.example.deshcalv1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView welcomeText, bmiStatusText, calorieGoalText;
    private Button viewDietPlanButton, logFoodButton, trackProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is authenticated
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        
        if (currentUser == null) {
            // User is not authenticated, redirect to login
            Toast.makeText(this, "Please log in to access this page", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LogInActivity.class));
            finish();
            return;
        }
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize UI components
        initializeViews();
        setupClickListeners();
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcome_text);
        bmiStatusText = findViewById(R.id.bmi_status_text);
        calorieGoalText = findViewById(R.id.calorie_goal_text);
        viewDietPlanButton = findViewById(R.id.view_diet_plan_button);
        logFoodButton = findViewById(R.id.log_food_button);
        trackProgressButton = findViewById(R.id.track_progress_button);
        
        // Set welcome message
        welcomeText.setText("Welcome to DeshCal!");
        bmiStatusText.setText("BMI Status: Not calculated yet");
        calorieGoalText.setText("Daily Calorie Goal: Not set");
    }
    
    private void setupClickListeners() {
        viewDietPlanButton.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "View Diet Plan - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
        
        logFoodButton.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Log Food - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
        
        trackProgressButton.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Track Progress - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }
}