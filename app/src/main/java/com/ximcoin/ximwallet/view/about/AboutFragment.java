package com.ximcoin.ximwallet.view.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import com.ximcoin.ximwallet.EnvironmentConstants;
import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.api.HelpLinks;
import com.ximcoin.ximwallet.view.common.BaseFragment;
import com.ximcoin.ximwallet.view.util.ViewUtils;


public class AboutFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @OnClick(R.id.privacy_policy_link)
    public void onUserRequestPrivacyPolicy() {
        ViewUtils.openUrl(HelpLinks.PRIVACY_POLICY, activityContext);
    }

    @OnClick(R.id.source_code_link)
    public void onUserRequestSourceCode() {
        ViewUtils.openUrl(HelpLinks.SOURCE_CODE, activityContext);
    }

    @OnClick(R.id.contact_link)
    public void onUserRequestContact() {
        ViewUtils.sendEmail(activityContext,
                EnvironmentConstants.CONTACT_EMAIL,
                getString(R.string.contact_subject),
                getString(R.string.contact_body),
                getString(R.string.contact_chooser_title));
    }
}
