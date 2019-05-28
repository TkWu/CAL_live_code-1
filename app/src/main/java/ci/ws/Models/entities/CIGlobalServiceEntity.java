package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kevincheng on 2016/3/5.
 * 此資料表主要是儲存全球服務據點的資料，包含了機場名稱、電話、地址、座標等...
 */


/**
 * @DatabaseTable 建立資料表所用的註解
 * (tableName = "global_service") 代表資料表名稱為 global_service
 * */
@DatabaseTable(tableName = "branch")
public class CIGlobalServiceEntity implements Cloneable{

    /**generatedId = true 代表自動建立id*/
    @DatabaseField(generatedId = true)
    public int id;

    /**
     * @Expose 需加此註解才可以讓Gson解析
     * @DatabaseField 加此註解代表要讓ormlite加入table時建立此欄位
     */


    @Expose
    @DatabaseField
    public String continene;        //國家分類

    @Expose
    @DatabaseField
    public String branch_name;      //營業所代號

    @Expose
    @DatabaseField
    public String branch;           //營業所名稱

    @Expose
    @DatabaseField
    public String address;          //營業所地址

    @Expose
    @DatabaseField
    public String ticket_op_tel;    //票務電話

    @Expose
    @DatabaseField
    public String ticket_op_fax;    //票務傳真

    @Expose
    @DatabaseField
    public String book_ticket_tel;  //訂位電話

    @Expose
    @DatabaseField
    public String book_ticket_fax;  //訂位傳真

    @Expose
    @DatabaseField
    public String open_time;        //營業時間

    @Expose
    @DatabaseField
    public double lng;              //緯度

    @Expose
    @DatabaseField
    public double lat;              //經度

    public CIGlobalServiceEntity(){
        continene = "Global";
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
