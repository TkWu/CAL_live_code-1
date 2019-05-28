package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlchen on 2016/5/31.
 */
@SuppressWarnings("serial")
public class CIBoardPassResp implements Serializable {

    public List<CIBoardPassResp_PnrInfo> Pnr_Info = new ArrayList<>();
}
