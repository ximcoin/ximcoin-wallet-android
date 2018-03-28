package com.ximcoin.ximwallet.view.createaccount;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ximcoin.ximwallet.R;
import com.ximcoin.ximwallet.model.fees.Fees;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.util.AssetUtil;
import com.ximcoin.ximwallet.model.util.FeesUtil;
import com.ximcoin.ximwallet.presenter.createaccount.AddAccountSourceContract;
import com.ximcoin.ximwallet.view.common.BaseViewFragment;
import com.ximcoin.ximwallet.view.util.ViewUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class AddAccountSourceFragment extends BaseViewFragment<AddAccountSourceContract.AddAccountSourcePresenter>
        implements AddAccountSourceContract.AddAccountSourceView {
    private static final String ACCOUNT_ARG = "AddAccountSourceFragment.ACCOUNT_ARG";
    private static final String IS_NEW_TO_LUMINA_ARG = "AddAccountSourceFragment.IS_NEW_TO_LUMINA_ARG";

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.fund_xim_message)
    TextView fundXimMessage;

    @BindView(R.id.no_account_container)
    ViewGroup noAccountContainer;

    @BindView(R.id.xim_trust_container)
    ViewGroup ximTrustContainer;

    /**
     * No longer used in the Xim wallet.
     */
    @Deprecated
    public static AddAccountSourceFragment getInstance(boolean isNewToLumina) {
        Bundle args = new Bundle();
        args.putBoolean(IS_NEW_TO_LUMINA_ARG, isNewToLumina);
        AddAccountSourceFragment sourceFragment = new AddAccountSourceFragment();
        sourceFragment.setArguments(args);
        return sourceFragment;
    }

    public static AddAccountSourceFragment getInstance(@Nullable Account account) {
        Bundle args = new Bundle();
        args.putParcelable(ACCOUNT_ARG, account);
        AddAccountSourceFragment sourceFragment = new AddAccountSourceFragment();
        sourceFragment.setArguments(args);
        return sourceFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_account_source_fragment, container, false);
    }


    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showLoadFailure() {
        noAccountContainer.setVisibility(View.GONE);
        ximTrustContainer.setVisibility(View.GONE);
        Toast.makeText(activityContext, R.string.load_accounts_failure, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNoAccount() {
        ximTrustContainer.setVisibility(View.GONE);
        noAccountContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAccountLacksXimTrust(@NonNull Fees fees, @NonNull Account account) {
        noAccountContainer.setVisibility(View.GONE);
        ximTrustContainer.setVisibility(View.VISIBLE);
        fundXimMessage.setText(getString(R.string.fund_xim_message, AssetUtil.getAssetAmountString(
                FeesUtil.getMinimumAccountBalance(fees, account) + 1)));
    }

    @OnClick(R.id.create_account_button)
    public void onUserRequestCreateAccount() {
        presenter.onUserRequestCreateAccount();
    }

    @OnClick(R.id.import_account_button)
    public void onUserRequestImportAccount() {
        presenter.onUserRequestImportAccount();
    }

    @OnClick(R.id.export_id_login_button)
    public void onUserRequestExportIdLogin() {
        presenter.onUserRequestExportIdLogin();
    }

    @OnClick(R.id.fund_xim_button)
    public void onUserRequestFundXim() {
        presenter.onUserRequestFundXim();
    }

    @Override
    public void startCreateAccountFlow(boolean isImportingSeed) {
        ViewUtils.whenNonNull(getActivity(), activity ->
                ((AccountSourceReceiver) activity).onUserRequestedAccountCreation(isImportingSeed));
    }

    @Nullable
    @Override
    public Account getAccount() {
        Bundle args = getArguments();
        return args == null ? null : args.getParcelable(ACCOUNT_ARG);
    }
}
