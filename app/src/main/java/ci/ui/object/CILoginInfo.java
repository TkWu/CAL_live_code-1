package ci.ui.object;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import ci.function.Core.EncryptValueManager;
import ci.ui.define.UiMessageDef;
import ci.ws.Models.entities.CILoginResp;

/**
 * Created by JobsNo5 on 16/3/26.
 * 2016-05-17 modifly by ryan for 新增 ProfileFile 欄位, 將 登入後資訊與Profile 整合
 */
public class CILoginInfo {

    private final String TAG = "LoginInfo";

    private Context m_context = null;

    private SharedPreferences m_spLoginInfo = null;

    //SharedPreferences 檔名定義
    /**
     * 登入狀態
     */
    private static final String KEY_LOGIN_STATUS   = "KEY_LOGIN_STATUS";
    /**
     * 登入類型
     */
    private static final String KEY_LOGIN_TYPE     = "KEY_LOGIN_TYPE";
    /**
     * 禮遇卡卡別
     */
    private static final String KEY_VIP_CARD_TYPE  = "KEY_VIP_CARD_TYPE";
    /**
     * 禮遇卡生效日
     */
    private static final String KEY_VIP_CARD_EFF   = "KEY_VIP_CARD_EFF";
    /**
     * 禮遇卡到期日
     */
    private static final String KEY_VIP_CARD_EXP   = "KEY_VIP_CARD_EXP";
    /**
     * 正式會員卡別
     */
    private static final String KEY_CARD_TYPE      = "KEY_CARD_TYPE";
    /**
     * fb是否已連結正式會員
     */
    private static final String KEY_FB_CONNECT     = "KEY_FB_CONNECT";
    /**
     * google是否已連結正式會員
     */
    private static final String KEY_GOOGLE_CONNECT = "KEY_GOOGLE_CONNECT";
    /**
     * 社群登入 Id
     */
    private static final String KEY_OPEN_ID            = "KEY_OPEN_ID";
    /**
     * Google 社群登入 Id
     */
    private static final String KEY_GOOGLE_OPEN_ID     = "KEY_GOOGLE_OPEN_ID";
    /**
     * FB 社群登入 Id
     */
    private static final String KEY_FB_OPEN_ID = "KEY_FB_OPEN_ID";
    /**
     * 使用者會員卡號
     */
    private static final String KEY_USER_MEMBER_CARD_NO = "KEY_USER_MEMBER_CARD_NO";
    /**
     * 使用者里程數
     */
    private static final String KEY_MILES = "KEY_MILES";
    /**
     * 使用者名稱
     */
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    /**
     * 使用者名稱-姓氏
     */
    private static final String KEY_USER_FIRST_NAME = "KEY_USER_FIRST_NAME";
    /**
     * 使用者名稱-名字
     */
    private static final String KEY_USER_LAST_NAME = "KEY_USER_LAST_NAME";
    /**
     * 使用者信箱
     */
    private static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    /**
     * 使用者google社群連結信箱
     */
    private static final String KEY_GOOGLE_EMAIL = "KEY_GOOGLE_EMAIL";
    /**
     * 使用者fb社群連結信箱
     */
    private static final String KEY_FB_EMAIL = "KEY_FB_EMAIL";
    /**
     * 使用者照片
     */
    private static final String KEY_USER_PHOTO = "KEY_USER_PHOTO";
    /**
     * 是否保持登入狀態
     */
    private static final String KEY_KEEP_LOGIN = "KEY_KEEP_LOGIN";
    /**
     * 卡片到期日
     */
    private static final String KEY_CARD_TYPE_EXP = "KEY_CARD_TYPE_EXP";
    //
    //額外新個人資料欄位
    //
    //行動電話
    private static final String KEY_CELL_NUM = "KEY_CELL_NUM";
    //行動電話國碼
    private static final String KEY_CELL_CITY = "KEY_CELL_CITY";
    //中文姓名
    private static final String KEY_CHIN_NAME = "KEY_CHIN_NAME";
    //該使用者註冊時所填的國籍代碼
    private static final String KEY_NATION_CODE = "KEY_NATION_CODE";
    //使用者身分證字號
    private static final String KEY_ID_NUM = "KEY_ID_NUM";
    //使用者稱謂
    private static final String KEY_SURNAME = "KEY_SURNAME";
    //使用者生日
    private static final String KEY_BIRTH_DATE = "KEY_BIRTH_DATE";
    //是否接收email訊息
    private static final String KEY_RCV_EMAIL = "KEY_RCV_EMAIL";
    //是否接收手機簡訊
    private static final String KEY_RCV_SMS = "KEY_RCV_SMS";
    //會員護照號碼
    private static final String KEY_PASSPORT = "KEY_PASSPORT";
    //監護人會員卡號
    private static final String KEY_GUARD_CARD_NO = "KEY_GUARD_CARD_NO";
    //餐點偏好
    private static final String KEY_MEAL_TYPE = "KEY_MEAL_TYPE";
    //座位偏好 NSSW(靠窗) / NSSA(靠走道)
    private static final String KEY_SEAT_CODE = "KEY_SEAT_CODE";
    //監護人會員生日
    private static final String KEY_GUARD_BIRTH_DATE = "KEY_GUARD_BIRTH_DATE";
    //監護人會員first name
    private static final String KEY_GUARD_FIRST_NAME = "KEY_GUARD_FIRST_NAME";
    //監護人會員last name
    private static final String KEY_GUARD_LAST_NAME = "KEY_GUARD_LAST_NAME";
    //使用者 profile first name
    private static final String KEY_PROFILE_USER_FIRST_NAME = "KEY_PROFILE_USER_FIRST_NAME";
    //使用者 profile last name
    private static final String KEY_PROFILE_USER_LAST_NAME = "KEY_PROFILE_USER_LAST_NAME";
    //登入後的Token
    private static final String KEY_MEMBER_TOKEN           = "KEY_MEMBER_TOKEN";
    //SharedPreferences 欄位預設值
    /**
     * 推播總開關
     */
    private static final boolean DEF_GCM_SWITCH = true;
    /**
     * 登入狀態
     */
    private static final boolean DEF_LOGIN_STATUS = false;
    /**
     * 登入資料
     */
    private static final String DEF_LOGIN_DATA = "";


