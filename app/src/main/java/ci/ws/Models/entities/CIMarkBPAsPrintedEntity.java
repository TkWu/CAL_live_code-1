package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 16/8/11.
 */
public class CIMarkBPAsPrintedEntity {

    @Expose
    public List<CIMarkBPAsPrinted_Pax_Info> Pax_Info = new ArrayList<>();

}
