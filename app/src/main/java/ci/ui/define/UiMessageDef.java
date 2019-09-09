package ci.ui.define;


import ci.ws.define.SocialNetworkKind;

/**
 * 各畫面間傳遞訊息用的ID
 * */
public class UiMessageDef {

	public static final int		MSG_LOGIN_GOOGLE								= 9992;
	public static final int		MSG_BACK_HOME									= 9993;
	public static final int		MSG_DO_LOGOUT									= 9994;
	public static final int		MSG_RESTART_APP									= 9995;
	public static final int		MSG_BACK_PRESSED								= 9998;
	public static final int		MSG_EXIT_APP									= 9999;

	public static final int		REQUEST_CODE_CHECK_IN_ADD_SINGLE_PASSENGER		= 200;
	public static final int 	REQUEST_CODE_SOCIAL_NETWORK_TAG 				= 201;
	public static final int 	REQUEST_CODE_SOCIAL_NETWORK_DISCONNECT_TAG 		= 202;
	public static final int 	REQUEST_CODE_LOGIN 								= 203;
	public static final int 	REQUEST_CODE_LOGIN_INPUT_DETAIL 				= 204;
	public static final int 	REQUEST_CODE_PERSONAL_ADD_APIS_TAG 				= 205;
	public static final int 	REQUEST_CODE_PERSONAL_EDIT_APIS_TAG 			= 206;
	public static final int 	REQUEST_CODE_TRIP_DETAIL_TAG 					= 207;
	public static final int 	REQUEST_CODE_CHECK_IN 							= 208;
	public static final int 	REQUEST_CODE_MY_TRIP 							= 209;
	public static final int 	REQUEST_CODE_FIND_MY_BOOKING 					= 210;
	public static final int 	REQUEST_CODE_PERSONAL_PROFILE_EDIT 				= 211;
	public static final int		REQUEST_CODE_SIGN_UP							= 212;
	public static final int 	REQUEST_CODE_PERSONAL_ADD_COMPANIONS_APIS_TAG 	= 213;
	public static final int 	REQUEST_CODE_PERSONAL_EDIT_COMPANIONS_APIS_TAG 	= 214;
	public static final int		REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_SEAT	= 215;
	public static final int		REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_MEAL	= 216;
	public static final int		REQUEST_CODE_TRIP_DETAIL_PASSENGER_CHECK_IN_WEB = 217;
	public static final int		REQUEST_CODE_TRIP_DETAIL_PASSENGER_CHECK_IN		= 218;
	public static final int		REQUEST_CODE_TRIP_DETAIL_PASSENGER_CANCEL_CHECK_IN = 219;
	public static final int		REQUEST_CODE_TRIP_DETAIL						= 220;
	public static final int		REQUEST_CODE_CHECK_IN_SELECT_SEAT				= 221;
	public static final int		REQUEST_CODE_BECOME_MEMBER						= 222;
	public static final int		REQUEST_CODE_ADD_EXCESS_BAG						= 222;
	public static final int		REQUEST_CODE_GDPR_WEB							= 223;
	public static final int		REQUEST_CODE_CHECK_IN_EDIT_APIS_VISA			= 224;

	public static final int		RESULT_CODE_LANGUAGE_SETTING					= 100;
	public static final int		RESULT_CODE_LANGUAGE_CHANGE						= 101;
	public static final int		RESULT_CODE_CHECK_IN_ADD_SINGLE_PASSENGER		= 102;
	public static final int		RESULT_CODE_SOCIAL_NETWORK_DISCONNECT_OK_TAG	= 103;
	public static final int 	RESULT_CODE_ADD_PASSENGER 						= 104;
	public static final int 	RESULT_CODE_GO_TRIP_DETAIL 						= 105;
	public static final int 	RESULT_CODE_GO_FLIGHT_STATUS 					= 106;

	public static final String	BUNDLE_DIALOG_MSG_TAG							= "Dialog_Msg";
	public static final String	BUNDLE_TOAST_MSG_TAG							= "Toast_Msg";

