package com.ximcoin.ximwallet.view.account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ximcoin.ximwallet.model.persistence.account.Account;

public interface AccountPerspectiveView {
   void setAccount(@Nullable Account account);
   void transactionPostedForAccount(@NonNull Account account);
}
