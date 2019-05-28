package ci.ws.Models.entities;

/**
 * Created by Ryan on 16/5/5.
 */
public class CISeatColInfo {

    public String ColName;
    public CISeatInfo.CISeatType ColType;


    public CISeatColInfo (){
        ColName = "";
        ColType = CISeatInfo.CISeatType.Aisle;
    }
}
