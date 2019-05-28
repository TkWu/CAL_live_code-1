package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/6.
 */
public class CIUpdateProfileEntity {

    /**座位偏好靠走道*/
    public static final  String SEAT_NSSA = "NSSA";
    /**座位偏好靠窗*/
    public static final  String SEAT_NSSW = "NSSW";

//    /**稱謂
//     *值不得為空白，畫面請提供選項為 (先生/小姐)，呼叫程式請傳入代號 (MR/MS)。*/
//    @Expose
//    public String surname;      //V
//
//    /**國籍
//     *值不得為空白，限制2碼，請預設為中華民國。
//     *當國籍為中華民國、中國大陸，需要出現“護照中文姓名”欄位，香港、澳門，不需要出現“護照中文姓名”欄位。*/
//    @Expose
//    public String nation_code;  //V
//
//    /**會員英文名*/
//    @Expose
//    public String first_name;   //V
//
//    /**會員英文姓氏*/
//    @Expose
//    public String last_name;    //V
//
//    /**會員中文姓名
//     *若是中華民國或中國大陸國籍，護照中文姓名不得為空值。
//     *中文姓名限制輸入中文，不得超過 6個字。*/
//    @Expose
//    public String chin_name;    //V
//
//    /**會員生日*/
//    @Expose
//    public String birth_date;   //V
//    /**監護人會員卡號
//     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
//    public String guard_card_no;
//    /**監護人會員英文名
//     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
//    public String guard_first_name;
//    /**監護人會員英文性
//     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
//    public String guard_last_name;
//    /**監護人會員生日
//     *若申請人年齡介於2~12歲之間值不得為空白。卡號前2碼為英文字母 (大寫) + 7位數字*/
//    public String guard_birth_date;
//
//    /**會員身分證
//     *若是中華民國國籍，身份證字號不得為空值。
//     *需驗證身分證之正確性*/
//    @Expose
//    public String id_num;       //V

    /**會員護照號碼
     *非中華民國國籍，護照號碼不得為空值。
     *護照號碼限制20碼，只能輸入英文 & 數字*/
    public String pass_port;

    /**會員Email*/
    @Expose
    public String email;        //V

    /**促銷訊息接收否
     *是否接收電子訊息，是，回傳Y；否，回傳N*/
    @Expose
    public String rcv_email;    //V

    /**行動電話國碼(限制 3 碼)*/
    @Expose
    public String cell_city;    //V

    /**行動電話號碼(限制 12 碼)*/
    @Expose
    public String cell_num;     //V

    /**是否接收簡訊
     *是，回傳Y；否，回傳N*/
    @Expose
    public String rcv_sms;      //V

//    /**密碼*/
//    public String password;

    /**餐點偏好{@link ci.ws.Presenter.CIInquiryMealListPresenter}*/
    public String meal_type;
    /**座位偏好
     * NSSW(靠窗)/NSSA(靠走道)*/
    public String seat_code;

//    /**社群UID*/
//    public String social_id;
//    /**社群商; FACEBOOK/GOOGLE+*/
//    public String social_vendor;
//    /**社群信箱*/
//    public String social_email;
//
//    /**是否綁定Google社群*/
//    @Expose
//    public String con_google;   //V
//
//    /**是否綁定FB社群*/
//    @Expose
//    public String con_facebook; //V

    /**郵寄地址*/
    public CIAddressEntity addressInfo;

    /**郵寄地址代碼*/
    public String addressType;


    public CIUpdateProfileEntity(){
        pass_port   = "";
        email       = "";
        rcv_email   = "";
        cell_city   = "";
        cell_num    = "";
        rcv_sms     = "";
        meal_type   = "";
        seat_code   = SEAT_NSSA;
        addressInfo = new CIAddressEntity();
    }
}
