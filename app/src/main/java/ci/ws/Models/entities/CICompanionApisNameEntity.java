package ci.ws.Models.entities;

import java.util.ArrayList;

/**
 * Created by joannyang on 16/6/1.
 */
public class CICompanionApisNameEntity implements Cloneable {

    public String full_name;
    public String display_name;

    public ArrayList<CIApisEntity> arCompanionApisList = new ArrayList<>();


    @Override
    protected Object clone() throws CloneNotSupportedException {

        try {
            return super.clone();
        }catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
