package tech.duchess.luminawallet.view.intro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.duchess.luminawallet.R;
import tech.duchess.luminawallet.view.account.AccountsActivity;

public class IntroActivity extends AppCompatActivity {
    private static final String INTRO_PREFS_HANDLE = "IntroActivity.INTRO_PREFS_HANDLE";
    private static final String HAS_VIEWED_INTRO_KEY = "IntroActivity.HAS_VIEWED_INTRO_KEY";

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.layoutDots)
    LinearLayout dotsLayout;

    @BindView(R.id.btn_previous)
    TextView btnPrevious;

    @BindView(R.id.btn_next)
    TextView btnNext;

    private int[] layouts;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(INTRO_PREFS_HANDLE, Context.MODE_PRIVATE);

        // TODO: This is bad and I should feel bad. But I don't.
        if (preferences.getBoolean(HAS_VIEWED_INTRO_KEY, false)) {
            launchHomeScreen();
            finish();
        }

        // Make notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.intro_activity);
        ButterKnife.bind(this);

        layouts = new int[]{
                R.layout.welcome_intro_slide,
                R.layout.secure_intro_slide,
                R.layout.simple_intro_slide,
                R.layout.open_source_intro_slide,
                R.layout.developer_intro_slide};

        addBottomDots(0);
        changeStatusBarColor();

        viewPager.setAdapter(new IntroPagerAdapter());
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Check button state in case of activity destroy/recreate.
        updateNavButtons(viewPager.getCurrentItem());
    }

    @OnClick(R.id.btn_previous)
    public void onPrevious() {
        int current = viewPager.getCurrentItem();
        if (current > 0) {
            viewPager.setCurrentItem(--current);
        }
    }

    @OnClick(R.id.btn_next)
    public void onNext() {
        int next = viewPager.getCurrentItem() + 1;
        if (next < layouts.length) {
            viewPager.setCurrentItem(next);
        } else {
            launchHomeScreen();
        }
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        //prefManager.setFirstTimeLaunch(false);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(HAS_VIEWED_INTRO_KEY, true);
        prefEditor.apply();
        startActivity(new Intent(this, AccountsActivity.class));
        finish();
    }

    private void updateNavButtons(int currentPosition) {
        addBottomDots(currentPosition);

        btnPrevious.setVisibility(currentPosition == 0 ? View.INVISIBLE : View.VISIBLE);

        if (currentPosition == layouts.length - 1) {
            btnNext.setText(getString(R.string.start));
        } else {
            btnNext.setText(getString(R.string.next));
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            updateNavButtons(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private class IntroPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
