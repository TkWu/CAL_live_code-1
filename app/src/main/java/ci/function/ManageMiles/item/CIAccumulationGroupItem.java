package ci.function.ManageMiles.item;

import java.util.ArrayList;

/**
 * Created by jlchen on 2016/3/9.
 */
public class CIAccumulationGroupItem {

    //以年份分組
    String m_strYear;
    //該年的所有里程記錄
    ArrayList<CIAccumulationItem> m_alInfo = new ArrayList<>();

    public CIAccumulationGroupItem(String strYear) {
        this.m_strYear = strYear;
    }

    public void addInfo(CIAccumulationItem item){
        this.m_alInfo.add(item);
    }

    public String GetYear() {
        return m_strYear;
    }

    public ArrayList<CIAccumulationItem> GetAccumulationList() {
        return m_alInfo;
    }

}
