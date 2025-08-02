package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    private TextView bmiValueText, bmiCategoryText, bmrValueText, recommendationText;
    private Button dietPlanButton, guestModeButton;
    private SharedPreferences sharedPreferences;
    
    private String gender;
    private int age, heightFt, heightIn;
    private float weight;
    private double bmi, bmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Initialize views
        bmiValueText = findViewById(R.id.bmi_value_text);
        bmiCategoryText = findViewById(R.id.bmi_category_text);
        bmrValueText = findViewById(R.id.bmr_value_text);
        recommendationText = findViewById(R.id.recommendation_text);
        dietPlanButton = findViewById(R.id.diet_plan_button);
        guestModeButton = findViewById(R.id.guest_mode_button);

        // Retrieve data from SharedPreferences
        retrieveUserData();
        
        // Calculate BMI and BMR
        calculateMetrics();
        
        // Display results
        displayResults();
        
        // Set click listeners
        setupClickListeners();
    }
    
    private void retrieveUserData() {
        gender = sharedPreferences.getString("gender", "");
        age = sharedPreferences.getInt("age", 0);
        heightFt = sharedPreferences.getInt("height_ft", 0);
        heightIn = sharedPreferences.getInt("height_in", 0);
        weight = sharedPreferences.getFloat("weight", 0);
    }
    
    private void calculateMetrics() {
        // Convert height to meters for BMI calculation
        double totalInches = (heightFt * 12) + heightIn;
        double heightInMeters = totalInches * 0.0254;
        
        // Calculate BMI
        bmi = weight / (heightInMeters * heightInMeters);
        
        // Convert height to cm for BMR calculation
        double heightInCm = totalInches * 2.54;
        
        // Calculate BMR using Mifflin-St Jeor Equation
        if (gender.equals("Male")) {
            bmr = (10 * weight) + (6.25 * heightInCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * heightInCm) - (5 * age) - 161;
        }
        
        // Save calculated values to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("bmi", (float) bmi);
        editor.putFloat("bmr", (float) bmr);
        editor.apply();
    }
    
    private void displayResults() {
        // Display BMI
        bmiValueText.setText(String.format("%.1f", bmi));
        
        // Display BMI category and set color
        String bmiCategory = getBMICategory(bmi);
        bmiCategoryText.setText(bmiCategory);
        
        if (bmiCategory.equals("Normal")) {
            bmiCategoryText.setTextColor(Color.GREEN);
        } else {
            bmiCategoryText.setTextColor(Color.RED);
        }
        
        // Display BMR
        bmrValueText.setText(String.format("%.0f calories/day", bmr));
        
        // Display recommendation
        String recommendation = getRecommendation(bmi);
        recommendationText.setText(recommendation);
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
    
    private String getRecommendation(double bmi) {
        if (bmi < 18.5) {
            return "You may need to gain weight. Consider consulting with a nutritionist for a healthy weight gain plan.";
        } else if (bmi >= 18.5 && bmi < 25) {
            return "Great! You have a healthy BMI. Maintain your current lifestyle with balanced diet and exercise.";
        } else if (bmi >= 25 && bmi < 30) {
            return "You may benefit from losing some weight. A balanced diet and regular exercise can help.";
        } else {
            return "Consider consulting with a healthcare professional for a personalized weight loss plan.";
        }
    }
    
    private void setupClickListeners() {
        dietPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to Login page for detailed diet plan
                Intent intent = new Intent(ResultActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
        
        guestModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set guest mode flag and redirect to HomeActivity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("guest_mode", true);
                editor.apply();
                
                Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}