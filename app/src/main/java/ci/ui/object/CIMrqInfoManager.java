package ci.ui.object;

/**
 * Created by Ryan on 16/6/21.
 * 推播機制
 */
@Deprecated
public class CIMrqInfoManager {

//
//public class CIMrqInfoManager implements MrqSdkListener, MrqDataProvider {
//
//    /**第一次開啟App, 僅StatActivity 才會使用此參數*/
//    public static final String TAG_FIRST_LAUNCHED = "first_launch";
//    /**每一次送推播都帶此參數*/
//    public static final String TAG_LAUNCHED = "launched";
//
//
//    private static final String TAG = "MRQ Manager";
//    private static final String FILTER_TAG = "GCM ";
//    private static MrqSdk   m_mrqSdk;
//
//    private MrqUserContext m_MrqUserContext = null;
//
//    public void setMrqSDK( MrqSdk mrqsdk ){
//        m_mrqSdk = mrqsdk;
//    }
//
//    public MrqSdk getMrqSdk(){
//        return m_mrqSdk;
//    }
//
//    /**取得卡號*/
//    private String getCustomerId(){
//
//        String strCustomerId = "";
//        if ( CIApplication.getLoginInfo().GetLoginStatus() &&
//                TextUtils.equals(CIApplication.getLoginInfo().GetLoginType(), UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER) ){
//            strCustomerId = CIApplication.getLoginInfo().GetUserMemberCardNo();
//        }
//
//        return strCustomerId;
//    }
//
//    /**設定卡號, 有登入帶入登入帳號, 未登入帶空字串
//     * @param customerId 帳號*/
//    public void setCustomerId(String customerId) {
//        if ( null != getMrqSdk() ){
//            getMrqSdk().setCustomerId(customerId);
//        }
//    }
//
//    /**設定Token 至 MRQ SDK*/
//    public void setGcmRegistrationId(String gcmRegistrationId) {
//        if ( null != getMrqSdk() ){
//            getMrqSdk().setGcmRegistrationId(gcmRegistrationId);
//        }
//    }
//
//    /**取得存在MRQ Server 的Token*/
//    public String getGcmRegistrationId() {
//        if ( null != getMrqSdk() ) {
//            return getMrqSdk().getGcmRegistrationId();
//        }
//        return "";
//    }
//
//    /**將註冊的Token, 送至 MRQ Server*/
//    public void sendRegistrationToServer(String token) {
//        setGcmRegistrationId(token);
//        try {
//           SLog.d(TAG, FILTER_TAG + getGcmRegistrationId().toString() );
//        }catch(Exception e){
//           SLog.d(TAG, FILTER_TAG + e.toString());
//        }
//    }
//
//    /**取得App 狀態設定*/
//    public Map<String, String> getProperties() {
//
//        Map<String, String> properties = new HashMap<>();
//
//        properties.put("languages", CIApplication.getLanguageInfo().getWSLanguage() );
//        String strSwitch = AppInfo.getInstance(CIApplication.getContext()).GetAppNotiflySwitch()? "on":"off";
//        properties.put("notificationSwitch",    strSwitch);
//        properties.put("promoteSwitch",         strSwitch);
//        properties.put("fltstatusSwitch",       strSwitch);
//        properties.put("newsSwitch",            strSwitch);
//
//        return properties;
//    }
//
//    public Map<String, Set<String>> getTags( String strlaunch ){
//
//        Map<String, Set<String>> tags = new HashMap<>();
//
//        String strPackageName = "";
//        try {
//            PackageInfo pinfo = CIApplication.getContext().getPackageManager().getPackageInfo(CIApplication.getContext().getPackageName(), 0);
//            if (null != pinfo)
//            {
//                strPackageName = pinfo.packageName;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        //String[] tagss = {"Android", strPackageName, strlaunch };
//        String[] tagss = {"Android", "JOURNEYV2", strPackageName, strlaunch, CIApplication.getVersionName() };
//
//        tags.put("tags", new HashSet<>(Arrays.asList(tagss)));
//
//        return tags;
//    }
//
//    public void setUserContext( MrqUserContext mrqUserContext ){
//        m_MrqUserContext = mrqUserContext;
//    }
//
//    @Override
//    public MrqUserContext currentUserContext() {
//        return m_MrqUserContext;
//    }
//
//    @Override
//    public void onMrqSdkListenerActivityCreated(MrqSdk mrqSdk) {
//        m_mrqSdk = mrqSdk;
//        m_mrqSdk.setDataProvider(this);
//    }
//
//
//    public void SendDataToMRQServer( String strLaunched, List<MrqEvent> events ){
//
//        setCustomerId(getCustomerId());
//
//        m_MrqUserContext = new MrqUserContext(getProperties(), getTags(strLaunched), events);
//
//        getMrqSdk().refresh();
//    }
//
//    /**將航班資訊轉為MrqEvent 物件
//     * @param airline 航班
//     * @param number 航班編號
//     * @param origin 出發站
//     * @param destination 抵達站
//     * @param properties */
//    public MrqEvent setflight1(final String airline, final String number, final String origin, final String destination, final Date start, final Date end, final Map<String, String> properties) {
//
//        MrqEvent flight =new MrqEvent(MrqEventType.FLIGHT, start, end, null, null, new HashMap<String, String>() {{
//            // custom properties
//            putAll(properties);
//            // Add special flight properties (see documentation for MrqEvent)
//            put("airline", airline);
//            put("flightNumber", number);
//            put("originAirport", origin);
//            put("destinationAirport", destination);
//        }});
//
//        return flight;
//    }
//
//    public void SendPNRINfotoMRQ( String strLaunched, List<CITripListResp_Itinerary> arPnrList ){
//
//        List<MrqEvent> events = new ArrayList<>();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//
//        if ( null != arPnrList ) {
//
//            for ( CITripListResp_Itinerary info : arPnrList ){
//
//                //2016-11-18 調整時間的顯示邏輯, by ryan
//                Date dtDepart = null;
//                Date dtArrival = null;
//                try {
//                    dtDepart = sdf.parse(info.getDisplayDepartureDate_GMT() + " " + info.getDisplayDepartureTime_GMT());
//                    dtArrival = sdf.parse(info.getDisplayArrivalDate_GMT() + " " + info.getDisplayArrivalTime_GMT());
//                    //dtDepart = sdf.parse(info.Departure_Date_Gmt + " " + info.Departure_Time_Gmt);
//                    //dtArrival = sdf.parse(info.Arrival_Date_Gmt + " " + info.Arrival_Time_Gmt);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                HashMap<String, String> fltproperty =  new HashMap<String, String>() {};
//                fltproperty.put("pnr",  info.Pnr_Id);
//                //fltproperty.put("tktno","tktnumXXXX");
//
//                events.add(setflight1(info.Airlines, info.Flight_Number, info.Departure_Station, info.Arrival_Station, dtDepart, dtArrival, fltproperty));
//            }
//        }
//
//        setCustomerId(getCustomerId());
//
//        m_MrqUserContext = new MrqUserContext(getProperties(), getTags(strLaunched), events);
//
//        getMrqSdk().refresh();
//    }
//
//    public void test(){
//
//        List<MrqEvent> events = new ArrayList<>();
//
////        Map<String, String> properties = new HashMap<>();
////        Map<String, Set<String>> tags = new HashMap<>();
////
//        setCustomerId("TH0000000");
////
////        String[] tagss = {"android", "com.chinaairlines.mobile30.dev", "launched"};
////
////        tags.put("tags", new HashSet<>(Arrays.asList(tagss)));
//
//        //"2016-05-02T13:05:00.000+0000",
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//
//        try {
//            Calendar calendar = Calendar.getInstance();
//
//            String currentDateandTime = sdf.format(new Date());
//           SLog.d("mrqsdk current Time", currentDateandTime);
//
//            //Date currentDate = sdf.parse("2016-06-19 20:35");
//            Date currentDate = sdf.parse(currentDateandTime);
//
//
//            calendar.setTime(currentDate);
//            //calendar.add(Calendar.HOUR, 2);
//            calendar.add(Calendar.MINUTE, 32);
//            Date dep_Date = calendar.getTime();
//
//            calendar.setTime(currentDate);
//            calendar.add(Calendar.HOUR, 24);
//            Date arr_Date = calendar.getTime();
//
//            calendar.setTime(currentDate);
//            //calendar.add(Calendar.HOUR, 3);
//            calendar.add(Calendar.MINUTE, 34);
//            Date dep_Date2 = calendar.getTime();
//
//            calendar.setTime(currentDate);
//            calendar.add(Calendar.HOUR, 24);
//            Date arr_Date2 = calendar.getTime();
//
//
//            HashMap<String, String> fltproperty_1 =  new HashMap<String, String>() {};
//            fltproperty_1.put("pnr","pnrXXXX");
//            fltproperty_1.put("tktno","tktnumXXXX");
/////
//            events.add( setflight1("CI", "0006", "TPE", "LAX", dep_Date, arr_Date, fltproperty_1));
//            //setflight2("CI", flt2_depnumber.getText().toString(), flt2_depairport.getText().toString(), flt2_arrairport.getText().toString(), dep_Date2, arr_Date2, new HashMap<String, String>());
//
//        } catch (Exception c) {
//            // TODO Auto-generated catch block
//           SLog.e("mrqsdk_c", c.toString());
//        }
//
//        m_MrqUserContext = new MrqUserContext(getProperties(), getTags(TAG_LAUNCHED), events);
//
//        getMrqSdk().refresh();
//    }

}
