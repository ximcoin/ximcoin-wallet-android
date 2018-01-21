package tech.duchess.luminawallet.view.account.transactions;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.model.persistence.transaction.Operation;

public class TransactionsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.asset_code)
    TextView assetCode;

    @BindView(R.id.asset_volume)
    TextView assetVolume;

    TransactionsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindData(@Nullable Operation operation) {
        assetVolume.setText(operation == null ? "" : operation.getId());
    }
}
