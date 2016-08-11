package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by phyo.pwint on 26/7/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    KeyFragment tab1 = new KeyFragment();
                    return tab1;
                case 1:
                    RoomFragment tab3 = new RoomFragment();
                    return tab3;
                case 2:
                    ManageKeyFragment tab2 = new ManageKeyFragment();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

