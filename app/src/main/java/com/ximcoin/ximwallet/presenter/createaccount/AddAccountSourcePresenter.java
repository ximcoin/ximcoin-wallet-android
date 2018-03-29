package com.ximcoin.ximwallet.presenter.createaccount;

import android.support.annotation.NonNull;

import com.ximcoin.ximwallet.dagger.SchedulerProvider;
import com.ximcoin.ximwallet.model.persistence.account.Account;
import com.ximcoin.ximwallet.model.repository.FeesRepository;
import com.ximcoin.ximwallet.presenter.common.BasePresenter;

import timber.log.Timber;

public class AddAccountSourcePresenter extends BasePresenter<AddAccountSourceContract.AddAccountSourceView>
        implements AddAccountSourceContract.AddAccountSourcePresenter {
    @NonNull
    private final FeesRepository feesRepository;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    protected AddAccountSourcePresenter(@NonNull AddAccountSourceContract.AddAccountSourceView view,
                                        @NonNull FeesRepository feesRepository,
                                        @NonNull SchedulerProvider schedulerProvider) {
        super(view);
        this.feesRepository = feesRepository;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public void resume() {
        super.resume();
        updateView();
    }

    private void updateView() {
        Account account = view.getAccount();
        if (account == null) {
            view.showNoAccount();
        } else {
            feesRepository.getFees()
                    .compose(schedulerProvider.singleScheduler())
                    .doOnSubscribe(disposable -> {
                        addDisposable(disposable);
                        view.showLoading(true);
                    })
                    .doAfterTerminate(() -> view.showLoading(false))
                    .subscribe(fees ->
                                    view.showAccountLacksXimTrust(fees, account),
                            throwable -> {
                                Timber.e(throwable, "Failed to load fees for add account screen");
                                view.showLoadFailure();
                            });
        }
    }

    @Override
    public void onUserRequestCreateAccount() {
        view.startCreateAccountFlow(false);
    }

    @Override
    public void onUserRequestImportAccount() {
        view.startCreateAccountFlow(true);
    }

    @Override
    public void onUserRequestExportIdLogin() {
        view.navigateToExportIDLogin();
    }

    @Override
    public void onUserRequestFundXim() {
        view.navigateToExportIDLogin();
    }
}
