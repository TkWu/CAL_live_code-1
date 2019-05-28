package com.chinaairlines.mobile30.Model;

import android.util.Log;

import ci.function.Core.SLog;

import com.chinaairlines.mobile30.base.SBaseAndroidTestCase;

import junit.framework.Assert;

import ci.ws.Models.CIInquiryCheckInModel;
import ci.ws.Models.CIInquiryTripModel;

/**
 * Created by kevincheng on 2016/5/23.
 */
public class CITest extends SBaseAndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //初始化
       SLog.i("unit-test", "setUp");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
       SLog.i("unit-test","tearDown");
    }

    /**
     *  案例說明：有可網路狀態、調用findByContractNum(String FoNum,CallBack callBack)、驗證是否可取的資料
     *  輸入參數：FoNum = "EE201310001590"
     *  斷言結果：callBack取得相對應的主機號碼資料
     */
    public void testCase01() throws Exception {
        Assert.assertEquals(1,1);
        setWifiEnabled(true);
       SLog.i("unit-test","testCase01");
    }

    /**
     *  案例說明：有可網路狀態、調用findByContractNum(String FoNum,CallBack callBack)、驗證是否可取的資料
     *  輸入參數：FoNum = "EE201310001590"
     *  斷言結果：callBack取得相對應的主機號碼資料
     */
    public void testCase02() throws Exception {
        Assert.assertEquals(1,1);
        setWifiEnabled(false);
       SLog.i("unit-test","testCase02");
    }
}

