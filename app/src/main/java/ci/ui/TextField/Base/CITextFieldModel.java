package ci.ui.TextField.Base;

import ci.function.Core.SLog;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kevin on 2016/3/24.
 */
public class CITextFieldModel {
    private        int                               m_iAutoGen = 0;
    private static CITextFieldModel s_this     = null;
    private        Map<Integer, CITextFieldFragment> m_map = null;
    private final int                                MAX_ID     = 10000;

    private CITextFieldModel() {
        m_map = new LinkedHashMap<>();
    }

    public static CITextFieldModel getInstance() {
        if (null == s_this) {
            s_this = new CITextFieldModel();
        }
       return s_this;
    }
    public void addFragment(CITextFieldFragment fragment) {
        m_iAutoGen++;
        if(null != m_map.get(m_iAutoGen)){
            m_map.remove(m_map.get(m_iAutoGen));
        }
        m_map.put(m_iAutoGen, fragment);
       SLog.d("m_iAutoGen", m_iAutoGen + "");
        fragment.setFragmentId(m_iAutoGen);
        if(MAX_ID <= m_iAutoGen){
            m_iAutoGen = 0;
        }
    }

    public CITextFieldFragment getFragment(int id){
        return m_map.get(id);
    }

    public void clear(){
        m_iAutoGen = 0;
        m_map.clear();
    }
}
