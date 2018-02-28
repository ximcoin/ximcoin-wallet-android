package tech.duchess.luminawallet.view.account.transactions;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.threeten.bp.format.FormatStyle;

import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;
import tech.duchess.luminawallet.model.persistence.transaction.Transaction;
import tech.duchess.luminawallet.model.util.AssetUtil;
import tech.duchess.luminawallet.view.util.TextUtils;

class TransactionViewModelFactory {

    static TransactionViewModel getViewModel(@NonNull Operation operation,
                                                    @NonNull String viewingAccount,
                                                    @NonNull Context context) {
        String memo = null;
        Transaction transaction = operation.getTransaction();
        if (transaction != null) {
            memo = transaction.getMemo();
        }

        String operationName = context.getString(operation.getOperationType().getFriendlyName());
        String date = TextUtils.parseDateTimeZuluDate(operation.getCreated_at(), FormatStyle.MEDIUM);

        TransactionViewModel.TransactionViewModelBuilder builder =
                new TransactionViewModel.TransactionViewModelBuilder();

        switch(operation.getOperationType()) {
            case CREATE_ACCOUNT: {
                boolean isTransferIn = !viewingAccount.equals(operation.getFunder());
                builder
                        .withHeadline(getAmountText(
                                isTransferIn,
                                operation.getStarting_balance(),
                                AssetUtil.LUMEN_ASSET_CODE,
                                context))
                        .withHeadlineColor(getAmountColor(isTransferIn))
                        .withRow(R.string.date_label, date)
                        .withRow(R.string.operation_type_label, operationName)
                        .withAddress(getAddressLabel(isTransferIn), isTransferIn ?
                                operation.getFunder()
                                : operation.getAccount());
                break;
            }
            case PAYMENT: {
                boolean isTransferIn = !viewingAccount.equals(operation.getFrom());
                builder
                        .withHeadline(getAmountText(isTransferIn, operation.getAmount(),
                                operation.getAsset_code(), context))
                        .withHeadlineColor(getAmountColor(isTransferIn))
                        .withRow(R.string.date_label, date)
                        .withRow(R.string.operation_type_label, operationName)
                        .withAddress(getAddressLabel(isTransferIn), isTransferIn ?
                                operation.getFrom()
                                : operation.getTo());
                break;
            }
            case PATH_PAYMENT: {
                break;
            }
            case MANAGE_OFFER: {
                break;
            }
            case CREATE_PASSIVE_OFFER: {
                break;
            }
            case SET_OPTIONS: {
                builder
                        .withHeadline(operationName)
                        .withRow(R.string.date_label, date);
                break;
            }
            case CHANGE_TRUST: {
                break;
            }
            case ALLOW_TRUST: {
                break;
            }
            case ACCOUNT_MERGE: {
                break;
            }
            case INFLATION: {
                break;
            }
            case MANAGE_DATA: {
                break;
            }
            case UNKNOWN: {
                break;
            }
        }

        if (!TextUtils.isEmpty(memo)) {
            builder.withRow(R.string.memo_label, memo);
        }

        return builder.build();
    }

    private static String getAmountText(boolean isTransferIn,
                                        double amount,
                                        @NonNull String assetCode,
                                        @NonNull Context context) {
        int prefixRes = isTransferIn ? R.string.receive_amount_prefix
                : R.string.send_amount_prefix;
        return context.getString(prefixRes, String.valueOf(amount), assetCode);
    }

    @ColorRes
    private static int getAmountColor(boolean isTransferIn) {
        return isTransferIn ? R.color.transfer_in : R.color.transfer_out;
    }

    @StringRes
    private static int getAddressLabel(boolean isTransferIn) {
        return isTransferIn ? R.string.sender_label : R.string.recipient_label;
    }
}
