package ci.ui.object.festival;

import com.chinaairlines.mobile30.R;

import ci.function.HomePage.item.CIHomeBgItem;

/**
 * Created by kevincheng on 2018/1/30.
 */

public class SNewYearFestival extends SFestival{
    /**聖誕新年期間*/
    @Override
    public String getStartDateTime() {
        return "2017-11-30 00:00";
    }

    @Override
    public String getEndDateTime() {
        return "2018-01-04 23:59";
    }

    @Override
    public int getLoadingResourceId() {
        return R.drawable.loading_newyear;
    }

    @Override
    public CIHomeBgItem getBackgroundImage() {
        return new CIHomeBgItem(R.drawable.wallpaper_left_newyear,
                                R.drawable.wallpaper_mid_newyear,
                                R.drawable.wallpaper_right_newyear,
                                R.drawable.wallpaper_mid_blur_newyear);
    }
}
