package ci.function.About.item;

/**
 * Created by jlchen on 2016/4/7.
 */
public class CIAboutChildItem {

    //聯繫我們-服務電話
    public static final int DEF_ABOUT_CHILD_ITEM_CALL   = 100811;
    //聯繫我們-全球客服
    public static final int DEF_ABOUT_CHILD_ITEM_GLOBAL = 100812;
    //聯繫我們-fb即時通訊
    public static final int DEF_ABOUT_CHILD_ITEM_FB     = 100813;
    //聯繫我們-ai 智能客服
    public static final int DEF_ABOUT_CHILD_ITEM_AI     = 100814;
    //華航簡介-關於華航
    public static final int DEF_ABOUT_CHILD_ITEM_CI     = 100821;
    //華航簡介-其他華航app
    public static final int DEF_ABOUT_CHILD_ITEM_APP    = 100822;
    //華航簡介-天合聯盟航網地圖
    public static final int DEF_ABOUT_CHILD_ITEM_SKY    = 100831;

    private int     m_iChildId;
    private String  m_strCall;
    private String  m_strText;
    private int     m_iIconImgId;

    public CIAboutChildItem(int iChildId, String strText, String strCall, int iIconId){
        this.m_iChildId     = iChildId;
        this.m_strText      = strText;
        this.m_strCall      = strCall;
        this.m_iIconImgId   = iIconId;
    }

    public int getChildId(){
        return m_iChildId;
    }

    public String getCallNumber(){
        return m_strCall;
    }

    public String getText(){
        return m_strText;
    }

    public int getIconId(){
        return m_iIconImgId;
    }

    public void setCallNumber(String number){
        m_strCall = number;
    }

    public void setText(String text){
        m_strText = text;
    }
}
