package tech.duchess.luminawallet.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import io.reactivex.functions.Consumer;

/**
 * Helper methods for view bindings.
 */
public final class ViewBindingUtils {
    private static final String TAG = ViewBindingUtils.class.getSimpleName();

    private ViewBindingUtils() {
    }

    /**
     * Helper method to execute method invocations on an object, if and only if it is not null.
     *
     * @param object The object to be checked which can be null.
     * @param consumer The consumer to call if the item is not null.
     * @param <T>    The item type.
     */
    public static <T> void whenNonNull(@Nullable T object,
                                       @NonNull Consumer<T> consumer) {
        if (object != null) {
            try {
                consumer.accept(object);
            } catch (Exception e) {
                Log.e(TAG, "Failed method invocation non-null object", e);
            }
        }
    }
}
