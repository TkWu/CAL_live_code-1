package ci.ui.object.item;

import java.util.Locale;

/**
 * Created by JobsNo5 on 16/2/18.
 */
public class LocaleItem {

    public String strTag;
    public Locale locale;
    public String strDisplayName;

    public LocaleItem(){
        locale = Locale.TAIWAN;
        strDisplayName = "";
        strTag = "";
    }
}
