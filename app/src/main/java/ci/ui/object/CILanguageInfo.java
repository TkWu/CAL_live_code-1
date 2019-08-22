package ci.ui.object;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ui.object.item.LocaleItem;

/**
 * Created by Ryan on 16/2/18.
 */
public class CILanguageInfo {

    private String   LANGUAGE_TAG = "App_Language";

    public static enum eLANGUAGE{
        TAIWAN,
        CHINA,
        ENGLISH,
        JAPAN
    }

    public static Locale[] LANGUAGE = new Locale[]{ Locale.TAIWAN, Locale.CHINA, Locale.ENGLISH, Locale.JAPAN};
    public int[]  LANGUAGE_NAME     = new int[]{ R.string.language_tw, R.string.language_cn, R.string.language_en, R.string.language_jp};

    /* 德文
    public static Locale[] LANGUAGE = new Locale[]{ Locale.TAIWAN, Locale.CHINA, Locale.ENGLISH, Locale.JAPAN, Locale.GERMAN };
    public int[]  LANGUAGE_NAME     = new int[]{ R.string.language_tw, R.string.language_cn, R.string.language_en, R.string.language_jp, R.string.language_ge };
    */

    private ArrayList<LocaleItem> m_arLanguageList = new ArrayList<>();

    //整合進 CIApplication by ryan 2016/04/12
    //private static CILanguageInfo m_instance = null;
    //private static Context m_context = null;
    private Context m_context = null;

    public CILanguageInfo(Context context){
        initial(context);
    }

    //整合進 CIApplication by ryan 2016/04/12
//    public static CILanguageInfo getInstance(Context context)
//    {
//        if ( null == m_instance ){
//            m_instance = new CILanguageInfo();
//            m_instance.initial(context);
//        }
//        m_context = context;
//        return m_instance;
//    }

    private void initial( Context context ){

        m_arLanguageList.clear();

        int ilength = LANGUAGE.length;
        for ( int iIdx = 0; iIdx < ilength; iIdx++ ){

            LocaleItem localeItem = new LocaleItem();
            localeItem.locale = LANGUAGE[iIdx];
            localeItem.strTag = localeItem.locale.toString();
            localeItem.strDisplayName = context.getResources().getString(LANGUAGE_NAME[iIdx]);
            m_arLanguageList.add(localeItem);
        }
    }

    public ArrayList<LocaleItem> getLanguageList(){
        return m_arLanguageList;
    }

    /**初始話App語系, 先抓取手機記憶體儲存的語系, 若無語系預設為繁體中文*/
    public void initialAppLanguage(){
        Locale locale = getLanguage_Locale();
        setLanguage( locale );
    }

    /**取得目前記憶體內的語系*/
    public Locale getLanguage_Locale(){

        Locale locale = null;

        String strLan = LoadLanguage();
        for ( LocaleItem lanItem : m_arLanguageList ){
            if ( strLan.equals( lanItem.strTag ) ){

                locale = lanItem.locale;
                break;
            }
        }

        return locale;
    }

    /**取得目前記憶體內的語系的資料結構*/
    public LocaleItem getLanguage(){

        LocaleItem localeItem = null;

        String strLan = LoadLanguage();
        for ( LocaleItem lanItem : m_arLanguageList ){
            if ( strLan.equals( lanItem.strTag ) ){

                localeItem = lanItem;
                break;
            }
        }

        return localeItem;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Context attachBaseContext(Context context) {
            return updateResources(context);
    }


    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = getLanguage_Locale();
        Locale.setDefault(locale);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(new LocaleList(locale));
        }

        return context.createConfigurationContext(configuration);
    }

    public void setLanguage( Locale locale ){
        SaveLanguage(locale.toString());
    }

    /**
     * 儲存語系
     * @param  strTag 請給如下的格式, ex zh_TW, "en", "en_US"*/
    public void SaveLanguage( String strTag ){

        SharedPreferences.Editor edit = AppInfo.getInstance(CIApplication.getContext()).getCommonSharedPreferences().edit();
        edit.putString(LANGUAGE_TAG, strTag);
        edit.commit();
    }

    /**
     * 取得目前語系
     * @return String App的語系, ex zh_TW, "en", "en_US"*/
    public String LoadLanguage(){

        String strAppLanguage = "";
        //2016-12-07 Modify by Ryan
        //先檢查App是否已經設定語系，如果未設定則檢查手機語系是否符合App支援的語系, 如果不符合預設英文    //2017-04-05 modifly 調整為預設英文
        String strMemory = AppInfo.getInstance(CIApplication.getContext()).getCommonSharedPreferences().getString(LANGUAGE_TAG, "");
        if ( TextUtils.isEmpty(strMemory) ){
            String strSystem = Locale.getDefault().getLanguage();
            String strCountryCode = Locale.getDefault().getCountry();
           SLog.d("CAL", "CAL LoadLanguageLoadLanguage System= " + strSystem);
            if ( Locale.JAPAN.getLanguage().equals(strSystem) ){
                strAppLanguage = Locale.JAPAN.toString();

            } else if ( Locale.CHINA.getLanguage().equals(strSystem) &&
                        Locale.CHINA.getCountry().equals(strCountryCode)    ){
                strAppLanguage = Locale.CHINA.toString();

            } else if ( Locale.TAIWAN.getLanguage().equals(strSystem) ){
                strAppLanguage = Locale.TAIWAN.toString();

            }
            /* 德文 */
//            else if ( Locale.GERMAN.getLanguage().equals(strSystem) ){
//                strAppLanguage = Locale.GERMAN.toString();
//
//            }
            else {
                strAppLanguage = Locale.ENGLISH.toString();
            }
//            } else if ( Locale.ENGLISH.getLanguage().equals(strSystem) ){
//                strAppLanguage = Locale.ENGLISH.toString();
//
//            } else {
//                strAppLanguage = Locale.TAIWAN.toString();
//            }
        } else {
            strAppLanguage = strMemory;
        }

        return strAppLanguage;
        //return AppInfo.getInstance(CIApplication.getContext()).getCommonSharedPreferences().getString(LANGUAGE_TAG, Locale.TAIWAN.toString() );
    }

    /**
     * 取得WS需要帶入的語系
     * 會根據App抓到的語系, 經過轉換與WS共同定義的Tag
     * zh-TW / zh-CN / ja-JP / en-US
     * @return String App的語系,*/
    public String getWSLanguage(){
        //待定義
        String strTag =  LoadLanguage();
        if ( Locale.TAIWAN.toString().equals(strTag) ){
            return "zh-TW";
        } else if ( Locale.CHINA.toString().equals(strTag) ){
            return "zh-CN";
        } else if ( Locale.JAPAN.toString().equals(strTag) ){
            return "ja-JP";
        } else if ( Locale.ENGLISH.toString().equals(strTag) ){
            return "en-US";
        } else {
            //2017-04-05 modifly 調整為預設英文
            //return "zh-TW";
            return "en-US";
        }
    }
}

