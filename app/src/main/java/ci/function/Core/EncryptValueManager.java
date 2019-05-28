package ci.function.Core;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by kevincheng on 2017/6/13.
 */

public class EncryptValueManager {

    enum EValueType{
        STRING, BOOLEAN, INT, LONG, FLOAT
    }

    /**
     * 將資料加密後儲存於SharedPreferences
     * @param sharedPreferences SharedPreferences instance
     * @param key               SharedPreferences key
     * @param value             資料
     */
    public static void setValue(SharedPreferences sharedPreferences,
                          String key,
                          Object value){
        if (null == sharedPreferences) {
            return ;
        }
        Encryption.AES aes = Encryption.AES.getInstance();
        String covertedValue = "";
        if(false == value instanceof String){
            covertedValue = aes.encrypt(String.valueOf(value));
        } else {
            covertedValue = aes.encrypt((String)value);
        }

        sharedPreferences.edit().putString(key, covertedValue).commit();
    }

    /**
     * 取得SharedPreferences的資料並解密
     * @param sharedPreferences SharedPreferences instance
     * @param key               SharedPreferences key
     * @param defValue          預設值
     * @return
     */
    private static  Object getValue(EValueType valueType,
                            SharedPreferences sharedPreferences,
                            String key,
                            Object defValue){

        Encryption.AES aes = Encryption.AES.getInstance();
        try{
            String value = sharedPreferences.getString(key, null);
            if(value == null){
                return defValue;
            }
            if(valueType == EValueType.STRING) {
                return aes.decrypt(value);
            } else if(valueType == EValueType.BOOLEAN){
                return Boolean.valueOf(aes.decrypt(value));
            } else if(valueType == EValueType.INT){
                return Integer.valueOf(aes.decrypt(value));
            } else if(valueType == EValueType.LONG){
                return Long.valueOf(aes.decrypt(value));
            } else if(valueType == EValueType.FLOAT){
                return Float.valueOf(aes.decrypt(value));
            }
        }catch (Exception e) {}
        return defValue;
    }

    public static String getString(SharedPreferences sharedPreferences,
                                   String key,
                                   String defValue){

        return (String) getValue(EValueType.STRING,
                                 sharedPreferences,
                                 key,
                                 defValue);
    }

    public static boolean getBoolean(SharedPreferences sharedPreferences,
                                    String key,
                                     boolean defValue){

        return (boolean) getValue(EValueType.BOOLEAN,
                                 sharedPreferences,
                                 key,
                                 defValue);
    }

    public static int getInt(SharedPreferences sharedPreferences,
                             String key,
                             int defValue){

        return (int) getValue(EValueType.INT,
                                  sharedPreferences,
                                  key,
                                  defValue);
    }

    public static long getLong(SharedPreferences sharedPreferences,
                                 String key,
                                 long defValue){

        return (long) getValue(EValueType.LONG,
                              sharedPreferences,
                              key,
                              defValue);
    }

    public static float getFloat(SharedPreferences sharedPreferences,
                                  String key,
                                  float defValue){

        return (float) getValue(EValueType.FLOAT,
                               sharedPreferences,
                               key,
                               defValue);
    }
}
