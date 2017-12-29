package tech.duchess.luminawallet.view.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.model.api.HorizonApi;
import tech.duchess.luminawallet.model.dagger.SchedulerProvider;
import tech.duchess.luminawallet.model.dagger.module.ActivityLifecycleModule;

/**
 * Displays a Stellar Lumen Wallet, with it's respective features (transactions, send, receive,
 * etc...).
 */
public class WalletActivity extends RxAppCompatActivity {
    private static final String TAG = WalletActivity.class.getSimpleName();
    @Inject
    HorizonApi horizonApi;

    @Inject
    SchedulerProvider schedulerProvider;

    @Inject
    LifecycleProvider<ActivityEvent> lifecycleProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Just testing out our dagger setup
        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new ActivityLifecycleModule(this))
                .inject(this);

        if (horizonApi != null) {
            horizonApi.getAccount("GCEZWKCA5VLDNRLN3RPRJMRZOX3Z6G5CHCGSNFHEYVXM3XOJMDS674JZ")
                    .compose(schedulerProvider.appScheduler())
                    .compose(lifecycleProvider.bindToLifecycle())
                    .subscribe(account -> Log.d(TAG, account.toString()));
        }
    }
}
