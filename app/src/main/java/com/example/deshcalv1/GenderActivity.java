package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GenderActivity extends AppCompatActivity {

    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private Button nextButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gender);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Initialize views
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        maleRadioButton = findViewById(R.id.male_radio_button);
        femaleRadioButton = findViewById(R.id.female_radio_button);
        nextButton = findViewById(R.id.next_button);

        // Set click listener for next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                
                if (selectedId == -1) {
                    Toast.makeText(GenderActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedGender;
                if (selectedId == R.id.male_radio_button) {
                    selectedGender = "Male";
                } else {
                    selectedGender = "Female";
                }

                // Save gender to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("gender", selectedGender);
                editor.apply();

                // Navigate to AgeActivity
                Intent intent = new Intent(GenderActivity.this, AgeActivity.class);
                startActivity(intent);
            }
        });
    }
}