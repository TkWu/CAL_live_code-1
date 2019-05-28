package ci.ui.object;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Core.Encryption;
import ci.ui.define.UiMessageDef;
import ci.ws.Models.entities.CIInquiryTripEntity;

/**
 * Created by Ryan on 16/5/26.
 * 將原在InquiryTripModel處理NR儲存資料移動至此, 集中處理PNR 邏輯的Code
 */
public class CIPNRStatusModel {


    private Dao<CIInquiryTripEntity,String> m_dao = null;

    private static ConnectionSource m_connectionSource = CIApplication.getDbManager().getConnectionSource();

    private DatabaseTableConfig m_tableConfig = null;

    public CIPNRStatusModel(){}

    /**
     * 初始化Dao
     * 根據目前是否登入以及登入帳號的卡號決定資料表的名稱，以便在未登入時使用公用資料表存取
     * 以及登入時以卡號為資料表名稱區分不同帳號來存取資料。
     * 公用資料表名稱：PNRTable
     * 帳號資料表名稱：PNRTable_CardNo
     * 因為此資料表名稱有個人敏感資料，所以需要編碼成MD5
     */
    private void initDao(){
        final String prefix     = "PNRTable";
        final String CardNo     = CIApplication.getLoginInfo().GetUserMemberCardNo();
        final String LoginType  = CIApplication.getLoginInfo().GetLoginType();
        final boolean isLogin   = CIApplication.getLoginInfo().GetLoginStatus();
        m_tableConfig = new DatabaseTableConfig();
        m_tableConfig.setDataClass(CIInquiryTripEntity.class);
        Encryption encryption = Encryption.getInstance();
        if(true ==  isLogin && false == TextUtils.isEmpty(CardNo)
                && UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER.equals(LoginType)){
            m_tableConfig.setTableName(encryption.MD5(prefix + "_" + CardNo));
        } else {
            m_tableConfig.setTableName(encryption.MD5(prefix));
        }

        try {
            m_dao = DaoManager.createDao(m_connectionSource, m_tableConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //防止以Dao鎖定著CIInquiryTripEntity.class，導致創建資料表錯誤
        DaoManager.unregisterDao(m_connectionSource, m_dao);
    }

    /**
     * 檢查資料表是否存在
     * @return  if false 就是不存在
     */
    private Boolean isTableExists(){
        initDao();
        try {
            return m_dao.isTableExists();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清空資料表所有資料
     * */
    public void Clear(){
        initDao();
        try {
            TableUtils.clearTable(m_connectionSource, m_tableConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 創建資料表
     */
    private void CreateTable(boolean isInitDao){
        if(true == isInitDao){
            initDao();
        }
        CIModelInfo info = new CIModelInfo(CIApplication.getContext());
        try {
            TableUtils.createTableIfNotExists(m_connectionSource, m_tableConfig);
            info.addDatabaseTableName(m_tableConfig.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果資料已經存在就更新資料(以PNR為主key)
     * @param datas CIInquiryTripEntity
     */
    public void insertOrUpdate(CIInquiryTripEntity datas) {
        initDao();

        if(false == isTableExists()){
            CreateTable(false);
        }

        try {
            m_dao.createOrUpdate(datas);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 搜尋全部資料
     * @return List<CIInquiryTripEntity>
     */
    public List<CIInquiryTripEntity> findData(){
        List<CIInquiryTripEntity> datas;
        initDao();
        try {
            if(false == m_dao.isTableExists()){
                /*如果沒有資料表會創建，剛創建的資料表一定沒資料，
                * 所以返回null
                * */
                CreateTable(false);
                return null;
            }
            datas = m_dao.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        if(null == datas || datas.size() <= 0){
            return null;
        }

        return datas;
    }

    /**
     * 搜尋指定的PNR行程
     * @param pnr 指定PNR ID
     * @return List<CIInquiryTripEntity>
     */
    public CIInquiryTripEntity findTripDataByPNR(String pnr){
        CIInquiryTripEntity data;
        initDao();

        try {
            if(false == m_dao.isTableExists()){
                /*如果沒有資料表會創建，剛創建的資料表一定沒資料，
                * 所以返回null
                * */
                CreateTable(false);
                return null;
            }
            data = m_dao.queryForId(pnr);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return data;
    }

    /**
     * 僅搜尋可以顯示在首頁的行程
     * @return List<CIInquiryTripEntity>
     */
    public List<CIInquiryTripEntity> findDataForOnlyVisibleAtHome(){
        List<CIInquiryTripEntity> datas;
        initDao();

        try {
            if(false == m_dao.isTableExists()){
                /*如果沒有資料表會創建，剛創建的資料表一定沒資料，
                * 所以返回null
                * */
                CreateTable(false);
                return null;
            }
            datas = m_dao.queryForEq("isVisibleAtHome", true);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        if(null == datas || datas.size() <= 0){
            return null;
        }
        return datas;
    }

    /**
     * 更新某筆PNR_Id的是否於主頁顯示的狀態
     * @return  更新是否成功
     */
    public boolean UpdatePNRisVisibleAtHome( String strPNR_Id, Boolean bVisible ){
        initDao();

        try {
            if(false == m_dao.isTableExists()){
                /*如果沒有資料表會創建，剛創建的資料表一定沒資料，
                * 所以返回null
                * */
                CreateTable(false);
                return false;
            }
            if(true == m_dao.idExists(strPNR_Id)){//是否有此筆資料
                CIInquiryTripEntity data = m_dao.queryForId(strPNR_Id);
                data.isVisibleAtHome = bVisible;
                int row = m_dao.update(data);
                if(row == 1){//如果被更新的筆數是1，就是有被正確更新
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新全部PNR資料不顯示於首頁
     * @return  更新是否成功
     */
    public boolean UpdateAllPNRisVisibleToFalse(){
        initDao();

        try {
            if(false == m_dao.isTableExists()){
                /*如果沒有資料表會創建，剛創建的資料表一定沒資料，
                * 所以返回null
                * */
                CreateTable(false);
                return false;
            }

            List<CIInquiryTripEntity> datas;
            datas = m_dao.queryForAll();
            if(null != datas && 0 < datas.size()){
                for(CIInquiryTripEntity data:datas){
                    data.isVisibleAtHome = false;
                    m_dao.createOrUpdate(data);
                }
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 刪除指定的PNR行程
     * @param pnrs 指定PNR ID list
     */
    public int deleteTripDataByPNR(ArrayList<String> pnrs){

        int iResult = 0;
        initDao();

        try {
            if(false == m_dao.isTableExists()){
                /*如果沒有資料表會創建，剛創建的資料表一定沒資料，
                 * 所以返回null
                 * */
                CreateTable(false);
                return iResult;
            }
            iResult = m_dao.deleteIds(pnrs);
        } catch (SQLException e){
            e.printStackTrace();
            return iResult;
        }

        return iResult;
    }
}
