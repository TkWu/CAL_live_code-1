package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlchen on 2016/6/17.
 */
@SuppressWarnings("serial")
public class CICancelCheckInResp implements Serializable {

    public List<CICancelCheckInResp_PaxInfo> Pax_Info = new ArrayList<>();
}
