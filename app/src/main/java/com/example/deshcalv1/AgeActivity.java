package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AgeActivity extends AppCompatActivity {

    private EditText ageEditText;
    private Button nextButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_age);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Initialize views
        ageEditText = findViewById(R.id.age_edit_text);
        nextButton = findViewById(R.id.next_button);

        // Set click listener for next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ageString = ageEditText.getText().toString().trim();
                
                if (ageString.isEmpty()) {
                    Toast.makeText(AgeActivity.this, "Please enter your age", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int age = Integer.parseInt(ageString);
                    
                    if (age < 10 || age > 120) {
                        Toast.makeText(AgeActivity.this, "Please enter a valid age (10-120)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save age to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("age", age);
                    editor.apply();

                    // Navigate to BodyMetricsActivity
                    Intent intent = new Intent(AgeActivity.this, BodyMetricsActivity.class);
                    startActivity(intent);
                    
                } catch (NumberFormatException e) {
                    Toast.makeText(AgeActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}