package com.chinaairlines.mobile30.Model;

import android.text.TextUtils;

import com.chinaairlines.mobile30.Config;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIInquiryCouponInfoModel;
import ci.ws.Models.entities.CICouponRespList;
import ci.ws.Models.entities.CIInquiryCouponDBEntity;
import ci.ws.Models.entities.CIInquiryCouponResp;
import ci.ws.Models.entities.CIInquiryCoupon_Info;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;

/**
 * Created by jlchen on 2016/8/19.
 */
public class CIInquiryCouponInfoWSModelTest extends SBaseAndroidTestCase {

    /**
     * 功能：查詢優惠券資料。
     * (1)向API請求資料後，依據結果調用正確之callback。
     * (2)若向API請求資料成功，則將資料存入DB，於無網路時可先讀取DB已存資料。
     */

    /**
     * 向API請求資料-> 是否請求資料成功
     * (1)成功：調用callback的 onSuccess 返回資料給presenter
     * (2)失敗：調用callback的 onError 返回錯誤訊息給presenter
     *
     * 影響請求資料是否成功的事件
     * (1)有無網路
     * */

    /**
     * 存取優惠券資料至DB-> 是否存取成功
     * (1)成功：返回true
     * (2)失敗：返回false
     *
     * 影響存取資料是否成功的事件
     * (1)存入資料格式不正確
     *
     *
     * 於DB取出已存取的優惠券資料-> 是否取得成功
     * (1)成功：返回ArrayList<CIInquiryCoupon_Info> data
     * (2)失敗：返回null
     *
     * 影響讀取資料是否成功的事件
     * (1)存入資料格式不正確, 造成解析失敗
     * (2)資料表尚未建立
     * (3)資料尚未存入於DB
     * */

    //計數及順序的斷言用途
    private int m_iCount = 0;

