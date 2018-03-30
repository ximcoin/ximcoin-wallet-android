package com.ximcoin.ximwallet.view.exportidweb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ximcoin.ximwallet.EnvironmentConstants;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.view.util.TextUtils;
import com.ximcoin.ximwallet.view.util.ViewUtils;

import org.stellar.sdk.KeyPair;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExportIdWebviewActivity extends AppCompatActivity {
    private static final String LOGIN_SUCCESS_SIGNIFIER = "login=good";
    private static final String ACCOUNT_ID_ARG = "ExportIdWebviewActivity.ACCOUNT_ID_ARG";
    public static final String ACCOUNT_SEED_KEY = "ExportIdWebviewActivity.ACCOUNT_SEED_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.webview)
    WebView webView;

    @Nullable
    private String accountSeed;

    /**
     * Gets the intent for this activity.
     * @param accountId Non-null if we're filling a pre-existing account. Null if we're generating a
     *                  new account to fill with XIM.
     * @return Intent for this activity.
     */
    @NonNull
    public static Intent getIntent(@Nullable String accountId, @NonNull Context context) {
        Intent intent = new Intent(context, ExportIdWebviewActivity.class);
        intent.putExtra(ACCOUNT_ID_ARG, accountId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ViewUtils.whenNonNull(getSupportActionBar(), actionBar -> {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        });
        setTitle(R.string.xim_webview_title);

        // Pre-emptively set result to canceled, in case the user backs out without logging in.
        setResult(Activity.RESULT_CANCELED);

        String publicKey = getIntent().getStringExtra(ACCOUNT_ID_ARG);
        if (TextUtils.isEmpty(publicKey)) {
            if (savedInstanceState == null) {
                // We have to generate our own. This will be used downstream during import.
                KeyPair keyPair = KeyPair.random();
                accountSeed = String.valueOf(keyPair.getSecretSeed());
                publicKey = keyPair.getAccountId();
            } else {
                accountSeed = savedInstanceState.getString(ACCOUNT_SEED_KEY);
                KeyPair keyPair = KeyPair.fromSecretSeed(accountSeed);
                publicKey = keyPair.getAccountId();
            }
        }

        webView.setWebViewClient(new CustomClient());
        webView.loadUrl(EnvironmentConstants.EXPORT_ID_LOGIN_URL + publicKey);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(accountSeed, ACCOUNT_SEED_KEY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CustomClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return didCompleteLogin(url);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return didCompleteLogin(request.getUrl().toString());
        }

        private boolean didCompleteLogin(String url) {
            if (!TextUtils.isEmpty(url) && url.contains(LOGIN_SUCCESS_SIGNIFIER)) {
                Intent data = new Intent();
                data.putExtra(ACCOUNT_SEED_KEY, accountSeed);
                setResult(Activity.RESULT_OK, data);
                finish();
                return true;
            } else {
                return false;
            }
        }
    }
}
