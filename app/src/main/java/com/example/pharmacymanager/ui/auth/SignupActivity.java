package com.example.pharmacymanager.ui.auth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    Button CallSignIn,login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username,email,password,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hooks
        CallSignIn = findViewById(R.id.signin_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
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
                pairs[3] = new Pair<View,String>(username,"logo_user");
                pairs[4] = new Pair<View,String>(password,"logo_password");
                pairs[5] = new Pair<View,String>(login_btn,"buttonlogin_trans");
                pairs[6] = new Pair<View,String>(CallSignIn,"signin_signup_trans");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }

    public void registerUser(View view){
        if(!validateUsername() | !validateEmail() | !validatePassword() | !validateConfirmPassword()){
            return;
        }
        assert username.getEditText() != null;
        assert email.getEditText() != null;
        assert password.getEditText() != null;

        String name = username.getEditText().getText().toString().trim();
        String emailVal = email.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();

        login_btn.setEnabled(false);
        new com.example.pharmacymanager.data.repositories.AuthRepository(this)
                .register(name, emailVal, pass, new com.example.pharmacymanager.data.repositories.AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        login_btn.setEnabled(true);
                        Toast.makeText(SignupActivity.this, "Registered successfully. Please log in.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        login_btn.setEnabled(true);
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean validateUsername(){
        assert username.getEditText() != null;
        String val = username.getEditText().getText().toString().trim();

        String noWhiteSpace = "^[A-Za-z0-9._]{4,15}$";
        // Only letters, digits, . and _, between 4-15 chars

        if(val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        }
        else if (!val.matches(noWhiteSpace)) {
            username.setError("Username must be 4-15 chars, no spaces, only letters/numbers/._");
            return false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail(){
        assert email.getEditText() != null;
        String val = email.getEditText().getText().toString();
        String noWhiteSpacePattern = "^[^\\s]+$";
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if(val.trim().isEmpty())
        {
            email.setError("Field Can not be empty");
            return false;
        } else if (!val.matches(noWhiteSpacePattern))
        {
            email.setError("White spaces is not allowed");
            return false;
        } else if(!val.matches(emailRegex))
        {
            email.setError("Email format is invalid");
            return false;
        }
        else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(){
        assert password.getEditText() != null;
        String val = password.getEditText().getText().toString();

        if(val.trim().isEmpty())
        {
            password.setError("Field Can not be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword(){
        assert confirmPassword.getEditText() != null;
        assert password.getEditText() != null;

        String val = confirmPassword.getEditText().getText().toString();
        String valPassword = password.getEditText().getText().toString();

        if(val.trim().isEmpty()) {
            confirmPassword.setError("Field Can not be empty");
            return false;
        }
        else if(!val.equals(valPassword)) {
            confirmPassword.setError("Passwords do not match");
            return false;
        }
        else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }
}