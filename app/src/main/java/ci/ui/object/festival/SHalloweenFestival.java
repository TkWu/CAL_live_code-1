package ci.ui.object.festival;

import com.chinaairlines.mobile30.R;

import ci.function.HomePage.item.CIHomeBgItem;

/**
 * Created by kevincheng on 2018/1/30.
 */

public class SHalloweenFestival extends SFestival {
    /**萬聖節*/
    @Override
    public String getStartDateTime() {
        return "2017-10-28 00:00";
    }

    @Override
    public String getEndDateTime() {
        return "2017-11-03 23:59";
    }

    @Override
    public int getLoadingResourceId() {
        return R.drawable.loading_halloween;
    }

    @Override
    public CIHomeBgItem getBackgroundImage() {
        return new CIHomeBgItem(R.drawable.wallpaper_left_halloween,
                                R.drawable.wallpaper_mid_halloween,
                                R.drawable.wallpaper_right_halloween,
                                R.drawable.wallpaper_mid_blur_halloween);
    }
}