    /**
     *  開始測試前執行（預先設置測試所需的測試資料）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     *  測試完成後執行（用於移除測試資料）
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * 案例說明：有網路狀態，使用sendReqDataToWS()向WS請求優惠券資料成功
     * 輸入參數：無
     * 斷言結果：回呼onSuccess(), rt_code必為000, resp不為null, 並驗證回傳結果是否正確
     */
    public void testCase1_1() throws Exception {

        //開啟網路
        setWifiEnabled(true);

        new CIInquiryCouponInfoModel(new CIInquiryCouponInfoModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas) {

                m_iCount++;

                //結果代碼為000，表示成功
                Assert.assertEquals( Config.WS_RESULT_CODE_SUCCESS, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                //傳回資料不可為null, 不然應調用onError
                Assert.assertNotNull( datas );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動unit-test thread
                countDown();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                //如果呼叫這個callback則斷言失敗
                fail();
            }
        }).sendReqDataToWS();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     * 案例說明：無網路狀態，使用sendReqDataToWS()向WS請求優惠券資料失敗
     * 輸入參數：無
     * 斷言結果：回呼onError, rt_code必為9999
     */
    public void testCase1_2() throws Exception {

        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        new CIInquiryCouponInfoModel(new CIInquiryCouponInfoModel.CallBack() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas) {

                fail();
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                m_iCount++;

                //結果代碼為9999，表示無網路連線
                Assert.assertEquals( CIWSResultCode.NO_INTERNET_CONNECTION, rt_code );

                //結果訊息不為空值
                Assert.assertFalse( TextUtils.isEmpty(rt_msg) );

                Assert.assertEquals(1, m_iCount);

                //倒數歸零 啟動 unit-test thread
                countDown();
            }
        }).sendReqDataToWS();

        //停止unit-test thread，等待callback
        await(60, TimeUnit.SECONDS);

        Assert.assertEquals(1, m_iCount);
    }

    /**
     *  案例說明：無網路狀態，使用saveDataToDB()，
     *          將優惠券資料(格式ArrayList<CICouponRespList>)存入DB成功
     *  輸入參數：entity.respResult = GsonTool.toJson(setDBData()); (優惠券資料)
     *  斷言結果：回傳true表示存入資料格式正確，並且存取成功
     */
    public void testCase2_1() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryCouponInfoModel model = new CIInquiryCouponInfoModel(null);

        CIInquiryCouponDBEntity entity = new CIInquiryCouponDBEntity();
        entity.respResult = GsonTool.toJson(setDBData());

        //驗證是否寫入資料成功
        Assert.assertTrue(model.saveDataToDB(entity));

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    /**
     *  案例說明：無網路狀態，使用saveDataToDB()將格式錯誤的資料存入DB
     *  輸入參數：entity.respResult = "123"
     *  斷言結果：回傳false表示存入錯誤資料
     */
    public void testCase2_2() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryCouponInfoModel model = new CIInquiryCouponInfoModel(null);

        CIInquiryCouponDBEntity entity = new CIInquiryCouponDBEntity();

        //驗證是否寫入資料失敗
        Assert.assertFalse(model.saveDataToDB(entity));

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    /**
     *  案例說明：無網路狀態，已saveDataToDB()成功，使用getDataFromDB()從DB取出優惠券資料成功
     *  輸入參數：無
     *  斷言結果：回傳優惠券資料，資料不可為null，且內容正確
     */
    public void testCase2_3() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        //預先將資料存入db
        ArrayList<CIInquiryCoupon_Info> alSaveData = setDBData();

        CIInquiryCouponDBEntity entity = new CIInquiryCouponDBEntity();
        entity.respResult = GsonTool.toJson(alSaveData);

        CIInquiryCouponInfoModel model = new CIInquiryCouponInfoModel(null);
        Assert.assertTrue(model.saveDataToDB(entity));

        //資料於db取出
        ArrayList<CIInquiryCoupon_Info> alDBData =  model.getDataFromDB();

        //驗證資料內容是否一致
        //Assert.assertEquals(alDBData, alSaveData);
        //Assert.assertTrue(alDBData.equals(alSaveData));
        Assert.assertEquals(alDBData.get(0).ExcludedItem, alSaveData.get(0).ExcludedItem);
        Assert.assertEquals(alDBData.get(0).ExpiryDate, alSaveData.get(0).ExpiryDate);
        Assert.assertEquals(alDBData.get(0).Title, alSaveData.get(0).Title);
        Assert.assertEquals(alDBData.get(0).BarCodeImage, alSaveData.get(0).BarCodeImage);
        Assert.assertEquals(alDBData.get(0).InformationImage, alSaveData.get(0).InformationImage);
        Assert.assertEquals(alDBData.get(0).Discont, alSaveData.get(0).Discont);
        Assert.assertEquals(alDBData.get(0).DiscontUnit, alSaveData.get(0).DiscontUnit);

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    /**
     *  案例說明：無網路狀態，saveDataToDB()回傳false時，使用getDataFromDB()，從DB取出優惠券資料失敗
     *  輸入參數：無
     *  斷言結果：回傳資料為null
     */
    public void testCase2_4() throws Exception{
        //關閉網路
        setWifiEnabled(false);

        //初始化倒數啟動執行緒
        init(1);

        CIInquiryCouponInfoModel model = new CIInquiryCouponInfoModel(null);

        CIInquiryCouponDBEntity entity = new CIInquiryCouponDBEntity();

        //驗證是否寫入資料失敗
        Assert.assertFalse(model.saveDataToDB(entity));

        //驗證取得優惠券資料失敗
        Assert.assertNull(model.getDataFromDB());

        //測試完畢需清空測試資料
        clearDBData(entity);
    }

    //測試資料
    private CICouponRespList setDBData(){

        CIInquiryCoupon_Info couponInfo = new CIInquiryCoupon_Info();
        couponInfo.ExcludedItem = " ";
        couponInfo.ExpiryDate = "2016-09-30";
        couponInfo.Title = "Duty Free Goods Discount";
        couponInfo.BarCodeImage = "iVBORw0KGgoAAAANSUhEUgAAAKwAAAA6CAIAAACibCXRAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAAlwSFlzAAASdAAAEnQB3mYfeAAAAcNJREFUeF7t3dsOgjAQRVH9/49GDQoFS0sGL0CXDyZeSsJ0mNPZp+i167qLR+MRuCeBR+MRuDR+/k7/IQWiIAKVJLhrZRqj/mUqoNmXS28Oh3o/Qj9k9lz4/nCEBgemYVk5HeVElwST5DtE2kmCfClqqoRIAkkwlvb16kwOnuuHwvJlKCTkIJMuFoZLV9sfBYgckANysNCj/vG6fC+Ws/b1492sSqASqAQqQbJy0x1M1vzkoA9HldVme0XEEDGsGUhaRC3i8yoJLEezFhED6SMmWWA6EEPEsJwD5GBqYcPGsPGkBf828wlTJnIAFoFFYBFYtMOGLVzVwwPJATkgB+SAHJCDtIVjIDGQRtej2s0ihoghYvi6TGw0XcoFVjIrmXfAO6jekGo/wQ77EbAILAKLwCKwaIfFOWwBhAeSA3JADsgBOSAHvIPS/RXpRt5DbBW0Jhi3pM9uHmpnLiWBJMjc01E1A8PsjovIReQichErOcBAYiAxkGbL0kO0FYghYogYIoaIIWKIGCKGwd8bxwlwApwAJ8AJVrLYMMn//UAtohbx5y1irZD4/AwR8B9IZ5jFjecgCTYG8AzDJcEZZnHjOdwARxwbXJc7J68AAAAASUVORK5CYII=";
        couponInfo.InformationImage = "iVBORw0KGgoAAAANSUhEUgAAAKwAAAA6CAIAAACibCXRAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAAlwSFlzAAASdAAAEnQB3mYfeAAAAcNJREFUeF7t3dsOgjAQRVH9/49GDQoFS0sGL0CXDyZeSsJ0mNPZp+i167qLR+MRuCeBR+MRuDR+/k7/IQWiIAKVJLhrZRqj/mUqoNmXS28Oh3o/Qj9k9lz4/nCEBgemYVk5HeVElwST5DtE2kmCfClqqoRIAkkwlvb16kwOnuuHwvJlKCTkIJMuFoZLV9sfBYgckANysNCj/vG6fC+Ws/b1492sSqASqAQqQbJy0x1M1vzkoA9HldVme0XEEDGsGUhaRC3i8yoJLEezFhED6SMmWWA6EEPEsJwD5GBqYcPGsPGkBf828wlTJnIAFoFFYBFYtMOGLVzVwwPJATkgB+SAHJCDtIVjIDGQRtej2s0ihoghYvi6TGw0XcoFVjIrmXfAO6jekGo/wQ77EbAILAKLwCKwaIfFOWwBhAeSA3JADsgBOSAHvIPS/RXpRt5DbBW0Jhi3pM9uHmpnLiWBJMjc01E1A8PsjovIReQichErOcBAYiAxkGbL0kO0FYghYogYIoaIIWKIGCKGwd8bxwlwApwAJ8AJVrLYMMn//UAtohbx5y1irZD4/AwR8B9IZ5jFjecgCTYG8AzDJcEZZnHjOdwARxwbXJc7J68AAAAASUVORK5CYII=";
        couponInfo.Discont = "20";
        couponInfo.DiscontUnit = "%";

        CICouponRespList list = new CICouponRespList();
        list.add(couponInfo);

        return list;
    }

    //清空DB裡的測試資料
    private void clearDBData(CIInquiryCouponDBEntity entity){
        RuntimeExceptionDao<CIInquiryCouponDBEntity, Integer> dao
                = CIApplication.getDbManager().getRuntimeExceptionDao(CIInquiryCouponDBEntity.class);
        dao.delete(entity);
    }
}
