package ci.function.HomePage;

import android.content.SharedPreferences;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.function.Core.EncryptValueManager;
import ci.function.Core.SLog;
import ci.function.HomePage.item.CIHomeBgItem;
import ci.ui.object.AppInfo;
import ci.ui.object.festival.IFestival;
import ci.ui.object.festival.SFestival;

/**
 * Created by Ryan on 16/5/2.
 */
public class CIHomePageBgManager {

    private static final String SHARE_HOMEPAGE = "CIHomePageBg";

    private static CIHomePageBgManager  m_Instance      = null;

    private ArrayList<CIHomeBgItem>     arHomeBgList    = null;

    public static CIHomePageBgManager getInstance(){
        if ( null == m_Instance ){
            m_Instance = new CIHomePageBgManager();
            m_Instance.initialBackground();
        }
        return m_Instance;
    }


    public void initialBackground(){
        AppInfo appInfo = new AppInfo();
        //2016-09-19 調整為「101、梅花、九份、蘭嶼、桃園」共五組
        //設定背景圖, 每組背景圖都有左中右, 加上下半部Blur 四張圖
        arHomeBgList = new ArrayList<>();

        ArrayList<IFestival> arrayList = SFestival.getAll();
        for(IFestival festival:arrayList) {
            if(true == festival.isBetweenDate()) {
                arHomeBgList.add(festival.getBackgroundImage());
                return;
            }
        }

        arHomeBgList.add( new CIHomeBgItem(R.drawable.wallpaper_001_left, R.drawable.wallpaper_001_mid, R.drawable.wallpaper_001_right, R.drawable.wallpaper_001_mid_blur));
        arHomeBgList.add( new CIHomeBgItem(R.drawable.wallpaper_002_left, R.drawable.wallpaper_002_mid, R.drawable.wallpaper_002_right, R.drawable.wallpaper_002_mid_blur));
        arHomeBgList.add( new CIHomeBgItem(R.drawable.wallpaper_003_left, R.drawable.wallpaper_003_mid, R.drawable.wallpaper_003_right, R.drawable.wallpaper_003_mid_blur));
        arHomeBgList.add( new CIHomeBgItem(R.drawable.wallpaper_004_left, R.drawable.wallpaper_004_mid, R.drawable.wallpaper_004_right, R.drawable.wallpaper_004_mid_blur));
        arHomeBgList.add(new CIHomeBgItem(R.drawable.wallpaper_005_left, R.drawable.wallpaper_005_mid, R.drawable.wallpaper_005_right, R.drawable.wallpaper_005_mid_blur));
        //arHomeBgList.add( new CIHomeBgItem(R.drawable.wallpaper_006_left, R.drawable.wallpaper_006_mid, R.drawable.wallpaper_006_right, R.drawable.wallpaper_006_mid_blur));
        //arHomeBgList.add( new CIHomeBgItem(R.drawable.wallpaper_007_left, R.drawable.wallpaper_007_mid, R.drawable.wallpaper_007_right, R.drawable.wallpaper_007_mid_blur));
    }

    private void setBgSelect( int iSelect ){
        SharedPreferences sp = AppInfo.getInstance(CIApplication.getContext()).getCommonSharedPreferences();
        EncryptValueManager.setValue(sp, SHARE_HOMEPAGE, iSelect);
    }

    private int getBgSelect(){
        SharedPreferences sp = AppInfo.getInstance(CIApplication.getContext()).getCommonSharedPreferences();
        return EncryptValueManager.getInt(sp, SHARE_HOMEPAGE, 0);
    }

    public CIHomeBgItem getBackground(){
        int iSelectIdx = getBgSelect();
        if ( iSelectIdx >= arHomeBgList.size() ){
            iSelectIdx = 0;
        }
        return arHomeBgList.get(iSelectIdx);
    }

    /**取亂數*/
    public void RandomBackgroundImage(){

        int iSize = arHomeBgList.size();
        int iSelect = 0;
        for ( int iIdx=0; iIdx < iSize; iIdx++ ){
           SLog.d("CAL", "Math.random");
            iSelect = (int)(Math.random()*iSize);
            if ( iSelect != getBgSelect() ){
                break;
            }
        }
        setBgSelect(iSelect);
    }
}
