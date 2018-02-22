package tech.duchess.luminawallet.view.contacts;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import tech.duchess.luminawallet.R;

class ContactUtil {
    private static int[] contactColors;

    @ColorInt
    static int getContactColor(@NonNull Context context, int colorIndex) {
        if (contactColors == null) {
            contactColors = context.getResources().getIntArray(R.array.contact_colors);
        }

        if (colorIndex > contactColors.length || colorIndex < 0) {
            return ContextCompat.getColor(context, R.color.contact_blue);
        } else {
            return contactColors[colorIndex];
        }
    }
}
