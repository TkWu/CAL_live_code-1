package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlchen on 2016/6/17.
 */
@SuppressWarnings("serial")
public class CICancelCheckInReq implements Serializable{

    public List<CICancelCheckInReq_PaxInfo> Pax_Info;

    public CICancelCheckInReq(){
        Pax_Info = new ArrayList<>();
    }
}