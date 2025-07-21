package edu.uph.m23si1.sobatberbagi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.uph.m23si1.sobatberbagi.auth.LoginActivity;

public class splashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // Durasi splash screen (ms)

    private ImageView logoImage;
    private TextView titleText, taglineText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hubungkan view dari XML
        logoImage = findViewById(R.id.logo);
        titleText = findViewById(R.id.app_name);
        taglineText = findViewById(R.id.tagline);

        // Buat animasi fade-in langsung di Java
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000);
        fadeIn.setFillAfter(true);

        // Terapkan animasi ke elemen
        logoImage.startAnimation(fadeIn);
        titleText.startAnimation(fadeIn);
        taglineText.startAnimation(fadeIn);

        // Pindah ke LoginActivity setelah delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
