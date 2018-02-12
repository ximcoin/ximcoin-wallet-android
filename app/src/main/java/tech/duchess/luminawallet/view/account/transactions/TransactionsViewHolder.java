package tech.duchess.luminawallet.view.account.transactions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.threeten.bp.format.FormatStyle;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.view.util.TextUtils;
import tech.duchess.luminawallet.view.util.ViewUtils;
import timber.log.Timber;

class TransactionsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.amount_transferred)
    TextView transferAmount;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.operation_type)
    TextView operationType;

    @BindView(R.id.memo_label)
    TextView memoLabel;

    @BindView(R.id.memo)
    TextView memo;

    @BindView(R.id.sender_label)
    TextView sender;

    @BindView(R.id.recipient_label)
    TextView recipient;

    @BindView(R.id.address)
    TextView address;

    TransactionsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindData(@Nullable Operation operation, @Nullable String viewingAccount) {
        Context context = itemView.getContext();
        if (viewingAccount == null) {
            Timber.e("Viewing account was null");
            viewingAccount = "";
        }
        OperationAdapter operationAdapter =
                new OperationAdapter(operation, viewingAccount, context);

        if (TextUtils.isEmpty(operationAdapter.memo)) {
            memoLabel.setVisibility(View.GONE);
            memo.setVisibility(View.GONE);
        } else {
            memoLabel.setVisibility(View.VISIBLE);
            memo.setVisibility(View.VISIBLE);
            memo.setText(operationAdapter.memo);
        }

        date.setText(operationAdapter.date);

        operationType.setText(context.getString(operationAdapter.operationType));


        int amountColorRes;
        int amountPrefixRes;
        if (operationAdapter.isTransferIn) {
            recipient.setVisibility(View.GONE);
            sender.setVisibility(View.VISIBLE);
            amountColorRes = R.color.transfer_in;
            amountPrefixRes = R.string.receive_amount_prefix;
        } else {
            recipient.setVisibility(View.VISIBLE);
            sender.setVisibility(View.GONE);
            amountColorRes = R.color.transfer_out;
            amountPrefixRes = R.string.send_amount_prefix;
        }

        transferAmount.setTextColor(ContextCompat.getColor(context, amountColorRes));
        transferAmount.setText(context.getString(amountPrefixRes, operationAdapter.amount, operationAdapter.assetCode));
        address.setText(operationAdapter.address);
    }

    // TODO: Should probably move this data abstraction into the presenter itself.
    private static class OperationAdapter {
        @NonNull
        String date = "";

        @NonNull
        String address = "";

        @NonNull
        String amount = "";

        @NonNull
        String assetCode = "";

        @NonNull
        String memo = "";

        @StringRes
        int operationType = Operation.OperationType.UNKNOWN.getFriendlyName();

        boolean isTransferIn = false;

        OperationAdapter(@Nullable Operation operation,
                         @NonNull String viewingAccount,
                         @NonNull Context context) {
            if (operation == null) {
                return;
            }

            ViewUtils.whenNonNull(operation.getTransaction(), transaction ->
                    ViewUtils.whenNonNull(transaction.getMemo(), memo -> {
                        if (!TextUtils.isEmpty(memo)) {
                            this.memo = memo;
                        }
                    }));

            operationType = operation.getOperationType().getFriendlyName();
            assetCode = operation.getAsset_code();
            date = TextUtils.parseDateTimeZuluDate(operation.getCreated_at(), FormatStyle.MEDIUM);

            // TODO: Fill the rest of these in.
            switch (operation.getOperationType()) {
                case CREATE_ACCOUNT:
                    assetCode = AssetUtil.LUMEN_ASSET_CODE;
                    if (viewingAccount.equals(operation.getFunder())) {
                        isTransferIn = false;
                        address = operation.getAccount();
                    } else {
                        isTransferIn = true;
                        address = operation.getFunder();
                    }
                    amount = String.valueOf(operation.getStarting_balance());
                    break;
                case PAYMENT:
                    if (viewingAccount.equals(operation.getFrom())) {
                        isTransferIn = false;
                        address = operation.getTo();
                    } else {
                        isTransferIn = true;
                        address = operation.getFrom();
                    }
                    amount = String.valueOf(operation.getAmount());
                    break;
                case PATH_PAYMENT:
                    break;
                case MANAGE_OFFER:
                    break;
                case CREATE_PASSIVE_OFFER:
                    break;
                case SET_OPTIONS:
                    break;
                case CHANGE_TRUST:
                    break;
                case ALLOW_TRUST:
                    break;
                case ACCOUNT_MERGE:
                    break;
                case INFLATION:
                    break;
                case MANAGE_DATA:
                    break;
                case UNKNOWN:
                    Timber.e("Operation had no associated type");
                    break;
                default:
                    Timber.e("Unhandled operation type: %s",
                            operation.getOperationType().name());
                    break;
            }
        }
    }
}
