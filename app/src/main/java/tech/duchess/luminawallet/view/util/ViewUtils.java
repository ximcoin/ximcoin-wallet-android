package tech.duchess.luminawallet.view.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import io.reactivex.functions.Consumer;
import tech.duchess.luminawallet.R;
import timber.log.Timber;

/**
 * Helper methods for view bindings.
 */
public final class ViewUtils {

    private ViewUtils() {
    }

    /**
     * Helper method to execute method invocations on an object, if and only if it is not null.
     *
     * @param object   The object to be checked which can be null.
     * @param consumer The consumer to call if the item is not null.
     * @param <T>      The item type.
     */
    public static <T> void whenNonNull(@Nullable T object,
                                       @NonNull Consumer<T> consumer) {
        if (object != null) {
            try {
                consumer.accept(object);
            } catch (Exception e) {
                Timber.e(e, "Failed method invocation non-null object");
            }
        }
    }

    public static void setTabsEnabled(@NonNull TabLayout tabLayout, boolean tabsEnabled) {
        whenNonNull(getTabViewGroup(tabLayout), viewGroup -> {
            for (int childIndex = 0; childIndex < viewGroup.getChildCount(); childIndex++) {
                whenNonNull(viewGroup.getChildAt(childIndex), tabView -> {
                    ((ViewGroup) tabView).getChildAt(0).setEnabled(tabsEnabled);
                    tabView.setEnabled(tabsEnabled);
                });
            }
        });
    }

    public static void setTabEnabled(@NonNull TabLayout tabLayout,
                                     boolean tabEnabled,
                                     int tabIndex) {
        whenNonNull(getTabViewGroup(tabLayout), viewGroup ->
                whenNonNull(viewGroup.getChildAt(tabIndex), tabView -> {
                    ((ViewGroup) tabView).getChildAt(0).setEnabled(tabEnabled);
                    tabView.setEnabled(tabEnabled);
                }));
    }

    private static ViewGroup getTabViewGroup(@NonNull TabLayout tabLayout) {
        ViewGroup viewGroup = null;

        if (tabLayout.getChildCount() > 0) {
            View view = tabLayout.getChildAt(0);
            if (view != null && view instanceof ViewGroup)
                viewGroup = (ViewGroup) view;
        }
        return viewGroup;
    }

    public static void openUrl(@NonNull String url, @NonNull Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Throwable t) {
            Timber.e(t, "Failed to open url: %s", url);
        }
    }

    /**
     * Copies the provided string to the clipboard.
     * @param context Android context.
     * @return {@code True} if we successfully copied to clipboard; otherwise {@code false}.
     */
    public static boolean copyToClipboard(@NonNull Context context,
                                          @NonNull String label,
                                          @NonNull String value) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager == null) {
            // Don't actually know under what circumstances this may occur. But best to let the user
            // know that the value they expected on the clipboard is not there.
            return false;
        }

        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, value));
        return true;
    }

    public static void addDividerDecoration(@NonNull RecyclerView recyclerView,
                                            @NonNull Context context,
                                            int layoutManagerOrientation) {
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(context, layoutManagerOrientation);
        ViewUtils.whenNonNull(ContextCompat.getDrawable(context, R.drawable.divider),
                dividerItemDecoration::setDrawable);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public static Bitmap encodeAsBitmap(String str) {
        BitMatrix result;
        Bitmap bitmap = null;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 500, 500, null);

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        } catch (Exception iae) {
            iae.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
