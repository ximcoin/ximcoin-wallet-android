package tech.duchess.luminawallet.view.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

import tech.duchess.luminawallet.model.persistence.account.Account;

public class AccountFragment extends RxFragment {
    private static final String ACCOUNT_KEY = "AccountFragment.ACCOUNT_KEY";
    private static final String ACCOUNT_PERSPECTIVE_KEY = "AccountFragment.ACCOUNT_PERSPECTIVE_KEY";

    private Account account;
    private AccountPerspective accountPerspective;

    public enum AccountPerspective {
        BALANCES,
        SEND,
        RECEIVE,
        TRANSACTIONS
    }

    public static AccountFragment newInstance(@NonNull Account account,
                                              @NonNull AccountPerspective accountPerspective) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ACCOUNT_KEY, account);
        arguments.putString(ACCOUNT_PERSPECTIVE_KEY, accountPerspective.name());
        AccountFragment accountFragment = new AccountFragment();
        accountFragment.setArguments(arguments);
        return accountFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        readBundle(savedInstanceState == null ? getArguments() : savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void readBundle(@Nullable Bundle bundle) {
        if (bundle != null) {
            account = bundle.getParcelable(ACCOUNT_KEY);
            accountPerspective =
                    AccountPerspective.valueOf(bundle.getString(ACCOUNT_PERSPECTIVE_KEY));
        }
    }

    public void changePerspective(@NonNull AccountPerspective accountPerspective) {
        this.accountPerspective = accountPerspective;
        updateUI();
    }

    public void updateAccount(@NonNull Account account) {
        this.account = account;
        updateUI();
    }

    private void updateUI() {
        Toast.makeText(getContext(), String.valueOf(account.getBalances().get(0).getBalance()),
                Toast.LENGTH_LONG).show();
    }
}
