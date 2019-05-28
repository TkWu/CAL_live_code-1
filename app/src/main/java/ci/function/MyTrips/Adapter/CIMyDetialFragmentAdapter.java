package ci.function.MyTrips.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevincheng on 2016/3/2.
 */
public class CIMyDetialFragmentAdapter extends FragmentStatePagerAdapter {
    List<Fragment> m_listFragments = null;
    ArrayList<String> m_list        = null;
    public CIMyDetialFragmentAdapter(FragmentManager fm,
                                     List<Fragment> listFragments,
                                     ArrayList<String> list) {
        super(fm);
        m_listFragments = listFragments;
        m_list          = list;
    }

    @Override
    public Fragment getItem(int position) {
        return m_listFragments.get(position);
    }

    @Override
    public int getCount() {
        return m_listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return m_list.get(position);
    }
}
