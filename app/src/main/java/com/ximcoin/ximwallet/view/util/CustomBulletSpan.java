package com.ximcoin.ximwallet.view.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.*;
import android.text.style.LeadingMarginSpan;

public class CustomBulletSpan implements LeadingMarginSpan {
    private final int gapWidth;
    private final boolean wantColor;
    private final int color;
    private final int bulletRadius;

    private static Path BulletPath = null;
    private static final int STANDARD_BULLET_RADIUS = 3;
    public static final int STANDARD_GAP_WIDTH = 2;

    public CustomBulletSpan() {
        gapWidth = STANDARD_GAP_WIDTH;
        wantColor = false;
        color = 0;
        bulletRadius = STANDARD_BULLET_RADIUS;
    }

    public CustomBulletSpan(int gapWidth) {
        this.gapWidth = gapWidth;
        wantColor = false;
        color = 0;
        bulletRadius = STANDARD_BULLET_RADIUS;
    }

    public CustomBulletSpan(int gapWidth, int color) {
        this.gapWidth = gapWidth;
        this.color = color;
        wantColor = color != 0;
        bulletRadius = STANDARD_BULLET_RADIUS;
    }

    public CustomBulletSpan(int gapWidth, int color, int bulletRadius) {
        this.gapWidth = gapWidth;
        this.color = color;
        wantColor = color != 0;
        this.bulletRadius = bulletRadius;
    }

    public int getLeadingMargin(boolean first) {
        return 2 * bulletRadius + gapWidth;
    }

    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = p.getStyle();
            int oldcolor = 0;

            if (wantColor) {
                oldcolor = p.getColor();
                p.setColor(color);
            }

            p.setStyle(Paint.Style.FILL);

            if (c.isHardwareAccelerated()) {
                if (BulletPath == null) {
                    BulletPath = new Path();
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    BulletPath.addCircle(0.0f, 0.0f, 1.2f + bulletRadius, Path.Direction.CW);
                }

                c.save();
                c.translate(x + dir + bulletRadius, (top + bottom) / 2.0f);
                c.drawPath(BulletPath, p);
                c.restore();
            } else {
                c.drawCircle(x + dir + bulletRadius, (top + bottom) / 2.0f, bulletRadius, p);
            }

            if (wantColor) {
                p.setColor(oldcolor);
            }

            p.setStyle(style);
        }
    }
}
