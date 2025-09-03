package com.example.pharmacymanager.ui.auth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
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
        if(!validateLoginUsernameOrEmail() | !validatePassword()){
            return;
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }

    private boolean validateLoginUsernameOrEmail() {
        assert username.getEditText() != null;
        String val = username.getEditText().getText().toString().trim();

        String usernamePattern = "^[A-Za-z0-9._]{4,15}$";

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        }
        else if (!val.matches(usernamePattern) && !val.matches(emailRegex)) {
            username.setError("Enter a valid username or email");
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