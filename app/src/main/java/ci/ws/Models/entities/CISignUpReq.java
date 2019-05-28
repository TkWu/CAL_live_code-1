package ci.ws.Models.entities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ryan on 16/3/30.
 * 請勿任意變動變數名稱, 名稱都與json tag 有對應
 */
public class CISignUpReq implements Cloneable{


    /**座位偏好靠走道*/
    public static final  String SEAT_NSSA = "NSSA";
    /**座位偏好靠窗*/
    public static final  String SEAT_NSSW = "NSSW";
    /**稱謂：先生*/
    public static final  String MR = "MR";
    /**稱謂：小姐*/
    public static final  String MS = "MS";
    /**不綁定社群帳號*/
    public static final String SOCIAL_COMBINE_NO = "NO";
    /**綁定社群帳號*/
    public static final String SOCIAL_COMBINE_YES= "YES";

    /**稱謂
     *值不得為空白，畫面請提供選項為 (先生/小姐)，呼叫程式請傳入代號 (MR/MS)。*/
    public String surname;
    /**國籍
     *值不得為空白，限制2碼，請預設為中華民國。
     *當國籍為中華民國、中國大陸，需要出現“護照中文姓名”欄位，香港、澳門，不需要出現“護照中文姓名”欄位。*/
    public String nation_code;
    /**會員英文名*/
    public String first_name;
    /**會員英文姓氏*/
    public String last_name;
    /**會員中文姓名
     *若是中華民國或中國大陸國籍，護照中文姓名不得為空值。
     *中文姓名限制輸入中文，不得超過 6個字。*/
    public String chin_name;
    /**會員生日*/
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
    public String id_num;
    /**會員護照號碼
     *非中華民國國籍，護照號碼不得為空值。
     *護照號碼限制20碼，只能輸入英文 & 數字*/
    public String passport;
    /**會員Email*/
    public String email;
    /**促銷訊息接收否
     *是否接收電子訊息，是，回傳Y；否，回傳N*/
    public String rcv_email;
    /**行動電話國碼(限制 3 碼)*/
    public String cell_city;
    /**行動電話號碼(限制 12 碼)*/
    public String cell_num;
    /**是否接收簡訊
     *是，回傳Y；否，回傳N*/
    public String rcv_sms;
    /**密碼*/
    public String password;

    /**餐點偏好{@link ci.ws.Models.CIInquiryMealListModel}*/
    public String meal_type;
    /**座位偏好
     * NSSW(靠窗)/NSSA(靠走道)*/
    public String seat_code;
    /**社群UID*/
    public String social_id;
    /**社群商; FACEBOOK/GOOGLE+*/
    public String social_vendor;
    /**社群信箱*/
    public String social_email;
    /**是否綁定社群*/
    public String is_social_combine;

    /**郵寄地址代碼*/
    public String addressType;

    //2019-01-29 郵寄地址
    public CIAddressEntity addressInfo;

    public CISignUpReq(){

        this.surname="";
        this.nation_code="";
        this.first_name="";
        this.last_name="";
        this.chin_name="";
        this.birth_date="";
        this.guard_card_no="";
        this.guard_first_name="";
        this.guard_last_name="";
        this.guard_birth_date="";
        this.id_num="";
        this.passport="";
        this.email="";
        this.rcv_email="";
        this.cell_city="";
        this.cell_num="";
        this.rcv_sms="";
        this.password="";
        this.meal_type="";
        this.seat_code="";
        this.social_id="";
        this.social_vendor="";
        this.social_email = "";
        this.is_social_combine=SOCIAL_COMBINE_NO;
        this.addressInfo = new CIAddressEntity();
    }

    /**
     * 取得稱謂代號的arraylist
     * @return
     */
    public static ArrayList<String> getSurnameCodeArray(){
        ArrayList<String> arrayList = new ArrayList<>();

        //依照string resource 順序排列
        arrayList.add(MR);
        arrayList.add(MS);
        return arrayList;
    }

    /**
     * 取得座位代號的arraylist
     * @return
     */
    public static ArrayList<String> getSeatCodeArray(){
        ArrayList<String> arrayList = new ArrayList<>();

        //依照string resource 順序排列
        arrayList.add("");
        arrayList.add(SEAT_NSSA);
        arrayList.add(SEAT_NSSW);
        return arrayList;
    }

    /**
     * 取得接收電子郵件或訊息代號的arraylist
     * @return
     */
    public static HashMap<String,Boolean> getReciverCodeMap(){
        HashMap<String,Boolean> mapList = new HashMap<>();
        mapList.put("Y",true);
        mapList.put("N",false);
        return mapList;
    }

    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
