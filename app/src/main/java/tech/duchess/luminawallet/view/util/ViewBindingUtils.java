package tech.duchess.luminawallet.view.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Helper methods for view bindings.
 */
public final class ViewBindingUtils {

    private ViewBindingUtils() {
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
            viewGroup.setEnabled(false);
            for (int childIndex = 0; childIndex < viewGroup.getChildCount(); childIndex++) {
                whenNonNull(viewGroup.getChildAt(childIndex), tabView -> {
                    ViewGroup tabViewGroup = (ViewGroup) tabView;
                    tabViewGroup.getChildAt(0).setEnabled(tabsEnabled);
                    tabView.setEnabled(tabsEnabled);
                });
            }
        });
    }

    public static void openUrl(@NonNull String url, @NonNull Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Throwable t) {
            Timber.e(t, "Failed to open url: %s", url);
        }
    }

    private static ViewGroup getTabViewGroup(@NonNull TabLayout tabLayout) {
        ViewGroup viewGroup = null;

        if (tabLayout != null && tabLayout.getChildCount() > 0) {
            View view = tabLayout.getChildAt(0);
            if (view != null && view instanceof ViewGroup)
                viewGroup = (ViewGroup) view;
        }
        return viewGroup;
    }

}
