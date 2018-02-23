package tech.duchess.luminawallet.view.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Action;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.util.ViewUtils;

public class ProgressOverlay extends FrameLayout {
    public static final int ANIMATION_DURATION_IN = 200;
    public static final int ANIMATION_DURATION_OUT = 2200;

    @BindView(R.id.determinate_image)
    ImageView determinateImage;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.loading_message)
    TextView loadingMessage;

    private Unbinder unbinder;

    public ProgressOverlay(@NonNull Context context) {
        super(context, null, R.attr.overlayStyle);
        initView();
    }

    public ProgressOverlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.attr.overlayStyle);
        initView();
    }

    public ProgressOverlay(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        inflate(getContext(), R.layout.progress_overlay, this);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbinder.unbind();
    }

    public void show(@Nullable String message) {
        determinateImage.setVisibility(INVISIBLE);
        progressBar.setVisibility(VISIBLE);
        loadingMessage.setText(message);
        ViewUtils.animateView(this, true, 1f, ANIMATION_DURATION_IN, null);
    }

    public void hide(@Nullable String message, boolean wasSuccess, boolean immediate) {
        hide(message, wasSuccess, immediate, null);
    }

    public void hide(@Nullable String message,
                     boolean wasSuccess,
                     boolean immediate,
                     @Nullable Action action) {
        if (immediate) {
            this.setVisibility(GONE);
            return;
        }

        loadingMessage.setText(message);
        progressBar.setVisibility(INVISIBLE);


        Context context = getContext();
        int drawable = wasSuccess ? R.drawable.ic_check_mark : R.drawable.ic_problem;
        ColorStateList tint = ColorStateList.valueOf(ContextCompat.getColor(context, wasSuccess
                ? R.color.transfer_in
                : R.color.warningColor));
        determinateImage.setImageDrawable(ContextCompat.getDrawable(context, drawable));
        ImageViewCompat.setImageTintList(determinateImage, tint);

        ViewUtils.animateView(determinateImage, true, 1f, ANIMATION_DURATION_IN, null);
        ViewUtils.animateView(this, false, 1f, ANIMATION_DURATION_OUT, action);
    }
}
