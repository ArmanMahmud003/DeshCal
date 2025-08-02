package com.example.deshcalv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private TextView profileTitle, userInfoText;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize Firebase and SharedPreferences
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("UserData", getActivity().MODE_PRIVATE);
        
        // Initialize views
        profileTitle = view.findViewById(R.id.profile_title);
        userInfoText = view.findViewById(R.id.user_info_text);
        logoutButton = view.findViewById(R.id.logout_button);
        
        setupUserInfo();
        setupClickListeners();
        
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
        } else {
            // Guest mode
            profileTitle.setText("Guest Profile");
            userInfoText.setText("You are browsing as a guest.\n\nSign up to save your progress and unlock all features!");
            logoutButton.setText("Sign Up");
        }
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
    }
}