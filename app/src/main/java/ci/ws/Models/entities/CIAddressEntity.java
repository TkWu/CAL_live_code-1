package ci.ws.Models.entities;

public class CIAddressEntity {

    public CIAddressEntity(){
        countryCode = "";
        countryName_CHN = "";
        countryName_ENG = "";
        cityCode = "";
        cityName_CHN = "";
        cityName_ENG = "";
        countyCode = "";
        countyName_CHN = "";
        streetCode = "";
        streetName1_CHN = "";
        streetName2_CHN = "";
        streetName3_CHN = "";
        streetName1_ENG = "";
        streetName2_ENG = "";
        streetName3_ENG = "";
        zipCode_ENG = "";
        zipCode_CHN = "";
        currAreaCode = "";
    }

    /**國家代碼*/
    public String countryCode;
    /**
     * 國家中文姓名，國家為TW、CN時必填
     * */
    public String countryName_CHN;
    /**
     國家英文姓名，為TW、CN以外的國家時必填
     */
    public String countryName_ENG;

    /**城市代碼，國家為TW、CN時必填*/
    public String cityCode;
    /**城市中文姓名，國家為TW、CN時必填*/
    public String cityName_CHN;
    /**城市中文姓名，國家為TW、CN以外時必填*/
    public String cityName_ENG;

    /**鄉鎮代碼，國家為TW時必填*/
    public String countyCode;
    /**
     鄉鎮名稱，國家為TW、CN時必填
     */
    public String countyName_CHN;

    /**路名代碼，國家為TW時必填*/
    public String streetCode;
    /**路名名稱，國家為TW、CN時必填*/
    public String streetName1_CHN;
    /**門牌號碼，國家為TW、CN時選填*/
    public String streetName2_CHN;
    /**路名額外資訊，國家為TW、CN時選填*/
    public String streetName3_CHN;

    /**路名名稱，為TW、CN以外的國家時必填*/
    public String streetName1_ENG;
    /**門牌號碼，為TW、CN以外的國家時選填*/
    public String streetName2_ENG;
    /**路名額外資訊，為TW、CN以外的國家時選填*/
    public String streetName3_ENG;

    /**郵遞區號，國家為TW、CN時必填*/
    public String zipCode_ENG;
    /**郵遞區號，為TW、CN以外的國家必填*/
    public String zipCode_CHN;

    /**鄰近城市代碼*/
    public String currAreaCode;

}
