package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private EditText loginEmail,loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        
        loginEmail =findViewById(R.id.login_email);
        loginPassword =findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=loginEmail.getText().toString();
                String pass=loginPassword.getText().toString();


                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    if(!pass.isEmpty()){
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        // Check if user is authenticated before proceeding
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                        if (currentUser != null) {
                                            saveUserDataToFirebase();
                                        } else {
                                            // Authentication failed somehow, show error
                                            Toast.makeText(LogInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        loginPassword.setError("Password cannot be empty");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Email cannot be empty");
                    
                }else{
                        loginEmail.setError("Please enter valid email");
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                                
                                navigateToHome();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LogInActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(LogInActivity.this, "User authentication error", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}