package tech.duchess.luminawallet.view.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.api.HelpLinks;
import tech.duchess.luminawallet.view.common.BaseFragment;
import tech.duchess.luminawallet.view.util.ViewUtils;


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
}
