package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIMarkBPAsPrintedEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by ryan on 2016-08-11.
 */
public class CIMarkBPAsPrintedModel extends CIWSBaseModel {

    public interface MarkBPAsPrintCallBack {

        /**
         * Api: MakeBPAsPrint 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        //void onMarkBPAsPrintSuccess(String rt_code, String rt_msg, CIMarkBPAsPrintedEntity PaxInfo );
        void onMarkBPAsPrintSuccess(String rt_code, String rt_msg);

        /**
         * Api: MakeBPAsPrint 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onMarkBPAsPrintError(String rt_code, String rt_msg);
    }

    private static final String API_NAME_MARKBPADPRINTED= "/CIAPP/api/MarkBPAsPrinted";

    private              String     m_testFileName      = "";
    private              boolean    m_bIsOnSuccess      = true;

    private enum eParaTag {
        Pax_Info,
        version,
    }

    private String m_strAPIName = "";
    private MarkBPAsPrintCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return m_strAPIName;
    }

    public void setCallback(MarkBPAsPrintCallBack callback){
        this.m_Callback = callback;
    }



    /**
     * 4.1.11.	MarkBPAsPrinted()
     * 功能說明:
     * 對應1A API : DCSPRT_MarkBPAsPrinted
     */
    public void MarkBPAsPrinted( CIMarkBPAsPrintedEntity markBPAsPrintedReq ){

        m_strAPIName = API_NAME_MARKBPADPRINTED;

        try {

            String strRequest = GsonTool.toJson(markBPAsPrintedReq.Pax_Info);
            m_jsBody = new JSONObject();

            JSONArray jsonArray = new JSONArray(strRequest);

            m_jsBody.put(eParaTag.Pax_Info.name(), jsonArray );
            m_jsBody.put(eParaTag.version.name(), WSConfig.DEF_API_VERSION);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();

    }


    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

//        CIMarkBPAsPrintedEntity response = null;
//
//        try {
//
//            response = GsonTool.toObject(respBody.strData, CIMarkBPAsPrintedEntity.class);
//
//            //JSONObject jsData = new JSONObject(respBody.strData);
//
//
//            //JSONArray jsarPax_info = GsonTool.getJSONArrayFromJsobject(jsData, "Pax_Info");
//
//            if( null == response ) {
//                SendError_Response_can_not_parse();
//                return ;
//            }
//
//            if (null != m_Callback) {
//                //m_Callback.onMarkBPAsPrintSuccess(respBody.rt_code, respBody.rt_msg, arPaxInfo);
//            }
//        }  catch (Exception e) {
//            e.printStackTrace();
//            SendError_Response_can_not_parse();
//            return;
//        }

        if ( null != m_Callback ){
            m_Callback.onMarkBPAsPrintSuccess(respBody.rt_code, respBody.rt_msg );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_Callback ){
            m_Callback.onMarkBPAsPrintError(code, strMag);
        }
    }
}
