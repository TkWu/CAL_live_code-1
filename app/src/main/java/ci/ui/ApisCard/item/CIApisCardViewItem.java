package ci.ui.ApisCard.item;

/**
 * Created by jlchen on 2016/3/21.
 */
public class CIApisCardViewItem {
    String m_strHead;
    String m_strBody;

    public CIApisCardViewItem(String strHead, String strBody) {
        this.m_strHead = strHead;
        this.m_strBody = strBody;
    }

    public String GetHeadText(){
        return m_strHead;
    }

    public String GetBodyText(){
        return m_strBody;
    }
}