	public static final String	BUNDLE_MENU_VIEW_ID_TAG							= "Menu_ViewId";
	public static final String	BUNDLE_ACTIVITY_MODE							= "Activity_Mode";	//切換Activity不同畫面用的key
	public static final String	BUNDLE_ACTIVITY_DATA_HINT						= "hint";			//傳遞給Actvity的hint key
	public static final String	BUNDLE_ACTIVITY_DATA_TITLE						= "Activity_title"; //傳遞給Actvity的title key
	public static final String  BUNDLE_ACTIVITY_DATA_BRANCH 					= "Activity_branch"; //傳遞給Actvity的key
	public static final String	BUNDLE_ACTIVITY_DATA_YOUTUBE_ID					= "Activity_youtube_id"; //傳遞給Actvity的youtube id key
	public static final String	BUNDLE_ACTIVITY_DATA							= "Activity_data";  //傳遞給Actvity的data
	public static final String	BUNDLE_FRAGMENT_MODE							= "Fragment_Mode";	//切換Fragment不同畫面用的key
	public static final String	BUNDLE_FRAGMENT_DATA							= "Fragment_Data";	//Fragment接收資料用的key
	public static final String	BUNDLE_TEXT_FEILD_FRAGMENT_ID					= "Text_Feild_Fragment_Id";	//CITextFeildFragment接收資料用的key
	public static final String	BUNDLE_LOGIN_TYPE_GOOGLE						= SocialNetworkKind.GOOGLE;			//登入類型Google
	public static final String	BUNDLE_LOGIN_TYPE_FACEBOOK						= SocialNetworkKind.FACEBOOK;		//登入類型FB
	public static final String 	BUNDLE_LOGIN_TYPE_DYNASTY_FLYER					= "DynastyFlyer";					//正式會員
	public static final String	BUNDLE_NOT_LOGIN_USERNAME_TAG					= "UserName";
	public static final String	BUNDLE_IS_DO_LOGOUT								= "IsDoLogout";
	public static final String	BUNDLE_LOGIN_USERNAME_TAG						= "Login_UserName";
	public static final String	BUNDLE_LOGIN_ACCOUNT_TAG 						= "Login_UserAcc";
	public static final String	BUNDLE_LOGIN_PASSWORD_TAG						= "Login_UserPw";
	public static final String	BUNDLE_INPUT_DETAIL_TYPE_TAG 					= "InputDetail_Type";
	public static final String 	BUNDLE_IS_SOCIAL_COMBINE_TAG					= "IsSocialCombine";//登入或註冊時是否要連結社群
	public static final String 	BUNDLE_IS_CHECK_IN_SELECT_SEAT					= "IsCheckInSelectSeat";
	public static final String 	BUNDLE_CHECK_IN_SELECT_SEAT_DATA				= "CheckIn_SelectSeat";
	public static final String 	BUNDLE_CHECK_IN_TRIP_PNR_ID						= "CheckSelectSeatIn_PnrId";
	public static final String 	BUNDLE_SELECT_SEAT_DATA							= "SelectSeat";

	public static final String	BUNDLE_MY_TRIPS_DATA_LIST						= "Trip_Data";//PNR或航段資料
	public static final String 	BUNDLE_TRIP_LIST_RESP 							= "BUNDLE_TRIP_LIST_RESP";//
	public static final String	BUNDLE_TRIPS_DETIAL_CURRENT_PAGE				= "TripsDetail_Page"; //TripDetail初始化時需顯示的tab index
	public static final String	BUNDLE_PASSENGER_INFO							= "Passenger_Data";//乘客資料
	public static final String	BUNDLE_PASSENGER_INFO_SINGLE					= "Single_Passenger_Data";//乘客資料
	public static final String 	BUNDLE_CANCEL_CHECK_IN_DATA 					= "CancelCheckIn";
	public static final String 	BUNDLE_INQUIRY_EBBASIC_INFO_RESP 				= "InquiryEBBasicInfoResp";
	public static final String 	BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_REQ 			= "InquiryExcessBaggageInfoReq";
	public static final String 	BUNDLE_INQUIRY_EXCESS_BAGGAGE_INFO_RESP 		= "InquiryExcessBaggageInfoResp";
	public static final String 	BUNDLE_INQUIRY_CCPAGE_RESP 						= "InquiryCCPageResp";
	public static final String 	BUNDLE_EBPAYMENTS_RESP 							= "EBPaymentsResp";
	public static final String 	BUNDLE_PAYMENTS_RESULT_MODE 				    = "EBPaymentsResultMode";

