package tech.duchess.luminawallet.view.nav;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.threeten.bp.format.FormatStyle;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tech.duchess.luminawallet.LuminaWalletApp;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.presenter.nav.NavHeaderContract;
import tech.duchess.luminawallet.presenter.nav.NavHeaderPresenter;
import tech.duchess.luminawallet.view.util.TextUtils;

public class NavHeaderView extends FrameLayout implements NavHeaderContract.NavHeaderView {
    @Inject
    NavHeaderPresenter presenter;

    @BindView(R.id.fiat_conversion_value)
    TextView conversionValue;

    @BindView(R.id.last_updated)
    TextView lastUpdated;

    @BindView(R.id.nav_progress_bar)
    ProgressBar progressBar;

    private Unbinder unbinder;

    public NavHeaderView(@NonNull Context context) {
        super(context);
        initView();
    }

    public NavHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NavHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.nav_header_view, this);
        LuminaWalletApp.getInstance()
                .getAppComponent()
                .plus(new NavHeaderViewModule(this))
                .inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        unbinder = ButterKnife.bind(this);
        presenter.onViewAttached();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        presenter.onViewDetached();
    }

    @OnClick(R.id.info)
    public void onUserRequestConversionInfo() {
        presenter.onUserRequestInfo();
    }

    @OnClick(R.id.refresh_conversion)
    public void onUserRefreshConversionRate() {
        presenter.onUserRefreshConversion();
    }

    @Override
    public void showConversionUnknown() {
        conversionValue.setText(R.string.fiat_conversion_unknown);
        conversionValue.setTextColor(ContextCompat.getColor(getContext(), R.color.warningColor));
        lastUpdated.setVisibility(INVISIBLE);
    }

    @Override
    public void showConversion(@NonNull String conversion,
                               @NonNull String currency,
                               long epochSeconds) {
        String suffix = getContext().getString(R.string.fiat_conversion_suffix,
                conversion, currency);
        conversionValue.setText(getContext().getString(R.string.fiat_conversion_prefix, suffix));
        conversionValue.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryText));
        lastUpdated.setVisibility(VISIBLE);
        String lastUpdatedValue = getContext().getString(R.string.fiat_conversion_last_updated,
                TextUtils.parseDateTimeEpochSeconds(epochSeconds, FormatStyle.SHORT));
        lastUpdated.setText(lastUpdatedValue);
    }

    @Override
    public void showInfo() {
        new AlertDialog.Builder(getContext(), R.style.DefaultAlertDialog)
                .setMessage(R.string.fiat_conversion_info)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? VISIBLE : GONE);
    }

    @Override
    public void showLoadError() {
        Toast.makeText(getContext(),
                R.string.fiat_conversion_load_failure,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        CustomViewSavedState savedState = new CustomViewSavedState(superState);
        savedState.presenterState = new Bundle();
        presenter.onSaveInstanceState(savedState.presenterState);
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        CustomViewSavedState savedState = (CustomViewSavedState) state;
        presenter.onViewStateRestored(savedState.presenterState);
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    private static class CustomViewSavedState extends BaseSavedState {
        Bundle presenterState;

        public static final Parcelable.Creator<CustomViewSavedState> CREATOR = new Creator<CustomViewSavedState>() {
            @Override public CustomViewSavedState createFromParcel(Parcel source) {
                return new CustomViewSavedState(source);
            }

            @Override public CustomViewSavedState[] newArray(int size) {
                return new CustomViewSavedState[size];
            }
        };

        CustomViewSavedState(Parcelable superState) {
            super(superState);
        }

        private CustomViewSavedState(Parcel source) {
            super(source);
            presenterState = source.readBundle(getClass().getClassLoader());
        }

        @Override public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(presenterState);
        }
    }

}
