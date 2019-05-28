package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIGetSeatReq;
import ci.ws.Models.entities.CISeatColInfo;
import ci.ws.Models.entities.CISeatInfo;
import ci.ws.Models.entities.CISeatInfoList;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/12.
 */
public class CIGetSeatMapModel extends CIWSBaseModel {

    public interface GetSeatMapCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param SeatInfoList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onGetSeatMapSuccess( String rt_code, String rt_msg, CISeatInfoList SeatInfoList );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onGetSeatMapError( String rt_code, String rt_msg );
    }

    private enum eParaTag {
        version,
    }

    private static final String API_NAME = "/CIAPP/api/GetSeatMap";
    private GetSeatMapCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIGetSeatMapModel( GetSeatMapCallBack callback ){
        this.m_Callback = callback;
    }

    public void GetSeatMap( CIGetSeatReq Seatreq ){
//
//        Seatreq = new CIGetSeatReq();
//        Seatreq.Departure_Date="2016-05-27";
//        Seatreq.Departure_Station="TPE";
//        Seatreq.Arrival_Station="YVR";
//        Seatreq.Airlines="CI";
//        Seatreq.Flight_Number="0032";
//        Seatreq.Booking_Class="W";


        try {

            String strSeat = GsonTool.toJson(Seatreq);

            m_jsBody = new JSONObject(strSeat);

            m_jsBody.put( eParaTag.version.name(), WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }


    /**座位屬性
     * 座位 = A:靠走道 W:靠窗 9: Center seat (not window, not aisle)
     * 走到 = 0:走道*/
    public CISeatInfo.CISeatType ParseColAttr( JSONArray jsarAttr ){

        CISeatInfo.CISeatType SeatType = CISeatInfo.CISeatType.Empty;

        try {
            int ilength = jsarAttr.length();
            for ( int iIdx =0; iIdx < ilength; iIdx++ ){
                String strAttr = jsarAttr.getString(iIdx);

                if ( "A".equals(strAttr) ||  "W".equals(strAttr) || "9".equals(strAttr) ){
                    SeatType = CISeatInfo.CISeatType.Seat;
                    break;
                } else if ( "0".equals(strAttr) ){
                    SeatType = CISeatInfo.CISeatType.Aisle;
                    break;
                } else {
                    SeatType = CISeatInfo.CISeatType.Empty;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return SeatType;
    }

    /**
     * 透過ColAttr 以及 Position_Type來決定座位的型態
     * 位置型態(Len: 1) :0:空位1:座位
     * 實際座位邏輯
     * 座位 = ColAttr:Seat, Position_Type: 1
     * 走到 = ColAttr:Aisle, Position_Type: 0
     * 其餘配對為未知區域, 一律顯示空白
     */
    public CISeatInfo.CISeatType ParseSeatType( CISeatInfo.CISeatType ColAttr, String Position_Type ){

        if ( ColAttr == CISeatInfo.CISeatType.Seat && "1".equals(Position_Type) ){
            return CISeatInfo.CISeatType.Seat;
        } else if ( ColAttr == CISeatInfo.CISeatType.Aisle && "0".equals(Position_Type) ){
            return CISeatInfo.CISeatType.Aisle;
        } else {
            return CISeatInfo.CISeatType.Empty;
        }
    }

    /**
     * 座位註記(Len: 4)
     * Y:已選
     * N:未選*/
    public CISeatInfo.CISeatStatus ParseSeatStatus( String strStatus ){

        if ( "N".equals(strStatus) ){
            return CISeatInfo.CISeatStatus.Available;
        } else if ( "Y".equals(strStatus) ) {
            return CISeatInfo.CISeatStatus.Occupied;
        } else {
            return CISeatInfo.CISeatStatus.Occupied;
        }
    }

    public CISeatFloor ParseSeatInfo( JSONObject jsDown ){

        //快速收尋用的map
        HashMap<String, CISeatInfo.CISeatType> mapColAttr = new HashMap<String, CISeatInfo.CISeatType>();
        mapColAttr.clear();

        CISeatFloor SeatFloorInfo = new CISeatFloor();

        try {
            //取得Col數量
            JSONArray jsarCol = GsonTool.getJSONArrayFromJsobject(jsDown, "Seat_Col");
            SeatFloorInfo.SeatCol = jsarCol.length();
            //解析ColInfo以及標籤
            for (int iIdx = 0; iIdx < SeatFloorInfo.SeatCol; iIdx++) {
                CISeatColInfo ColInfo = new CISeatColInfo();
                JSONObject jsCol = jsarCol.getJSONObject(iIdx);
                ColInfo.ColName = GsonTool.getStringFromJsobject(jsCol, "Id");
                ColInfo.ColType = ParseColAttr(GsonTool.getJSONArrayFromJsobject(jsCol, "Attr"));
                SeatFloorInfo.arColTextList.add(ColInfo);
                //暫存起來, 用來比對座位資訊
                mapColAttr.put(ColInfo.ColName, ColInfo.ColType);
            }
            //取得Row數量
            JSONArray jsarRow = GsonTool.getJSONArrayFromJsobject(jsDown, "Seat_Info");
            SeatFloorInfo.SeatRow = jsarRow.length();
            for (int iIdx = 0; iIdx < SeatFloorInfo.SeatRow; iIdx++) {
                //取得一個Row的座位資訊,
                JSONObject jsInfo = jsarRow.getJSONObject(iIdx);
                String strSeatRowNum = GsonTool.getStringFromJsobject(jsInfo, "Seat_Row_Number");
                JSONArray jsarSeat_Occupation = GsonTool.getJSONArrayFromJsobject(jsInfo, "Seat_Occupation");
                int ilength = jsarSeat_Occupation.length();
                for (int iJdx = 0; iJdx < ilength; iJdx++) {
                    //解析座位, 由左至右
                    CISeatInfo SeatInfo = new CISeatInfo();
                    JSONObject jsSeat = jsarSeat_Occupation.getJSONObject(iJdx);
                    SeatInfo.Col_Name = GsonTool.getStringFromJsobject(jsSeat, "Seat_Column");
                    SeatInfo.Row_Number = strSeatRowNum;
                    //取得座位型態
                    String Position_Type  = GsonTool.getStringFromJsobject(jsSeat, "Position_Type");
                    //使用Col屬性與座位型態比對出實際座位的屬性
                    SeatInfo.SeatType   = ParseSeatType(mapColAttr.get(SeatInfo.Col_Name), Position_Type);
                    //判斷該座位是否已被選
                    SeatInfo.SeatStatus = ParseSeatStatus(GsonTool.getStringFromJsobject(jsSeat, "Seat_Status"));
                    //
                    SeatFloorInfo.arSeatList.add(SeatInfo);
                }
                //容錯處理, 除了WS座位提供有缺, 不然不會進來
                if (ilength < SeatFloorInfo.SeatCol) {
                    int iSize = SeatFloorInfo.SeatCol - ilength;
                    for (int iIx = 0; iIx < iSize; iIx++) {
                        CISeatInfo SeatInfo = new CISeatInfo();
                        SeatInfo.Col_Name = "";
                        SeatInfo.Row_Number = strSeatRowNum;
                        SeatInfo.SeatType = CISeatInfo.CISeatType.Empty;
                        SeatInfo.SeatStatus = CISeatInfo.CISeatStatus.Occupied;
                        SeatFloorInfo.arSeatList.add(SeatInfo);
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return SeatFloorInfo;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        //實際的座位資訊
        CISeatInfoList SeatInfoList = new CISeatInfoList();
        try{
            JSONObject jsData = new JSONObject(respBody.strData);
            JSONObject jsItinerary_Info = null;
            JSONObject jsDown = null;
            JSONObject jsUpper = null;

            jsItinerary_Info = GsonTool.getJsobjectFromJsobject(jsData, "Itinerary_Info");
            //取得班機編號資訊
            if ( null != jsItinerary_Info )
                SeatInfoList.iataAircraftTypeCode = GsonTool.getStringFromJsobject(jsItinerary_Info, "Equiment");
            //下層資訊
            if ( null != jsItinerary_Info ){
                JSONArray   jsarMain = GsonTool.getJSONArrayFromJsobject(jsItinerary_Info, "Main_Floor");
                int iLength = jsarMain.length();
                for ( int iIdx = 0; iIdx < iLength; iIdx++ ){
                    JSONObject jsMainSeat = jsarMain.getJSONObject(iIdx);
                    if ( null != jsMainSeat ){
                        CISeatFloor seatFloor = ParseSeatInfo(jsMainSeat);
                        SeatInfoList.arMainSeatFloorList.add(seatFloor);
                    }
                }
                //避免換版閃退的暫時解法
                int iSize = SeatInfoList.arMainSeatFloorList.size();
                for ( int iJx = 0; iJx < iSize; iJx++ ){
                    CISeatFloor info = SeatInfoList.arMainSeatFloorList.get(iJx);
                    if ( iJx == 0 ) {
                        SeatInfoList.Down_SeatFloor = new CISeatFloor();
                        SeatInfoList.Down_SeatFloor.SeatCol = info.SeatCol;
                        SeatInfoList.Down_SeatFloor.SeatRow = info.SeatRow;
                        SeatInfoList.Down_SeatFloor.arColTextList.addAll(info.arColTextList);
                        SeatInfoList.Down_SeatFloor.arSeatList.addAll(info.arSeatList);
                    } else {
                        SeatInfoList.Down_SeatFloor.SeatRow += info.SeatRow;
                        SeatInfoList.Down_SeatFloor.arSeatList.addAll(info.arSeatList);
                    }
                }
                //
            }
            //上層資訊
            if ( null != jsItinerary_Info ){
                JSONArray   jsarUpFloor = GsonTool.getJSONArrayFromJsobject(jsItinerary_Info, "Upper_Floor");
                int iLength = jsarUpFloor.length();
                for ( int iIdx = 0; iIdx < iLength; iIdx++ ){
                    JSONObject jsUpSeat = jsarUpFloor.getJSONObject(iIdx);
                    if ( null != jsUpSeat ){
                        CISeatFloor seatFloor = ParseSeatInfo(jsUpSeat);
                        SeatInfoList.arUpSeatFloorList.add(seatFloor);
                    }
                }
                //避免換版閃退的暫時解法
                int iSize = SeatInfoList.arUpSeatFloorList.size();
                for ( int iJx = 0; iJx < iSize; iJx++ ){
                    CISeatFloor info = SeatInfoList.arUpSeatFloorList.get(iJx);
                    if ( iJx == 0 ) {
                        SeatInfoList.Up_SeatFloor = new CISeatFloor();
                        SeatInfoList.Up_SeatFloor.SeatCol = info.SeatCol;
                        SeatInfoList.Up_SeatFloor.SeatRow = info.SeatRow;
                        SeatInfoList.Up_SeatFloor.arColTextList.addAll(info.arColTextList);
                        SeatInfoList.Up_SeatFloor.arSeatList.addAll(info.arSeatList);
                    } else {
                        SeatInfoList.Up_SeatFloor.SeatRow += info.SeatRow;
                        SeatInfoList.Up_SeatFloor.arSeatList.addAll(info.arSeatList);
                    }
                }
            }
        }catch ( Exception e ){
            e.printStackTrace();
        }

        if ( null == SeatInfoList.arMainSeatFloorList || SeatInfoList.arMainSeatFloorList.size() <= 0 ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback ){
            m_Callback.onGetSeatMapSuccess(respBody.rt_code, respBody.rt_msg, SeatInfoList );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.SelectSeat)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onGetSeatMapError(code, strMag);
            }
        }
    }

}