	public static final String	BUNDLE_SOCIAL_NETWORK_DISCONNECT_TAG			= "SocialNetwork_Disconnect";
	public static final String	BUNDLE_PERSONAL_EDIT_APIS_TAG					= "Edit_APIS";
	public static final String	BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG			= "Edit_APIS_User_Name";
	public static final String	BUNDLE_PERSONAL_EDIT_APIS_DATA_TAG				= "Edit_APIS_Data";
	public static final String	BUNDLE_WEBVIEW_TITLE_TEXT_TAG					= "Webview_Title";
	public static final String	BUNDLE_WEBVIEW_TYPE_GDPR						= "bundle_webview_type_gdpr";
	//644336 2019 2月3月 AI/行事曆/截圖/注意事項
	public static final String	BUNDLE_WEBVIEW_TYPE_AI							= "bundle_webview_type_ai";
	public static final String	BUNDLE_CHKIN_FINDMYBOOKING_NOTES				= "bundle_chkin_findmybooking_notes";
	//644336 2019 2月3月 AI/行事曆/截圖/注意事項
	public static final String	BUNDLE_WEBVIEW_URL_TAG							= "Webview_URL";
	public static final String	BUNDLE_WEBVIEW_POST_DATA_TAG					= "Webview_Post_Data";
	public static final String	BUNDLE_WEBVIEW_WEB_DATA_TAG						= "Webview_Web_Data";
	public static final String	BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG		= "Webview_Web_Is_Show_Close_Btn";
	public static final String 	BUNDLE_LOGOUT_REQUEST_TAG						= "Logout_Request_Tag"; //切換至首頁時判斷是否需登出

	public static final String	BUNDLE_BACKGROUND_BITMAP_TAG					= "Bg_Bitmap";
	public static final String	BUNDLE_EWALLET_REQUEST							= "Ewallet_Req";
	public static final String	BUNDLE_BOARDING_PASS_DATA						= "BoardingPass_Data";
	public static final String	BUNDLE_BOARDING_PASS_DATAS						= "BoardingPass_Datas";
	public static final String	BUNDLE_BOARDING_PASS_INDEX						= "BoardingPass_Index";
	public static final String	BUNDLE_EWALLET_EXTRA_SERVICES_DATA				= "Ewallet_ES_Data";
	public static final String 	BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG 			= "Expired_Tag";
	public static final String	BUNDLE_MILES_DETAIL_DATA						= "Miles_Data";
	public static final String	BUNDLE_MILES_DETAIL_TYPE						= "Miles_Type";
	public static final String	BUNDLE_PERSONAL_QRCODE							= "Personal_QRCode";
	public static final String	BUNDLE_SELECT_SEAT_WEBVIEW_URL					= "Seat_Url";
	public static final String  BUNDLE_PULL_QUES_REQ_DATA                       = "pull_questionnaire_req";
	public static final String  BUNDLE_QUESTIONNAIRE_DATA                       = "questionnaire";

	public static final String BUNDLE_CHECK_IN_FLIGHT_LIST						= "CheckInFlightList";

	public static final String BUNDLE_CHECK_IN_RESULT							= "CheckInResult"; //離開check-in 回傳是否成功check-in

	public static final String BUNDLE_BAGGAGE_INFO_RESP							= "BaggageInfoList";

	public static final String BUNDLE_BAGGAGE_CONTENT_NUMBER					= "BaggageNumber";
	public static final String BUNDLE_BAGGAGE_CONTENT_DEPARTUREDATE				= "BaggageDepartureDate";
	public static final String BUNDLE_BAGGAGE_CONTENT_DEPARTURESTATION			= "BaggageDepartureStation";
	public static final String BUNDLE_BAGGAGE_CONTENT_ARRIVALSTATION			= "BaggageArrival";
	public static final String BUNDLE_BAGGAGE_CONTENT_RESP						= "BaggageData";

	public static final String BUNDLE_FIRST_NAME								= "FirstName";
	public static final String BUNDLE_LAST_NAME									= "LastName";

	public static final String 	BUNDLE_BOOKING_ISORIGINAL_Y					= "IsOriginal";
}
