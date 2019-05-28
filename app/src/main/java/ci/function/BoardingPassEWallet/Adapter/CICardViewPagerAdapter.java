package ci.function.BoardingPassEWallet.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 登機證/其他服務/優惠券卡 ViewPager用的Adapter
 * Created by jlchen on 2016/3/24.
 */
public class CICardViewPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> m_listFragments = null;

    public CICardViewPagerAdapter(FragmentManager fm,
                                  List<Fragment> listFragments) {
        super(fm);
        m_listFragments = listFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return m_listFragments.get(position);
    }

    @Override
    public int getCount() {
        return m_listFragments.size();
    }
}
