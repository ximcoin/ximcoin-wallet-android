package tech.duchess.luminawallet.presenter.account.send;

import android.support.annotation.NonNull;

import tech.duchess.luminawallet.presenter.common.BasePresenter;

public class SendPresenter extends BasePresenter<SendContract.SendView> implements SendContract.SendPresenter {
    protected SendPresenter(@NonNull SendContract.SendView view) {
        super(view);
    }
}
