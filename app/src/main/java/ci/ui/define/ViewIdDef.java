package ci.ui.define;

import android.content.Context;
import android.text.TextUtils;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ci.function.About.CIAboutFragment;
import ci.function.BoardingPassEWallet.CIBoardingPassEWalletFragment;
import ci.function.Core.CIConstructionFragment;
import ci.function.ExtraServices.CIExtraServicesFragment;
import ci.function.FlightStatus.CIFlightStatusFragment;
import ci.function.HomePage.CIHomeFragment;
import ci.function.ManageMiles.CIManageMilesFragment;
import ci.function.MyTrips.CIMyTripsFragment;
import ci.function.PersonalDetail.CIPersonalFragment;
import ci.function.Setting.CISettingFragment;
import ci.function.TimeTable.CITimeTableFragment;
import ci.function.Main.item.SideMenuItem;

/**
 * 定義SideMenu上會使用到的ViewCode
 */
public class ViewIdDef {

    public static final int VIEW_ID_NONE                = 0000;
    //--- Main - Side Menu ---
    /**首頁*/
    public static final int VIEW_ID_HOME                = 1000;
    //右側
    /**訂票*/
    public static final int VIEW_ID_BOOK_TICKET         = 1001;
    /**登機*/
    public static final int VIEW_ID_CHECK_IN            = 1002;
    /**航班狀態*/
    public static final int VIEW_ID_FIGHT_STATUS        = 1003;
    /**航班時刻表*/
    public static final int VIEW_ID_TIMETABLE           = 1004;
    /**額外服務*/
    public static final int VIEW_ID_EXTRA_SERVICES      = 1005;
    /**線上購物*/
    public static final int VIEW_ID_E_SHOPPING          = 1006;
    /**促銷*/
    public static final int VIEW_ID_PROMOTIONS          = 1007;
    /**關於*/
    public static final int VIEW_ID_ABOUT               = 1008;
    /**設定*/
    public static final int VIEW_ID_SETTING             = 1009;
    /**行李追蹤*/
    public static final int VIEW_ID_BAGGAGE_TRACKING    = 1010;
    /**智能客服*/
    public static final int VIEW_ID_AI_SERVICE          = 1011;

    //左側
    /**登機證*/
    public static final int VIEW_ID_BOARDINGPASS_EWALLET= 1100;
    /**我的行程*/
    public static final int VIEW_ID_MY_TRIPS            = 1101;
    /**里程管理*/
    public static final int VIEW_ID_MANAGE_MILES        = 1102;
    /**個人資料*/
    public static final int VIEW_ID_PERSONAL_DETAIL     = 1103;

    /**Demo模式*/
    public static final int VIEW_ID_DEMO                = 9000;

    private static ViewIdDef m_Instance = null;

    private static final int MENU_TYPE_LEFT             = 0;
    private static final int MENU_TYPE_RIGHT            = 1;
    private static final int MENU_TYPE_HOME             = 2;

    /**會員小卡狀態*/
    public static final int PERSONAL_TYPE_NOT_LOGIN     = 0;
    public static final int PERSONAL_TYPE_TEMPORARY     = 1;
    public static final int PERSONAL_TYPE_DYNASTY_FLYER = 2;


    private Map< Integer, ArrayList<ArrayList<SideMenuItem>>> m_mapSideMenuList = new HashMap< Integer, ArrayList<ArrayList<SideMenuItem>>>();

    //private ArrayList<ArrayList<SideMenuItem>> m_LeftMenuItemList = new ArrayList<ArrayList<SideMenuItem>>();
    //private ArrayList<ArrayList<SideMenuItem>> m_RightMenuItemList = new ArrayList<ArrayList<SideMenuItem>>();


    public static ViewIdDef getInstance( Context context ){
        if ( m_Instance == null ){

            m_Instance = new ViewIdDef();
            m_Instance.initialLeftMenuList(context);
            m_Instance.initialHomeMenu(context);
            m_Instance.initialRightMenuList(context);
        }
        return m_Instance;
    }

