package ci.ws.Models.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by kevincheng on 2017/5/11.
 * 儲存待上傳的問卷答案
 */
@SuppressWarnings("serial")
@DatabaseTable(tableName = "pending_questionnaire")
public class CIPendingQuestionnaireEntity extends CIPushQuestionnaireReq {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String version;

    @DatabaseField
    public String token;

    /**問卷答案 主要是以 CIQuestionItem 轉換成字串存入*/
    @DatabaseField(canBeNull = false)
    public String questionItem ;

    public CIPendingQuestionnaireEntity(){
        version         = "";
        token           = "";
        questionItem    = "";
    }

}
