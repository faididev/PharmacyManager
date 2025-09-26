package com.example.pharmacymanager.ui.auth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pharmacymanager.ui.MainActivity;
import com.example.pharmacymanager.R;
import com.google.android.material.textfield.TextInputLayout;
import com.example.pharmacymanager.data.repositories.AuthRepository;

public class LoginActivity extends AppCompatActivity {

    // Vars
    Button callSignUp,login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username,email,password,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hooks
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View,String>(image,"logo_image");
                pairs[1] = new Pair<View,String>(logoText,"logo_text");
                pairs[2] = new Pair<View,String>(sloganText,"logo_desc");
                pairs[3] = new Pair<View,String>(username,"logo_user");
                pairs[4] = new Pair<View,String>(password,"logo_password");
                pairs[5] = new Pair<View,String>(login_btn,"buttonlogin_trans");
                pairs[6] = new Pair<View,String>(callSignUp,"signin_signup_trans");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }


    public void loginUser(View view){
        if(!validateEmailOnly() | !validatePassword()){
            return;
        }

        assert username.getEditText() != null;
        assert password.getEditText() != null;
        String emailVal = username.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();

        login_btn.setEnabled(false);

        new AuthRepository(this).login(emailVal, pass, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                login_btn.setEnabled(true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                login_btn.setEnabled(true);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmailOnly(){
        assert username.getEditText() != null;
        String val = username.getEditText().getText().toString().trim();

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (val.isEmpty()) {
            username.setError("Email cannot be empty");
            return false;
        }
        else if (!val.matches(emailRegex)) {
            username.setError("Enter a valid email");
            return false;
        }
        else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(){
        assert password.getEditText() != null;
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

}