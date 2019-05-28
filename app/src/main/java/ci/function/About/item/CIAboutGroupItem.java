package ci.function.About.item;

import java.util.ArrayList;

/**
 * Created by jlchen on 2016/4/7.
 */
public class CIAboutGroupItem {

    private String m_strTitle;
    private ArrayList<CIAboutChildItem> m_alChild = new ArrayList<>();

    public CIAboutGroupItem (String strTitle){
        this.m_strTitle = strTitle;
    }

    public void addChildItem(int iChildId, String strText, String strCall, int iIconId) {
        m_alChild.add(new CIAboutChildItem(iChildId, strText, strCall, iIconId));
    }

    public void resetText(String strTitle, String[] strText){
        this.m_strTitle = strTitle;

        if ( null == m_alChild || 0 == m_alChild.size() )
            return;

        for (int i = 0; i < m_alChild.size(); i ++){
            m_alChild.get(i).setText(strText[i]);
        }
    }

    public String getTitle(){
        return m_strTitle;
    }

    public ArrayList<CIAboutChildItem> getChildList(){
        return m_alChild;
    }
}
