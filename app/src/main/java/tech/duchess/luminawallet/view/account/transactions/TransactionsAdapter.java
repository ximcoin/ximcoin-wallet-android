package tech.duchess.luminawallet.view.account.transactions;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;

public class TransactionsAdapter extends PagedListAdapter<Operation, RecyclerView.ViewHolder> {
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

    @Nullable
    private String accountId;
    private boolean isLoading;

    protected TransactionsAdapter() {
        super(OPERATION_COMPARATOR);
    }

    public void setAccountId(@Nullable String accountId) {
        this.accountId = accountId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.loading_recycler_item) {
            return new LoadingViewHolder(itemView);
        } else {
            return new TransactionsViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isLoading && position == getItemCount() - 1 ? R.layout.loading_recycler_item
                : R.layout.transaction_recycler_item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == R.layout.transaction_recycler_item) {
            ((TransactionsViewHolder) holder).bindData(getItem(position), accountId);
        }
    }

    public void setLoading(boolean isLoading) {
        boolean wasLoadingPrior = this.isLoading;
        this.isLoading = isLoading;
        if (isLoading != wasLoadingPrior) {
            if (isLoading) {
                notifyItemInserted(super.getItemCount());
            } else {
                notifyItemRemoved(super.getItemCount());
            }
        }
    }
}
