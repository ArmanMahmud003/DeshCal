package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private EditText signpuEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        
        auth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        
        signpuEmail=findViewById(R.id.signup_email);
        signupPassword =findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signpuEmail.getText().toString().trim();
                String pass=signupPassword.getText().toString().trim();
                if (user.isEmpty()){
                    signpuEmail.setError("Email cannot be empty");

                }
                if (pass.isEmpty())
                {
                    signupPassword.setError("Password cannot be Empty");
                }
                else
                {
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                // Check if user is authenticated before proceeding
                                FirebaseUser currentUser = auth.getCurrentUser();
                                if (currentUser != null) {
                                    saveUserDataToFirebase();
                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    // Authentication failed somehow, show error
                                    Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(SignUpActivity.this, "Signup Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(SignUpActivity.this,LogInActivity.class));
            }
        });
    }
    
    private void saveUserDataToFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            
            // Get data from SharedPreferences
            String gender = sharedPreferences.getString("gender", "");
            int age = sharedPreferences.getInt("age", 0);
            int heightFt = sharedPreferences.getInt("height_ft", 0);
            int heightIn = sharedPreferences.getInt("height_in", 0);
            float weight = sharedPreferences.getFloat("weight", 0);
            float bmi = sharedPreferences.getFloat("bmi", 0);
            float bmr = sharedPreferences.getFloat("bmr", 0);
            
            // Check if user data exists in SharedPreferences
            if (!gender.isEmpty() && age > 0) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("gender", gender);
                userData.put("age", age);
                userData.put("height_ft", heightFt);
                userData.put("height_in", heightIn);
                userData.put("weight", weight);
                userData.put("bmi", bmi);
                userData.put("bmr", bmr);
                
                databaseReference.child("users").child(uid).setValue(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Clear guest mode flag
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("guest_mode", false);
                                editor.apply();
                                
                                Toast.makeText(SignUpActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                // Still navigate to home even if data save fails
                                navigateToHome();
                            }
                        });
            } else {
                // No data in SharedPreferences, clear guest mode and go to HomeActivity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("guest_mode", false);
                editor.apply();
                navigateToHome();
            }
        } else {
            Toast.makeText(SignUpActivity.this, "User authentication error", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}