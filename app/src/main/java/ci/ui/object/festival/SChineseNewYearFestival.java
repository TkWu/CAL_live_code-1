package ci.ui.object.festival;

import com.chinaairlines.mobile30.R;

import ci.function.HomePage.item.CIHomeBgItem;

/**
 * Created by kevincheng on 2018/1/30.
 */

public class SChineseNewYearFestival extends SFestival{
    /**中國新年*/
    @Override
    public String getStartDateTime() {
        return "2018-02-12 00:00";
    }

    @Override
    public String getEndDateTime() {
        return "2018-03-02 23:59";
    }

    @Override
    public int getLoadingResourceId() {
        return R.drawable.loading_chinese_new_year;
    }

    @Override
    public CIHomeBgItem getBackgroundImage() {
        return new CIHomeBgItem(R.drawable.wallpaper_left_chinese_new_year,
                                R.drawable.wallpaper_mid_chinese_new_year,
                                R.drawable.wallpaper_right_chinese_new_year,
                                R.drawable.wallpaper_mid_blur_chinese_new_year);
    }
}
