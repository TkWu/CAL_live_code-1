package ci.ui.object.festival;

import com.chinaairlines.mobile30.R;

import ci.function.HomePage.item.CIHomeBgItem;

/**
 * Created by kevincheng on 2018/1/30.
 */

public class SMidAutumnFestival extends SFestival {
    /**中秋節*/
    @Override
    public String getStartDateTime() {
        return "2017-10-01 00:00";
    }

    @Override
    public String getEndDateTime() {
        return "2017-10-07 23:59";
    }

    @Override
    public int getLoadingResourceId() {
        return R.drawable.loading_moon;
    }

    @Override
    public CIHomeBgItem getBackgroundImage() {
        return new CIHomeBgItem(R.drawable.wallpaper_left_moon,
                                R.drawable.wallpaper_mid_moon,
                                R.drawable.wallpaper_right_moon,
                                R.drawable.wallpaper_mid_blur_moon);
    }
}
