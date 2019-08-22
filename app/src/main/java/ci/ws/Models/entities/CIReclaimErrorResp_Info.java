package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class CIReclaimErrorResp_Info implements Serializable {

    @Expose
    public String code;
    @Expose
    public String msg;
    @Expose
    public String seq;
}
