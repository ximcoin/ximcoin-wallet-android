package tech.duchess.luminawallet.view.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import tech.duchess.luminawallet.model.persistence.account.Account;

public interface AccountPerspectiveView {
   void setAccount(@Nullable Account account);
   void transactionPostedForAccount(@NonNull Account account);
}
