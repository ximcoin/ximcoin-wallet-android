package tech.duchess.luminawallet.view.account.transactions;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;

public class TransactionsAdapter extends PagedListAdapter<Operation, TransactionsViewHolder> {
    private static final DiffCallback<Operation> OPERATION_COMPARATOR = new DiffCallback<Operation>() {
        @Override
        public boolean areItemsTheSame(@NonNull Operation oldItem, @NonNull Operation newItem) {
            if (oldItem.getId() == null && newItem.getId() == null) {
                return true;
            } else if (oldItem.getId() == null) {
                return true;
            } else {
                return oldItem.getId().equals(newItem.getId());
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull Operation oldItem, @NonNull Operation newItem) {
            return oldItem == newItem;
        }
    };

    protected TransactionsAdapter() {
        super(OPERATION_COMPARATOR);
    }

    @Override
    public TransactionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.balance_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(TransactionsViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }
}
