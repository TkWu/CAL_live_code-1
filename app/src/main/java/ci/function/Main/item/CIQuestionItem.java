package ci.function.Main.item;

import java.util.ArrayList;

/**
 * Created by kevincheng on 2017/5/9.
 */

public class CIQuestionItem implements Cloneable{
    public String title;
    public String version;
    public String language;
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Question> questions;

    public CIQuestionItem() {
        this.title = "";
        this.language = "";
        this.version = "";
        this.questions = new ArrayList<>();
    }

    public static  class Question {
        public final static int PROGRESS_OF_OPTION = 10;
        public String code;
        public String name;
        public int    progress = 100;
    }
}
