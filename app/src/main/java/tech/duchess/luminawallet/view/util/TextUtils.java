package tech.duchess.luminawallet.view.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.Locale;

import timber.log.Timber;

public class TextUtils {

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static CharSequence getBulletedList(int bulletRadius,
                                               @Nullable Integer leadingMargin,
                                               @NonNull Context context,
                                               int... stringResources) {
        int count = stringResources.length;
        CharSequence[] items = new CharSequence[count];
        for (int i = 0; i < count; i++) {
            items[i] = context.getString(stringResources[i]);
        }
        return getBulletedList(bulletRadius, leadingMargin == null ? 35 : leadingMargin, items);
    }

    public static CharSequence getBulletedList(int bulletRadius,
                                               int leadingMargin,
                                               @NonNull CharSequence... lines) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int i = 0; i < lines.length; i++) {
            CharSequence line = lines[i] + (i < lines.length - 1 ? "\n\n" : "");
            Spannable spannable = new SpannableString(line);
            spannable.setSpan(new CustomBulletSpan(leadingMargin, 0, bulletRadius), 0,
                    spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sb.append(spannable);
        }
        return sb;
    }

    public static String parseDateTimeZuluDate(@Nullable String zuluDate,
                                               @NonNull FormatStyle formatStyle) {
        if (TextUtils.isEmpty(zuluDate)) {
            Timber.e("DateTime was empty!");
            return "Unknown";
        }

        return getDateTimeFromInstant(Instant.parse(zuluDate), formatStyle);
    }

    public static String parseDateTimeEpochSeconds(long millis, @NonNull FormatStyle formatStyle) {
        return getDateTimeFromInstant(Instant.ofEpochSecond(millis), formatStyle);
    }

    private static String getDateTimeFromInstant(@NonNull Instant instant,
                                          @NonNull FormatStyle formatStyle) {
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofLocalizedDateTime(formatStyle)
                        .withLocale(Locale.getDefault()));
    }
}
