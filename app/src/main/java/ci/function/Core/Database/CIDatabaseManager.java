package ci.function.Core.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.chinaairlines.mobile30.BuildConfig;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Set;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ui.object.CIModelInfo;
import ci.ws.Models.entities.CIFlightStationBookTicketODEntity;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIFlightStationStatusODEntity;
import ci.ws.Models.entities.CIFlightStationTimeTableODEntity;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.ws.Models.entities.CIInquiryBoardPassDBEntity;
import ci.ws.Models.entities.CIInquiryCouponDBEntity;
import ci.ws.Models.entities.CIInquiryExtraServicesDBEntity;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CINationalEntity;
import ci.ws.Models.entities.CIPendingQuestionnaireEntity;
import ci.ws.Models.entities.CIYouTubeDataEntity;

/**
 * Created by Kevin Cheng on 2016/3/5.
 */
public class CIDatabaseManager
        extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "cal.db";
    private static final int    DATABASE_VERSION = 7;

    public CIDatabaseManager(Context context) {
        super(context,
              DATABASE_NAME,
              null,
              DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase,
                         ConnectionSource connectionSource) {
        try {
           SLog.e(CIDatabaseManager.class.getName(), "onCreate");
            /**建立資料表*/
            TableUtils.createTable(connectionSource, CIFlightStationBookTicketODEntity.class);
            TableUtils.createTable(connectionSource, CIFlightStationTimeTableODEntity.class);
            TableUtils.createTable(connectionSource, CIFlightStationStatusODEntity.class);
            TableUtils.createTable(connectionSource, CIFlightStationEntity.class);
            TableUtils.createTable(connectionSource, CINationalEntity.class);
            TableUtils.createTable(connectionSource, CIGlobalServiceEntity.class);
            TableUtils.createTable(connectionSource, CIYouTubeDataEntity.class);
            TableUtils.createTable(connectionSource, CIInquiryBoardPassDBEntity.class);
            TableUtils.createTable(connectionSource, CIInquiryExtraServicesDBEntity.class);
            TableUtils.createTable(connectionSource, CIInquiryCouponDBEntity.class);
            TableUtils.createTable(connectionSource, CIPendingQuestionnaireEntity.class);
        } catch (SQLException e) {
           SLog.e(CIDatabaseManager.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          ConnectionSource connectionSource,
                          int oldVersion,
                          int newVersion) {
        try{
            switch (oldVersion){
                case 1:
                    //全球營業所欄位變動，重新創建資料表 by Kevin
                    TableUtils.dropTable(connectionSource, CIGlobalServiceEntity.class, true);
                    TableUtils.createTable(connectionSource, CIGlobalServiceEntity.class);
                case 2:
                    //建立用來儲存待上傳的問卷調查資料表
                    TableUtils.createTable(connectionSource, CIPendingQuestionnaireEntity.class);
                case 3:
                    //因欄位異動，刪除重建用來儲存待上傳的問卷調查資料表
                    TableUtils.dropTable(connectionSource, CIPendingQuestionnaireEntity.class, true);
                    TableUtils.createTable(connectionSource, CIPendingQuestionnaireEntity.class);
                case 4:
                    //因資安問題，刪除所有PNRStatus資料表及CIModelInfo相關資料
                    Context context = CIApplication.getContext();
                    CIModelInfo modelInfo = new CIModelInfo(context);
                    Set<String> tableNames;
                    try{
                        tableNames = modelInfo.GetAllDatabaseTableName();
                    }catch (Exception e){
                        tableNames = modelInfo.GetAllDatabaseTableNameForOldVersion();
                    }
                    dropDatabaseTable(CIInquiryTripEntity.class, tableNames);
                    modelInfo.clear();
                case 5:
                    TableUtils.dropTable(connectionSource, CINationalEntity.class, true);
                    TableUtils.createTable(connectionSource, CINationalEntity.class);
                    break;
                    //NOTE: 此switch區段中只能放置ㄧ個break，而且一定要在最後;
            }

            if(BuildConfig.isLoggable){
               SLog.d(getClass().getSimpleName(), "onUpgrade() \n OldVersion:" + oldVersion + "\nNewVersion:" + newVersion);
            }
        } catch (SQLException e) {
           SLog.e(getClass().getSimpleName(), "onUpgrade() failed\n OldVersion:" + oldVersion + "\nNewVersion:" + newVersion);
            throw new RuntimeException("onUpgrade() failed\n OldVersion:" + oldVersion + "\nNewVersion:" + newVersion + "\n" + e);
        }


        //處理DB更新版本 , 修正ＤＢ請留詳細註解(且須與前版本相容)
//        try {
//            switch (oldVersion) {
//                case 1:
//                    //增加 ApiQueue 欄位 groupId Modify by ShihMin 20150810
//                    getDao(SQueueEntity.class).executeRaw("ALTER TABLE `ApiQueue` ADD COLUMN `groupId` INTEGER;");
//                case 2:
//                    // SCustomerEntity 型別修改/欄位新增,但是為 Online Data,直接重建table
//                    TableUtils.dropTable(connectionSource, SCustomerEntity.class, true);
//                    TableUtils.createTable(connectionSource,
//                            SCustomerEntity.class);
//
//                    // SContactEntity 欄位更名,但是為 Online Data,直接重建table
//                    TableUtils.dropTable(connectionSource, SContactEntity.class, true);
//                    TableUtils.createTable(connectionSource, SContactEntity.class);
//                case 3:
//                    alterDatabaseTable(CIInquiryTripEntity.class,  "ADD COLUMN `Test` INTEGER;", getAllDatabaseTableName() );
//                    break;
//                    NOTE: 此switch區段中只能放置ㄧ個break，而且一定要在最後;
//            }
//
//        } catch (SQLException e) {
//            Log.e(getClass().getSimpleName(), "onUpgrade() failed\n OldVersion:" + oldVersion + "\nNewVersion:" + newVersion);
//            throw new RuntimeException("onUpgrade() failed\n OldVersion:" + oldVersion + "\nNewVersion:" + newVersion + "\n" + e);
//        }
    }

    /**
     * Get table name by data class
     *
     * @param dataClass dao
     * @return tableName
     */
    public static String getDaoTableName(@NonNull Class<?> dataClass) {
        DatabaseTable annotation = dataClass.getAnnotation(DatabaseTable.class);
        String tableName;

        if (annotation != null) {
            tableName = dataClass.getAnnotation(DatabaseTable.class).tableName();
            if (tableName == null || tableName.length() <= 0) {
                tableName = dataClass.getSimpleName();
            }
        } else {
            tableName = dataClass.getSimpleName();
        }

        return tableName;
    }

    /**
     * 初始化Dao，
     * @return Dao
     */
    private DatabaseObjects init(Class clazz, String tableName){
        Dao<CIInquiryTripEntity,String> dao;
        DatabaseTableConfig tableConfig = new DatabaseTableConfig();
        tableConfig.setDataClass(clazz);

        tableConfig.setTableName(tableName);
        try {
            dao = DaoManager.createDao(getConnectionSource(), tableConfig);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        //防止以Dao鎖定著CIInquiryTripEntity.class，導致創建資料表錯誤
        DaoManager.unregisterDao(getConnectionSource(), dao);
        DatabaseObjects databaseObjects = new DatabaseObjects();
        databaseObjects.dao = dao;
        databaseObjects.tableConfig = tableConfig;
        return databaseObjects;
    }

    private class DatabaseObjects{
        DatabaseTableConfig tableConfig;
        Dao dao;
    }



    /**
     * 修改單一資料表結構欄位
     * @param clazz         資料表結構類別
     * @param sqlStatements SQLite語法
     * @throws SQLException
     */
    private void alterDatabaseTable(Class clazz, String sqlStatements) throws SQLException{
        alterDatabaseTable(clazz, sqlStatements, null);
    }

    /**
     * 修改資料表結構欄位，可大量修改相同類別的資料表
     * @param clazz         資料表結構類別
     * @param sqlStatements SQLite語法
     * @param set           資料表名稱(Set結構)
     * @throws SQLException
     */
    private void alterDatabaseTable( Class clazz, String sqlStatements, Set<String> set) throws SQLException{
        if(null != set && 0 < set.size()){
            for(String tableName:set){
                Dao dao = init(clazz, tableName).dao;
                if(null != dao && true == dao.isTableExists()){
                    dao.executeRaw("ALTER TABLE `" + tableName + "` " + sqlStatements);
                }
            }
        } else {
            getDao(clazz).executeRaw("ALTER TABLE `" + getDaoTableName(clazz) + "` " + sqlStatements);
        }
    }

    /**
     * 刪除資料表，可大量處理
     * @param clazz         資料表結構類別
     * @param set           資料表名稱(Set結構)
     * @throws SQLException
     */
    private void dropDatabaseTable( Class clazz, Set<String> set) throws SQLException{
        if(null != set && 0 < set.size()){
            for(String tableName:set){
                DatabaseTableConfig tableConfig = init(clazz, tableName).tableConfig;
                if(null != tableConfig){
                    TableUtils.dropTable(getConnectionSource(), tableConfig, true);
                }
            }
        } else {
            TableUtils.dropTable(getConnectionSource(), clazz, true);
        }
    }

    /**
     * 取得儲存於sharePreference全部CIInquiryTripEntity結構的資料表名稱清單
     * @return 資料表名稱
     */
    private Set<String> getAllDatabaseTableName(){
        CIModelInfo info = new CIModelInfo(CIApplication.getContext());
        return info.GetAllDatabaseTableName();
    }
}
