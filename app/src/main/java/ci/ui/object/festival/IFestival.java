package ci.ui.object.festival;

import java.util.Date;

import ci.function.HomePage.item.CIHomeBgItem;

/**
 * Created by kevincheng on 2018/1/30.
 */

public interface IFestival {
    boolean isBetweenDate();
    Date getDate(String date, String pattern);
    String getStartDateTime();
    String getEndDateTime();
    int getLoadingResourceId();
    CIHomeBgItem getBackgroundImage();
}
