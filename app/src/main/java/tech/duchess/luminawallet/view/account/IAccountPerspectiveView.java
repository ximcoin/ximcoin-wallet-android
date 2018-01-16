package tech.duchess.luminawallet.view.account;

import android.support.annotation.Nullable;

import tech.duchess.luminawallet.model.persistence.account.Account;

public interface IAccountPerspectiveView {
   void setAccount(@Nullable Account account);
}