    public CILoginInfo(Context context) {

        this.m_context = context;
        m_spLoginInfo = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    /**
     * 設定「登入狀態」
     *
     * @param bEnable 已登入/未登入
     */
    public void SetLoginStatus(boolean bEnable) {

        if (null == m_spLoginInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spLoginInfo, KEY_LOGIN_STATUS, bEnable);
    }

    /**
     * @return 「登入狀態」是否已登入
     */
    public boolean GetLoginStatus() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_STATUS;
        }

        return EncryptValueManager.getBoolean(m_spLoginInfo, KEY_LOGIN_STATUS, DEF_LOGIN_STATUS);
    }

    /**
     * 設定「登入類型」
     *
     * @param strLoginType 登入類型
     */
    public void SetLoginType(String strLoginType) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_LOGIN_TYPE, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_LOGIN_TYPE, strLoginType);
        }

    }

    /**
     * @return 「登入類型」
     */
    public String GetLoginType() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_LOGIN_TYPE, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「正式會員卡別」
     *
     * @param strLoginType 正式會員卡別
     *                     {@link ci.ws.define.CICardType}
     */
    public void SetCardType(String strLoginType) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_CARD_TYPE, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_CARD_TYPE, strLoginType);
        }

    }

    /**
     * @return 「正式會員卡別」
     * {@link ci.ws.define.CICardType}
     */
    public String GetCardType() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_CARD_TYPE, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「禮遇卡卡別」
     *
     * @param data 禮遇卡卡別
     */
    public void SetVipCardType(String data) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_VIP_CARD_TYPE, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_VIP_CARD_TYPE, data);
        }

    }

    /**
     * @return 「禮遇卡卡別」
     */
    public String GetVipCardType() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_VIP_CARD_TYPE, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「禮遇卡到期日」
     *
     * @param data 禮遇卡到期日
     */
    public void SetVipCardExpDate(String data) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_VIP_CARD_EXP, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_VIP_CARD_EXP, data);
        }

    }

    /**
     * @return 「禮遇卡到期日」
     */
    public String GetVipCardExpDate() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_VIP_CARD_EXP, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「禮遇卡生效日」
     *
     * @param data 禮遇卡生效日
     */
    public void SetVipCardEffDate(String data) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_VIP_CARD_EFF, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_VIP_CARD_EFF, data);
        }

    }

    /**
     * @return 「禮遇卡生效日」
     */
    public String GetVipCardEffDate() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_VIP_CARD_EFF, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「FB連結狀態」
     *
     * @param bEnable 是否已經綁定FB帳號
     */
    public void SetFbCombineStatus(boolean bEnable) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_FB_CONNECT, false);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_FB_CONNECT, bEnable);
        }
    }

    /**
     * @return fb是否已綁定連結正式會員
     */
    public boolean GetFbCombineStatus() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_STATUS;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_STATUS;
        } else {
            return EncryptValueManager.getBoolean(m_spLoginInfo, KEY_FB_CONNECT, false);
        }
    }

    /**
     * 設定「Google連結狀態」
     */
    public void SetGoogleCombineStatus(boolean bEnable) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_GOOGLE_CONNECT, false);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_GOOGLE_CONNECT, bEnable);
        }
    }

    /**
     * @return Google是否已連結正式會員
     */
    public boolean GetGoogleCombineStatus() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_STATUS;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_STATUS;
        } else {
            return EncryptValueManager.getBoolean(m_spLoginInfo, KEY_GOOGLE_CONNECT, false);
        }
    }

    /**
     * 設定「使用者名稱」用於顯示sidemenu或成為華夏會員...等頁面
     * 社群登入可能會沒有分姓跟名 所以需要此參數另外存放
     *
     * @param strUserName 使用者名稱
     */
    public void SetUserName(String strUserName) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_NAME, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_NAME, strUserName);
        }

    }

    /**
     * @return 「使用者名稱」
     */
    public String GetUserName() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_USER_NAME, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「使用者名稱-名字」(華夏登入時,會依語系回傳FirstName)
     */
    public void SetUserFirstName(String strUserFirstName) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_FIRST_NAME, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_FIRST_NAME, strUserFirstName);
        }

    }

    /**
     * @return 「使用者名稱-名字」
     */
    public String GetUserFirstName() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_USER_FIRST_NAME, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「使用者名稱-姓氏」(華夏登入時,會依語系回傳LastName)
     */
    public void SetUserLastName(String strUserLastName) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_LAST_NAME, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_LAST_NAME, strUserLastName);
        }

    }

    /**
     * @return 「使用者名稱-姓氏」
     */
    public String GetUserLastName() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_USER_LAST_NAME, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「使用者會員卡號」
     */
    public void SetUserMemberCardNo(String strUserName) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (!GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_MEMBER_CARD_NO, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_MEMBER_CARD_NO, strUserName);
        }

    }

    /**
     * @return 「使用者會員卡號」
     */
    public String GetUserMemberCardNo() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (!GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else if ( TextUtils.equals( GetLoginType(), UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER) ){
            return EncryptValueManager.getString(m_spLoginInfo, KEY_USER_MEMBER_CARD_NO, DEF_LOGIN_DATA);
        } else {
            return DEF_LOGIN_DATA;
        }
    }

    /**
     * 設定「使用者會員卡有效期限」
     */
    public void SetCardTypeExp(String strUserName) {

        if (null == m_spLoginInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spLoginInfo, KEY_CARD_TYPE_EXP, strUserName);
    }

    /**
     * @return 「使用者會員卡有效期限」
     */
    public String GetCardTypeExp() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_CARD_TYPE_EXP, DEF_LOGIN_DATA);
        }
    }


    /**
     * 設定「里程數」
     */
    public void SetMiles(String strMiles) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_MILES, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_MILES, strMiles);
        }

    }

    /**
     * @return 「里程數」
     */
    public String GetMiles() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_MILES, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「社群登入 Id」
     *
     * @param strOpenId
     */
    public void SetSocialLoginId(String strOpenId) {

        if (null == m_spLoginInfo) {
            return;
        }

        if( null == strOpenId ) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_OPEN_ID, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_OPEN_ID, strOpenId);
        }
    }

    /**
     * @return 「社群登入 Id」
     */
    public String GetSocialLoginId() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if( TextUtils.isEmpty(GetLoginType()) ) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_OPEN_ID, DEF_LOGIN_DATA);
        }
    }

    public void SetGoogleLoginId(String strOpenId) {
        if (null == m_spLoginInfo) {
            return;
        }

        if( null == strOpenId ) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_GOOGLE_OPEN_ID, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_GOOGLE_OPEN_ID, strOpenId);
        }
    }

    public String GetGoogleLoginId() {
        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_GOOGLE_OPEN_ID, DEF_LOGIN_DATA);
    }

    public void SetFbLoginId(String strOpenId) {
        if (null == m_spLoginInfo) {
            return;
        }

        if( null == strOpenId ) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_FB_OPEN_ID, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_FB_OPEN_ID, strOpenId);
        }
    }

    public String GetFbLoginId() {
        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_FB_OPEN_ID, DEF_LOGIN_DATA);

    }

    /**
     * 設定「使用者信箱」
     *
     * @param strUserEmail 使用者信箱
     */
    public void SetUserEmail(String strUserEmail) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_EMAIL, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_EMAIL, strUserEmail);
        }

    }

    /**
     * @return 「使用者信箱」
     */
    public String GetUserEmail() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_USER_EMAIL, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「FB社群連結信箱」
     */
    public void SetFbEmail(String strUserEmail) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_FB_EMAIL, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_FB_EMAIL, strUserEmail);
        }

    }

    /**
     * @return 「FB社群連結信箱」
     */
    public String GetFbEmail() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_FB_EMAIL, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「Google社群連結信箱」
     */
    public void SetGoogleEmail(String strUserEmail) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_GOOGLE_EMAIL, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_GOOGLE_EMAIL, strUserEmail);
        }

    }

    /**
     * @return 「Google社群連結信箱」
     */
    public String GetGoogleEmail() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_GOOGLE_EMAIL, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「使用者照片」
     *
     * @param strUserPhotoUrl 使用者照片
     */
    public void SetUserPhotoUrl(String strUserPhotoUrl) {

        if (null == m_spLoginInfo) {
            return;
        }

        if (false == GetLoginStatus()) {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_PHOTO, DEF_LOGIN_DATA);
        } else {
            EncryptValueManager.setValue(m_spLoginInfo, KEY_USER_PHOTO, strUserPhotoUrl);
        }

    }

    /**
     * @return 「使用者照片」
     */
    public String GetUserPhotoUrl() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_DATA;
        }

        if (false == GetLoginStatus()) {
            return DEF_LOGIN_DATA;
        } else {
            return EncryptValueManager.getString(m_spLoginInfo, KEY_USER_PHOTO, DEF_LOGIN_DATA);
        }
    }

    /**
     * 設定「登入狀態」
     *
     * @param bEnable 已登入/未登入
     */
    public void SetKeepLogin(boolean bEnable) {

        if (null == m_spLoginInfo) {
            return;
        }

        EncryptValueManager.setValue(m_spLoginInfo, KEY_KEEP_LOGIN, bEnable);
    }

    /**
     * @return 「登入狀態」是否已登入
     */
    public boolean GetKeepLogin() {

        if (null == m_spLoginInfo) {
            return DEF_LOGIN_STATUS;
        }

        return EncryptValueManager.getBoolean(m_spLoginInfo, KEY_KEEP_LOGIN, DEF_LOGIN_STATUS);
    }

    /**
     * 設定「行動電話」
     *
     * @param strCellNum 行動電話
     */
    public void SetCellNum(String strCellNum) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_CELL_NUM, strCellNum);
    }

    /**
     * @return 「行動電話」
     */
    public String GetCellNum() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_CELL_NUM, "");
    }

    /**
     * 設定「行動電話國碼」
     *
     * @param strCellCity 行動電話國碼
     */
    public void SetCellCity(String strCellCity) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_CELL_CITY, strCellCity);
    }

    /**
     * @return 「行動電話國碼」
     */
    public String GetCellCity() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_CELL_CITY, "");
    }

    /**
     * 設定「中文姓名」
     *
     * @param strChinName 中文姓名
     */
    public void SetChinName(String strChinName) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_CHIN_NAME, strChinName);
    }

    /**
     * @return 「中文姓名」
     */
    public String GetChinName() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_CHIN_NAME, "");
    }

    /**
     * 設定「該使用者註冊時所填的國籍代碼」
     *
     * @param strNationCode 該使用者註冊時所填的國籍代碼
     */
    public void SetNationCode(String strNationCode) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_NATION_CODE, strNationCode);
    }

    /**
     * @return 「該使用者註冊時所填的國籍代碼」
     */
    public String GetNationCode() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_NATION_CODE, "");
    }

    /**
     * 設定「使用者身分證字號」
     *
     * @param strIdNum 使用者身分證字號
     */
    public void SetIDNum(String strIdNum) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_ID_NUM, strIdNum);
    }

    /**
     * @return 「使用者身分證字號」
     */
    public String GetIDNum() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_ID_NUM, "");
    }

    /**
     * 設定「使用者稱謂」
     *
     * @param strSurName 使用者稱謂
     */
    public void SetSurName(String strSurName) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_SURNAME, strSurName);
    }

    /**
     * @return 「使用者稱謂」
     */
    public String GetSurName() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_SURNAME, "");
    }

    /**
     * 設定「使用者生日」
     *
     * @param strBirthday 使用者生日
     */
    public void SetBirthday(String strBirthday) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_BIRTH_DATE, strBirthday);
    }

    /**
     * @return 「使用者生日」
     */
    public String GetBirthday() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_BIRTH_DATE, "");
    }

    /**
     * 設定「是否接收email訊息」
     *
     * @param strRcvEmail 是否接收email訊息
     */
    public void SetRcvEmail(String strRcvEmail) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_RCV_EMAIL, strRcvEmail);
    }

    /**
     * @return 「是否接收email訊息」
     */
    public String GetRcvEmail() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_RCV_EMAIL, "");
    }

    /**
     * 設定「是否接收手機簡訊」
     *
     * @param strRcvSMS 是否接收手機簡訊
     */
    public void SetRcvSMS(String strRcvSMS) {

        if (null == m_spLoginInfo) {
            return;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_RCV_SMS, strRcvSMS);
    }

    /**
     * @return 「是否接收手機簡訊」
     */
    public String GetRcvSMS() {

        if (null == m_spLoginInfo) {
            return "";
        }
        return EncryptValueManager.getString(m_spLoginInfo, KEY_RCV_SMS, "");
    }

    /**
     * @param strPassport 會員護照號碼
     */
    public void SetPassport(String strPassport) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_PASSPORT, strPassport);
    }

    /**
     * @return 「會員護照號碼」
     */
    public String GetPassport() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_PASSPORT, "");
    }
    /**
     * @param strGuardCardNo 監護人會員卡號
     */
    public void SetGuardCardNo(String strGuardCardNo) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_GUARD_CARD_NO, strGuardCardNo);
    }
    /**
     * @return 「監護人會員卡號」
     */
    public String GetGuardCardNo() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_GUARD_CARD_NO, "");
    }

    /**
     * @param strMealType 餐點偏好
     */
    public void SetMealType(String strMealType) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_MEAL_TYPE, strMealType);
    }
    /**
     * @return 「餐點偏好」
     */
    public String GetMealType() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_MEAL_TYPE, "");
    }

    /**
     * @param strSeatCode 座位偏好 NSSW(靠窗) / NSSA(靠走道)
     */
    public void SetSeatCode(String strSeatCode) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_SEAT_CODE, strSeatCode);
    }

    /**
     * @return 「座位偏好 NSSW(靠窗) / NSSA(靠走道)」
     */
    public String GetSeatCode() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_SEAT_CODE, "");
    }

    /**
     * @param strGuardBirthDate 監護人會員生日
     */
    public void SetGuardBirthDate(String strGuardBirthDate) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_GUARD_BIRTH_DATE, strGuardBirthDate);
    }

    /**
     * @return 「監護人會員生日」
     */
    public String GetGuardBirthDate() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_GUARD_BIRTH_DATE, "");
    }

    /**
     * @param strGuardFirstName 監護人會員生日
     */
    public void SetGuardFirstName(String strGuardFirstName) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_GUARD_FIRST_NAME, strGuardFirstName);
    }

    /**
     * @return 「監護人會員生日」
     */
    public String GetGuardFirstName() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_GUARD_FIRST_NAME, "");
    }
    /**
     * @param strGuardLasttName 監護人會員生日
     */
    public void SetGuardLastName(String strGuardLasttName) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_GUARD_LAST_NAME, strGuardLasttName);
    }

    /**
     * @return 「監護人會員生日」
     */
    public String GetGuardLastName() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_GUARD_LAST_NAME, "");
    }

    /**
     * @param strUserProfileFirstName 監護人會員生日
     */
    public void SetUserProfileFirstName(String strUserProfileFirstName) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_PROFILE_USER_FIRST_NAME, strUserProfileFirstName);
    }

    /**
     * @return 「監護人會員生日」
     */
    public String GetUserProfileFirstName() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_PROFILE_USER_FIRST_NAME, "");
    }

    /**
     * @param strUserProfileLastName 監護人會員生日
     */
    public void SetUserProfileLastName(String strUserProfileLastName) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_PROFILE_USER_LAST_NAME, strUserProfileLastName);
    }

    /**
     * @return 「監護人會員生日」
     */
    public String GetUserProfileLastName() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_PROFILE_USER_LAST_NAME, "");
    }

    /**
     * @param strMemberToken 會員Token
     */
    public void SetMemberToken(String strMemberToken) {
        if (null == m_spLoginInfo) {
            return ;
        }
        EncryptValueManager.setValue(m_spLoginInfo, KEY_MEMBER_TOKEN, strMemberToken);
    }

    /**
     * @return 「會員Token」
     */
    public String GetMemberToken() {
        if( null == m_spLoginInfo ) {
            return "";
        }

        return EncryptValueManager.getString(m_spLoginInfo, KEY_MEMBER_TOKEN, "");
    }

    /**
     * 判斷是否為正式會員
     * @return 假如為真，回傳true
     */
    public boolean isDynastyFlyerMember(){
        return true == GetLoginStatus() && GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER);
    }


    /**登入時寫入登入資訊*/
    public void SetLoginData(String loginType, CILoginResp loginResult){
        SetLoginStatus(true);
        SetLoginType(loginType);

        if ( null == loginResult.first_name )
            loginResult.first_name = "";

        if ( null == loginResult.last_name )
            loginResult.last_name = "";

        SetUserFirstName(loginResult.first_name);
        SetUserLastName(loginResult.last_name);
        SetUserName(loginResult.last_name + ", " + loginResult.first_name);

        if ( null == loginResult.card_no )
            loginResult.card_no = "";
        SetUserMemberCardNo(loginResult.card_no);

        if ( null == loginResult.card_type )
            loginResult.card_type = "";
        SetCardType(loginResult.card_type);

        if ( null == loginResult.mileage )
            loginResult.mileage = "0";
        SetMiles(loginResult.mileage);

        if ( null == loginResult.card_type_exp )
            loginResult.card_type_exp = "";
        SetCardTypeExp(loginResult.card_type_exp);

        SetUserEmail(loginResult.email);

        if ( null == loginResult.member_token )
            loginResult.member_token = "";
        SetMemberToken(loginResult.member_token);


//        SetFbCombineStatus(false);
//        SetGoogleCombineStatus(false);
//        SetGoogleEmail("");
//        SetSocialLoginId("");
//        SetUserPhotoUrl("");
//        SetUserEmail("");
    }

    /**登出時清除登入資訊*/
    public void ClearLoginData(){
        SetLoginStatus(false);
        SetLoginType("");
        SetMemberToken("");

        SetFbCombineStatus(false);
        SetFbEmail("");
        SetFbLoginId("");

        SetGoogleCombineStatus(false);
        SetGoogleEmail("");
        SetGoogleLoginId("");

        SetSocialLoginId("");
        SetUserName("");
        SetUserFirstName("");
        SetUserLastName("");
        SetUserEmail("");
        SetUserPhotoUrl("");

        SetCardType("");
        SetMiles("");
        SetUserMemberCardNo("");
        SetCardTypeExp("");

        //Profile
        SetBirthday("");
        SetCellCity("");
        SetCellNum("");
        SetRcvEmail("");
        SetRcvSMS("");
        SetChinName("");
        SetSurName("");
        SetIDNum("");
        SetNationCode("");
        SetPassport("");
        SetGuardCardNo("");
        SetMealType("");
        SetSeatCode("");
        SetGuardBirthDate("");
        SetGuardFirstName("");
        SetGuardLastName("");
        SetUserProfileFirstName("");
        SetUserProfileLastName("");
    }

    public void clear(){
        m_spLoginInfo.edit().clear().commit();
    }
}
