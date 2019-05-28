package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/6.
 */
public class CIProfileEntity implements Serializable {

    /**座位偏好靠走道*/
    public static final  String SEAT_NSSA = "NSSA";
    /**座位偏好靠窗*/
    public static final  String SEAT_NSSW = "NSSW";
    /**稱謂：先生*/
    public static final  String MR = "MR";
    /**稱謂：小姐*/
    public static final  String MS = "MS";
    /**不綁定社群帳號*/
    public static final String SOCIAL_COMBINE_NO = "N";
    /**綁定社群帳號*/
    public static final String SOCIAL_COMBINE_YES= "Y";

    /**稱謂
     *值不得為空白，畫面請提供選項為 (先生/小姐)，呼叫程式請傳入代號 (MR/MS)。*/
    @Expose
    public String surname;      

    /**國籍
     *值不得為空白，限制2碼，請預設為中華民國。
     *當國籍為中華民國、中國大陸，需要出現“護照中文姓名”欄位，香港、澳門，不需要出現“護照中文姓名”欄位。*/
    @Expose
    public String nation_code;  

    /**會員英文名*/
    @Expose
    public String first_name;   

    /**會員英文姓氏*/
    @Expose
    public String last_name;    

    /**會員中文姓名
     *若是中華民國或中國大陸國籍，護照中文姓名不得為空值。
     *中文姓名限制輸入中文，不得超過 6個字。*/
    @Expose
    public String chin_name;    

    /**會員生日*/
    @Expose
    public String birth_date;   
    /**監護人會員卡號
     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
    public String guard_card_no; 
    /**監護人會員英文名
     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
    public String guard_first_name; 
    /**監護人會員英文性
     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
    public String guard_last_name; 
    /**監護人會員生日
     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
    public String guard_birth_date;

    /**會員身分證
     *若是中華民國國籍，身份證字號不得為空值。
     *需驗證身分證之正確性*/
    @Expose
    public String id_num;       

    /**會員護照號碼
     *非中華民國國籍，護照號碼不得為空值。
     *護照號碼限制20碼，只能輸入英文 & 數字*/
    @Expose
    public String Passport;     

    /**會員Email*/
    @Expose
    public String email;        

    /**促銷訊息接收否
     *是否接收電子訊息，是，回傳Y；否，回傳N*/
    @Expose
    public String rcv_email;    

    /**行動電話國碼(限制 3 碼)*/
    @Expose
    public String cell_city;    

    /**行動電話號碼(限制 12 碼)*/
    @Expose
    public String cell_num;     

    /**是否接收簡訊
     *是，回傳Y；否，回傳N*/
    @Expose
    public String rcv_sms;      

//    /**密碼*/
//    public String password;

    /**餐點偏好{@link ci.ws.Presenter.CIInquiryMealListPresenter}*/
    @Expose
    public String Meal_Type;    

    /**座位偏好
     * NSSW(靠窗)/NSSA(靠走道)*/
    @Expose
    public String Seat_Code;    

    /**社群UID*/
    @Expose
    public String facebook_uid; 
    /**社群信箱*/
    @Expose
    public String facebook_mail;
    /**社群UID*/
    @Expose
    public String google_uid;   
    /**社群信箱*/
    @Expose
    public String google_mail;  

    /**是否綁定Google社群*/
    @Expose
    public String con_google;   

    /**是否綁定FB社群*/
    @Expose
    public String con_facebook; 

    /**禮遇卡生效日*/
    @Expose
    public String VipEffdt;

    /**禮遇卡到期日*/
    @Expose
    public String VipExprdt;

    /**禮遇卡卡別*/
    @Expose
    public String VipType;

    /**會員卡卡別*/
    @Expose
    public String CardType;

    /**卡別到期日*/
    @Expose
    public String CardTypeExp;

    /**郵寄地址*/

    /**代碼*/
    public String addressType;

    /**國家代碼*/
    public String countryCode;
    public String countryName;

    /**城市代碼*/
    public String cityCode;
    public String cityName;

    /**鄉鎮代碼*/
    public String countyCode;
    /**鄉鎮名稱*/
    public String countyName;

    /**路名代碼，國家為TW時*/
    public String street1;
    /**路名名稱，國家為TW時*/
    public String street1_name;
    /**路名名稱*/
    public String street2;
    /**門牌號碼*/
    public String street3;

    /**郵遞區號*/
    public String zipCode;

    /**郵遞區號-清單列表*/
    public ArrayList<String> zipCodeList;

    /**鄰近城市代碼*/
    public String currAreaCode;

    /**鄰近城市名稱*/
    public String currAreaName;
}
