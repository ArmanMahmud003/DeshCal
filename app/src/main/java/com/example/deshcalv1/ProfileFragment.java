package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private TextView profileTitle, userInfoText;
    private EditText heightEditText, weightEditText;
    private Button logoutButton, updateProfileButton, deleteProfileButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize Firebase and SharedPreferences
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        
        // Initialize views
        profileTitle = view.findViewById(R.id.profile_title);
        userInfoText = view.findViewById(R.id.user_info_text);
        heightEditText = view.findViewById(R.id.height_edit_text);
        weightEditText = view.findViewById(R.id.weight_edit_text);
        logoutButton = view.findViewById(R.id.logout_button);
        updateProfileButton = view.findViewById(R.id.update_profile_button);
        deleteProfileButton = view.findViewById(R.id.delete_profile_button);
        
        setupUserInfo();
        setupClickListeners();
        loadUserDataFromFirebase();
        
        return view;
    }
    
    private void setupUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        boolean isGuestMode = sharedPreferences.getBoolean("guest_mode", false);
        
        if (currentUser != null && !isGuestMode) {
            // Logged in user
            profileTitle.setText("Profile");
            String email = currentUser.getEmail();
            userInfoText.setText("Logged in as:\n" + (email != null ? email : "User"));
            logoutButton.setText("Logout");
            
            // Show profile management controls for logged-in users
            heightEditText.setVisibility(View.VISIBLE);
            weightEditText.setVisibility(View.VISIBLE);
            updateProfileButton.setVisibility(View.VISIBLE);
            deleteProfileButton.setVisibility(View.VISIBLE);
        } else {
            // Guest mode
            profileTitle.setText("Guest Profile");
            userInfoText.setText("You are browsing as a guest.\n\nSign up to save your progress and unlock all features!");
            logoutButton.setText("Sign Up");
            
            // Hide profile management controls for guests
            heightEditText.setVisibility(View.GONE);
            weightEditText.setVisibility(View.GONE);
            updateProfileButton.setVisibility(View.GONE);
            deleteProfileButton.setVisibility(View.GONE);
        }
    }
    
    private void loadUserDataFromFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        boolean isGuestMode = sharedPreferences.getBoolean("guest_mode", false);
        
        if (currentUser != null && !isGuestMode) {
            String uid = currentUser.getUid();
            
            databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Extract height and weight data
                        Object heightFtObj = snapshot.child("height_ft").getValue();
                        Object heightInObj = snapshot.child("height_in").getValue();
                        Object weightObj = snapshot.child("weight").getValue();
                        
                        // Convert height to decimal format (ft.in)
                        if (heightFtObj != null && heightInObj != null) {
                            int heightFt = (heightFtObj instanceof Long) ? ((Long) heightFtObj).intValue() : (Integer) heightFtObj;
                            int heightIn = (heightInObj instanceof Long) ? ((Long) heightInObj).intValue() : (Integer) heightInObj;
                            double heightDecimal = heightFt + (heightIn / 12.0);
                            heightEditText.setText(String.format("%.1f", heightDecimal));
                        }
                        
                        // Set weight
                        if (weightObj != null) {
                            if (weightObj instanceof Double) {
                                weightEditText.setText(String.valueOf(((Double) weightObj).floatValue()));
                            } else if (weightObj instanceof Long) {
                                weightEditText.setText(String.valueOf(((Long) weightObj).floatValue()));
                            } else {
                                weightEditText.setText(String.valueOf(weightObj));
                            }
                        }
                    }
                }
                
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to load profile data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void updateUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String heightStr = heightEditText.getText().toString().trim();
        String weightStr = weightEditText.getText().toString().trim();
        
        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in both height and weight", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double heightDecimal = Double.parseDouble(heightStr);
            float weight = Float.parseFloat(weightStr);
            
            // Convert decimal height to feet and inches
            int heightFt = (int) heightDecimal;
            int heightIn = (int) Math.round((heightDecimal - heightFt) * 12);
            
            String uid = currentUser.getUid();
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("height_ft", heightFt);
            updates.put("height_in", heightIn);
            updates.put("weight", weight);
            
            databaseReference.child("users").child(uid).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter valid numbers for height and weight", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String uid = currentUser.getUid();
        
        // First delete user data from database
        databaseReference.child("users").child(uid).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data deleted successfully, now delete auth account
                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Account deleted successfully
                                    Toast.makeText(getActivity(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                                    
                                    // Clear SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    
                                    // Sign out and redirect to LoginActivity
                                    auth.signOut();
                                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to delete profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void setupClickListeners() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = auth.getCurrentUser();
                boolean isGuestMode = sharedPreferences.getBoolean("guest_mode", false);
                
                if (currentUser != null && !isGuestMode) {
                    // Logout
                    auth.signOut();
                    
                    // Clear SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    
                    // Navigate to MainActivity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // Navigate to login for guest users
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivity(intent);
                }
            }
        });
        
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
        
        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUserProfile();
            }
        });
    }
}