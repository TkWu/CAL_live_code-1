package ci.function.About.item;

/**
 * Created by kevincheng on 2016/4/11.
 */
public class CIAPPItem {
    public String m_strAppName;
    public String m_strAppPackageName;
    public String m_strSummary;
    public byte[] m_byteArrayImg;

    public CIAPPItem(String strAppName,
                     String strAppPackageName,
                     String strSummary,
                     byte[] byteArrayImg){
        m_strAppName = strAppName;
        m_strAppPackageName = strAppPackageName;
        m_strSummary = strSummary;
        m_byteArrayImg = byteArrayImg;
    }
}
