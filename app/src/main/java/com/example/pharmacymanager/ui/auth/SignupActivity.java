package com.example.pharmacymanager.ui.auth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pharmacymanager.R;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    Button CallSignIn, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout name, email, password, confirmPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hooks
        CallSignIn = findViewById(R.id.signin_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        login_btn = findViewById(R.id.signup_screen);

        CallSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View,String>(image,"logo_image");
                pairs[1] = new Pair<View,String>(logoText,"logo_text");
                pairs[2] = new Pair<View,String>(sloganText,"logo_desc");
                pairs[3] = new Pair<View,String>(name,"logo_user");
                pairs[4] = new Pair<View,String>(password,"logo_password");
                pairs[5] = new Pair<View,String>(login_btn,"buttonlogin_trans");
                pairs[6] = new Pair<View,String>(CallSignIn,"signin_signup_trans");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }

    public void registerUser(View view) {
        if (!validateName() || !validateEmail() || !validatePassword() || !validateConfirmPassword()) {
            return;
        }
        
        assert name.getEditText() != null;
        assert email.getEditText() != null;
        assert password.getEditText() != null;

        String nameVal = name.getEditText().getText().toString().trim();
        String emailVal = email.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();

        // Show loading state
        showLoading(true);

        new com.example.pharmacymanager.data.repositories.AuthRepository(this)
                .register(nameVal, emailVal, pass, new com.example.pharmacymanager.data.repositories.AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        showLoading(false);
                        Toast.makeText(SignupActivity.this, "Account created successfully! You are now logged in.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, com.example.pharmacymanager.ui.MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        showLoading(false);
                        Toast.makeText(SignupActivity.this, "Registration failed: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        if (show) {
            login_btn.setEnabled(false);
            login_btn.setText("Creating Account...");
        } else {
            login_btn.setEnabled(true);
            login_btn.setText("Register");
        }
    }
    private boolean validateName() {
        assert name.getEditText() != null;
        String val = name.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            name.setError("Full name is required");
            return false;
        } else if (val.length() < 2) {
            name.setError("Name must be at least 2 characters");
            return false;
        } else if (val.length() > 50) {
            name.setError("Name must be less than 50 characters");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        assert email.getEditText() != null;
        String val = email.getEditText().getText().toString().trim();
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (val.isEmpty()) {
            email.setError("Email is required");
            return false;
        } else if (!val.matches(emailRegex)) {
            email.setError("Please enter a valid email address");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        assert password.getEditText() != null;
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Password is required");
            return false;
        } else if (val.length() < 8) {
            password.setError("Password must be at least 8 characters");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        assert confirmPassword.getEditText() != null;
        assert password.getEditText() != null;

        String val = confirmPassword.getEditText().getText().toString();
        String valPassword = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            confirmPassword.setError("Please confirm your password");
            return false;
        } else if (!val.equals(valPassword)) {
            confirmPassword.setError("Passwords do not match");
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }

}