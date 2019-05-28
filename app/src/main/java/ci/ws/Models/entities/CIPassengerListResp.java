package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 某行程的所有乘客資料
 * Created by jlchen on 16/5/11.
 */
@SuppressWarnings("serial")
public class CIPassengerListResp implements Serializable {

    public List<CIPassengerListResp_PaxInfo> Pax_Info = new ArrayList<>();

}
