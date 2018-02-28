package tech.duchess.luminawallet.view.account.transactions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import timber.log.Timber;

class TransactionsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.headline)
    TextView headline;

    @BindView(R.id.row_container)
    LinearLayout rowContainer;

    TransactionsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindData(@Nullable Operation operation, @Nullable String viewingAccount) {
        if (operation == null) {
            Timber.e("Operation was null");
            setInvisible();
            return;
        }

        if (viewingAccount == null) {
            Timber.e("Viewing account was null");
            setInvisible();
            return;
        }

        Context context = itemView.getContext();

        TransactionViewModel transactionViewModel = TransactionViewModelFactory.
                getViewModel(operation, viewingAccount, context);

        headline.setText(transactionViewModel.getHeadline());
        headline.setTextColor(ContextCompat.getColor(context,
                transactionViewModel.getHeadlineColorRes()));

        List<TransactionViewModel.Row> rows = transactionViewModel.getRowList();
        populateRowCount(rows.size(), context);

        for (int i = 0; i < rows.size(); i++) {
            TransactionViewModel.Row row = rows.get(i);
            LinearLayout rowView = (LinearLayout) rowContainer.getChildAt(i);
            TextView label = (TextView) rowView.getChildAt(0);
            TextView value = (TextView) rowView.getChildAt(1);
            label.setText(row.getLabel());
            value.setText(row.getValue());

            if (row.containsAddress()) {
                TextViewCompat.setTextAppearance(value, R.style.AddressText);
            } else {
                TextViewCompat.setTextAppearance(value, R.style.TransactionRowDefault);
            }
        }
    }

    private void setInvisible() {
        headline.setVisibility(View.GONE);
        rowContainer.setVisibility(View.GONE);
    }

    private void populateRowCount(int rowCount, @NonNull Context context) {
        int curChildCount = rowContainer.getChildCount();
        if (rowCount == curChildCount) {
            return;
        }

        if (rowCount == 0) {
            rowContainer.removeAllViews();
            return;
        }

        boolean isRemovingRows = rowCount < curChildCount;

        if (isRemovingRows) {
            rowContainer.removeViews(0, curChildCount - rowCount);
        } else {
            for (int i = 0; i < rowCount - curChildCount; i++) {
                LayoutInflater inflater = LayoutInflater.from(context);
                inflater.inflate(R.layout.transaction_row, rowContainer);
            }
        }
    }
}
