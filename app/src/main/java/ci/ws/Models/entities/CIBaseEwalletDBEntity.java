package ci.ws.Models.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by jlchen on 2016/6/20.
 * 查詢Ewallet後用於儲存的資料表
 */

public class CIBaseEwalletDBEntity {

    @DatabaseField(id = true, canBeNull = false, useGetSet = true)
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    /**response的Json格式字串，存於DB等待需要使用的時候使用Gson轉成物件*/
    @DatabaseField(dataType = DataType.STRING_BYTES)
    public String respResult;
}
