package fr.coding.yourandroidwebapp.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import fr.coding.webappsettingsviews.WebAppSettingsAdvancedFragment;
import fr.coding.webappsettingsviews.WebAppSettingsGeneralFragment;
import fr.coding.yourandroidwebapp.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    WebAppSettingsAdvancedFragment advancedFragment = null;
    WebAppSettingsGeneralFragment settingsGeneralFragment = null;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.webapp_general, R.string.pref_header_advancedsettings};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fr = null;
        switch (position) {
            case 1:
                if (advancedFragment == null) {
                    advancedFragment = WebAppSettingsAdvancedFragment.newInstance();
                }
                fr = advancedFragment;
                break;
            default:
                if (settingsGeneralFragment == null) {
                    settingsGeneralFragment = WebAppSettingsGeneralFragment.newInstance();
                }
                fr = settingsGeneralFragment;
        }

        return fr;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}