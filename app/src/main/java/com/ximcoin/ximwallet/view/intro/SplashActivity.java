package com.ximcoin.ximwallet.view.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.view.account.AccountsActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DURATION = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        scheduleTransition();
    }

    private void scheduleTransition() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, AccountsActivity.class));
            finish();
        }, SPLASH_SCREEN_DURATION);
    }
}