    private void initialLeftMenuList( Context context ){

        ArrayList<ArrayList<SideMenuItem>> MenuItemList = new ArrayList<>();
        ArrayList<SideMenuItem> arSideMenuList = new ArrayList<SideMenuItem>();
        arSideMenuList.clear();

        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_BOARDINGPASS_EWALLET, R.string.menu_title_boarding_pass_ewallet, R.drawable.ic_boarding_pass, R.drawable.ic_boarding_pass_n, CIBoardingPassEWalletFragment.class, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_MY_TRIPS, R.string.menu_title_my_trips, R.drawable.ic_my_trips, R.drawable.ic_my_trips_n, CIMyTripsFragment.class, false, 0));
        arSideMenuList.add( new SideMenuItem( ViewIdDef.VIEW_ID_MANAGE_MILES, R.string.menu_title_manage_miles, R.drawable.ic_manage_miles, R.drawable.ic_manage_miles_n, CIManageMilesFragment.class, false, 0 ));
        arSideMenuList.add( new SideMenuItem( ViewIdDef.VIEW_ID_PERSONAL_DETAIL, R.string.menu_title_personal_detail, R.drawable.ic_personal_detail, R.drawable.ic_personal_detail_n, CIPersonalFragment.class, false, 0 ));

        MenuItemList.add(arSideMenuList);
        m_mapSideMenuList.put( MENU_TYPE_LEFT, MenuItemList );
    }

    private void initialHomeMenu(Context context){

        ArrayList<ArrayList<SideMenuItem>> MenuItemList = new ArrayList<>();
        ArrayList<SideMenuItem> arSideMenuList = new ArrayList<SideMenuItem>();
        arSideMenuList.clear();
        //一
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_HOME, R.string.menu_title_homepage, R.drawable.ic_home, R.drawable.ic_home_n, CIHomeFragment.class, false, 0));
        MenuItemList.add(arSideMenuList);
        m_mapSideMenuList.put(MENU_TYPE_HOME, MenuItemList);
    }

    private void initialRightMenuList( Context context ){

        ArrayList<ArrayList<SideMenuItem>> MenuItemList = new ArrayList<>();
        //一
        ArrayList<SideMenuItem> arSideMenuList = new ArrayList<SideMenuItem>();
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_BOOK_TICKET, R.string.menu_title_book_ticket, R.drawable.ic_book_flight, R.drawable.ic_book_flight_n, null, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_CHECK_IN, R.string.menu_title_check_in, R.drawable.ic_checkin, R.drawable.ic_checkin_n, null, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_FIGHT_STATUS, R.string.menu_title_flight_status, R.drawable.ic_flight_status, R.drawable.ic_flight_status_n, CIFlightStatusFragment.class, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_TIMETABLE, R.string.menu_title_timetable, R.drawable.ic_timetablet, R.drawable.ic_timetablet_n, CITimeTableFragment.class, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_BAGGAGE_TRACKING, R.string.menu_title_baggage_tracking, R.drawable.ic_menu_baggage_tracing, R.drawable.ic_menu_baggage_tracing_n, null, false, 0));
        MenuItemList.add(arSideMenuList);
        //二
        arSideMenuList = new ArrayList<SideMenuItem>();
        arSideMenuList.add( new SideMenuItem( ViewIdDef.VIEW_ID_EXTRA_SERVICES, R.string.menu_title_extra_services, R.drawable.ic_extra_services, R.drawable.ic_extra_services_n, CIExtraServicesFragment.class, false, 0 ));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_E_SHOPPING, R.string.menu_title_e_shopping, R.drawable.ic_e_shopping, R.drawable.ic_e_shopping_n, null, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_PROMOTIONS, R.string.menu_title_promotions, R.drawable.ic_promotion, R.drawable.ic_promotion_n, null, false, 0));
        MenuItemList.add(arSideMenuList);
        //三
        arSideMenuList = new ArrayList<SideMenuItem>();
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_AI_SERVICE, R.string.menu_title_ai_service, R.drawable.ic_menu_ai_service, R.drawable.ic_menu_ai_service_n, null, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_ABOUT, R.string.menu_title_about, R.drawable.ic_about_china_airlines, R.drawable.ic_about_china_airlines_n, CIAboutFragment.class, false, 0));
        arSideMenuList.add(new SideMenuItem(ViewIdDef.VIEW_ID_SETTING, R.string.menu_title_setting, R.drawable.ic_settings, R.drawable.ic_settings_n, CISettingFragment.class, false, 0));
        MenuItemList.add(arSideMenuList);
        m_mapSideMenuList.put(MENU_TYPE_RIGHT, MenuItemList);
    }

    public ArrayList<ArrayList<SideMenuItem>> getLeftMenuList(){

        if ( null == m_mapSideMenuList ){
            return new ArrayList<ArrayList<SideMenuItem>>();
        }
        return m_mapSideMenuList.get(MENU_TYPE_LEFT);
    }

    public ArrayList<ArrayList<SideMenuItem>> getRightMenuList(){

        if ( null == m_mapSideMenuList ){
            return new ArrayList<ArrayList<SideMenuItem>>();
        }
        return m_mapSideMenuList.get(MENU_TYPE_RIGHT);
    }


    public ArrayList<ArrayList<SideMenuItem>> getHOMEMenuList(){

        if ( null == m_mapSideMenuList ){
            return new ArrayList<ArrayList<SideMenuItem>>();
        }
        return m_mapSideMenuList.get(MENU_TYPE_HOME);
    }
}
