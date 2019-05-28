package com.chinaairlines.mobile30.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;
import junit.framework.Assert;
import java.util.concurrent.TimeUnit;
import ci.function.Core.CIApplication;
import ci.ws.Models.CIAuthWSModel;
import ci.ws.cores.CIWSShareManager;

/**
 * Created by KevinCheng on 2016/5/23.
 */
public class CIAuthWSModelTest extends SBaseAndroidTestCase {
    /**
     * 功能：驗證 取得使用API時，Header所需的授權碼，並回存到CIWSShareManager
     */

    private SharedPreferences m_sharedPreferences;
    private static final String DEF_VALID_TIME  = "599";
    private int                 m_count         = 0;
    /**
     *  案例說明：有網路狀態、使用doAuthConnection()取得授權碼
     *  輸入參數：無
     *  斷言結果：
     *  (1)onAuthSuccess被回呼
     *  (2)strAuth、 expires_in 必須不為null，且長度大於零，expires_in 必須等於 "599"
     *  (3)最後存在 sharedPreferences 的資料應不等於更新後的資料
     *  (4)expires_in 有效時間應被轉換成整數並且被乘1000存入 sharedPreferences
     *  (5)驗證存入的系統時間(誤差應在一秒)
     */
    public void testCase01() throws Exception{
        m_sharedPreferences = CIApplication.getContext().getSharedPreferences( CIWSShareManager.WS_SHARE, Context.MODE_PRIVATE );
        //取得WS並更新前的授權碼
        final String lastAuth   = m_sharedPreferences.getString(CIWSShareManager.KEY_AUTH,"");
        final long   lastTime   = m_sharedPreferences.getLong(CIWSShareManager.KEY_TIME,0);

        setWifiEnabled(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new CIAuthWSModel().doAuthConnection(new CIAuthWSModel.AuthCallback() {
                    @Override
                    public void onAuthSuccess(String strAuth, String expires_in) {
                        //取得更新後的授權碼
                        String auth     = m_sharedPreferences.getString(CIWSShareManager.KEY_AUTH,"");
                        long time       = m_sharedPreferences.getLong(CIWSShareManager.KEY_TIME,0);
                        long validTime  = m_sharedPreferences.getLong(CIWSShareManager.KEY_VALID_TIME,0);

                        //驗證回傳的授權碼不為空字串或null，且必須等於存入
                        Assert.assertTrue(false == TextUtils.isEmpty(strAuth) && auth.equals(strAuth));

                        //驗證授權碼會被更新，所以更新前最後的授權碼應不等於取得授權碼之後
                        Assert.assertTrue(false == lastAuth.equals(auth));

                        //驗證expires_in不為空字串或null，且值為599
                        Assert.assertTrue(false == TextUtils.isEmpty(expires_in) && expires_in.equals(DEF_VALID_TIME) );

                        //被存入的有效時間被轉換成長整數，故驗證
                        Assert.assertTrue(Integer.parseInt(expires_in) * 1000 == validTime && validTime == 599000);

                        //取得現在時間
                        long nowTime = System.currentTimeMillis();

                        //存入sharedPreferences的時間和現在取得的誤差應該在一秒之間
                        Assert.assertTrue(time < nowTime && time > nowTime - 1000);

                        //驗證時間會被更新，所以更新前最後的時間應不等於取得之後
                        Assert.assertTrue(nowTime != lastTime);

                        //計數器加一
                        m_count++;

                        //倒數歸零啟動 unit-test thread
                        countDown();
                    }

                    @Override
                    public void onAuthError(String rt_code, String rt_msg, Exception exception) {
                        //如果呼叫這個callback則斷言失敗
                        fail();
                    }
                });
            }
        }).start();
        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);
        //倒數次數必需為一，才等同Success有被呼叫到
        Assert.assertEquals(1, m_count);
    }

    /**
     *  案例說明：無網路狀態、使用doAuthConnection()取得授權碼
     *  輸入參數：無
     *  斷言結果：
     *  (1)onAuthError被回呼
     *  (2)onAuthError所帶參數皆需有值(因變動性高，不驗證固定值)
     */
    public void testCase02() throws Exception{
        setWifiEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new CIAuthWSModel().doAuthConnection(new CIAuthWSModel.AuthCallback() {
                    @Override
                    public void onAuthSuccess(String strAuth, String expires_in) {
                        //如果呼叫這個callback則斷言失敗
                        fail();
                    }

                    @Override
                    public void onAuthError(String rt_code, String rt_msg, Exception exception) {

                        //驗證參數需皆有值帶回來
                        Assert.assertTrue(false == TextUtils.isEmpty(rt_code));
                        Assert.assertTrue(false == TextUtils.isEmpty(rt_msg));
                        Assert.assertTrue(null != exception);

                        //計數器加一
                        m_count++;

                        //倒數歸零啟動 unit-test thread
                        countDown();
                    }
                });
            }
        }).start();
        //停止unit-test thread，等待callback
        await(10, TimeUnit.SECONDS);
        //倒數次數必需為一，才等同Success有被呼叫到
        Assert.assertEquals(1, m_count);
    }

}
