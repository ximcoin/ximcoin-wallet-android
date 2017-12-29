package tech.duchess.luminawallet.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tech.duchess.luminawallet.view.wallet.WalletActivity;

/**
 * Activity serving as a splash screen during cold boot. This should be taken out if no loading
 * actually needs to take place at app start.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Observe that this activity has no views explicitly set. This activity simply serves as a
        //screen to display during cold boot, where in which the view displayed is determined by
        //the theme configured for this activity within the android manifest.
        startActivity(new Intent(this, WalletActivity.class));
        //Important to call finish to ensure the user can't back into this activity.
        finish();
    }

}
