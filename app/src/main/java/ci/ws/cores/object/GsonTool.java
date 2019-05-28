package ci.ws.cores.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Ryan on 16/3/25.
 */
public class GsonTool {

    public static Gson m_gson = null;

    public static Gson getGson(){
        if ( null == m_gson ){
            m_gson = new Gson();
        }
        return m_gson;
    }

    /*解析Json*/
    public static <T> T toObject( String strJson, Class<T> cls ){

        T t = null;
        try {
            t = getGson().fromJson(strJson, cls);
        } catch (Exception e ){
            e.printStackTrace();
        }
        return t;
    }

    /*轉Json*/
    public static String toJson( Object obj ){

        String strJson = "";
        try {
            strJson = getGson().toJson(obj);
        } catch (Exception e ){
            e.printStackTrace();
        }
        return strJson;
    }

    /**取得JSONObject內的StringValue*/
    public static String getStringFromJsobject( JSONObject jsObj , String strTag ){

        String strValue = "";
        try{
            if ( null == jsObj ){
                return strValue;
            }

            strValue = jsObj.optString(strTag, "");

        } catch (Exception e){
            e.printStackTrace();
        }

        return strValue;
    }
    /**取得JSONObject內的IntValue*/
    public static int getIntFromJsobject( JSONObject jsObj , String strTag ){

        int iValue = -1;

        try{
            if ( null == jsObj ){
                return iValue;
            }

            iValue = jsObj.optInt(strTag, -1);

        } catch (Exception e){
            e.printStackTrace();
        }

        return iValue;
    }

    /**取得JSONObject內的BooleanValue*/
    public static Boolean getBooleanFromJsobject( JSONObject jsObj , String strTag ){

        Boolean bValue = false;

        try{

            if ( null == jsObj ){
                return bValue;
            }

            bValue = jsObj.optBoolean( strTag , false);

        } catch (Exception e){
            e.printStackTrace();
        }

        return bValue;
    }

    /**取得JSONObject內的JSONObject*/
    public static JSONObject getJsobjectFromJsobject( JSONObject jsObj , String strTag ){

        JSONObject jsValue = null;
        try{
            if ( null == jsObj ){
                return jsValue;
            }

            jsValue = jsObj.optJSONObject(strTag);

        } catch (Exception e){
            e.printStackTrace();
        }

        return jsValue;
    }
    /**取得JSONObject內的JSONObject*/
    public static JSONArray getJSONArrayFromJsobject( JSONObject jsObj , String strTag ){

        JSONArray jsarValue = null;
        try{

            if ( null == jsObj ){
                return jsarValue;
            }

            jsarValue = jsObj.optJSONArray(strTag);

        } catch (Exception e){
            e.printStackTrace();
        }

        return jsarValue;
    }
}
