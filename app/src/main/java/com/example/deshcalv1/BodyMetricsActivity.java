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

public class BodyMetricsActivity extends AppCompatActivity {

    private EditText feetEditText, inchesEditText, weightEditText;
    private Button nextButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_body_metrics);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Initialize views
        feetEditText = findViewById(R.id.feet_edit_text);
        inchesEditText = findViewById(R.id.inches_edit_text);
        weightEditText = findViewById(R.id.weight_edit_text);
        nextButton = findViewById(R.id.next_button);

        // Set click listener for next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feetString = feetEditText.getText().toString().trim();
                String inchesString = inchesEditText.getText().toString().trim();
                String weightString = weightEditText.getText().toString().trim();
                
                if (feetString.isEmpty() || inchesString.isEmpty() || weightString.isEmpty()) {
                    Toast.makeText(BodyMetricsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int feet = Integer.parseInt(feetString);
                    int inches = Integer.parseInt(inchesString);
                    double weight = Double.parseDouble(weightString);
                    
                    // Validation
                    if (feet < 3 || feet > 8) {
                        Toast.makeText(BodyMetricsActivity.this, "Please enter valid height in feet (3-8)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (inches < 0 || inches > 11) {
                        Toast.makeText(BodyMetricsActivity.this, "Please enter valid inches (0-11)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (weight < 30 || weight > 300) {
                        Toast.makeText(BodyMetricsActivity.this, "Please enter valid weight (30-300 kg)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save metrics to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("height_ft", feet);
                    editor.putInt("height_in", inches);
                    editor.putFloat("weight", (float) weight);
                    editor.apply();

                    // Navigate to ResultActivity
                    Intent intent = new Intent(BodyMetricsActivity.this, ResultActivity.class);
                    startActivity(intent);
                    
                } catch (NumberFormatException e) {
                    Toast.makeText(BodyMetricsActivity.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}