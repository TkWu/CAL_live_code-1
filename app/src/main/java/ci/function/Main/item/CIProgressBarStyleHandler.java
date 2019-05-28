package ci.function.Main.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.HashMap;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2017/5/9.
 */

public class CIProgressBarStyleHandler {

    private final static Map<String, Integer> s_mapMemoryForViewTag = new HashMap<>();
    public  final static int SCORE_OF_LEVEL = 1;

    public static void setSeekBarStyle(int progress,
                                       ProgressBar progressBar,
                                       TextView thumbText ,
                                       boolean bIsClearMemory){

        if(true == bIsClearMemory){
            clearMap();
        }

        if(null == progressBar){
            return;
        }
        //設定progress最低只能到10
        if(progress < 10){
            progressBar.setProgress(10);
            progress = 10;
        }
        int thumbResourceId = 0;
        int progressResourceId = 0;
        String score = String.valueOf(SCORE_OF_LEVEL);
        String tag = (String)progressBar.getTag();
        if(progress > 0 && progress <= 20) {
            thumbResourceId = R.drawable.seekbar_thumb_red;
            progressResourceId = R.drawable.seekbar_progress_red;
            if(progress <= 10){
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL));
            } else {
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 2));
            }
        } else if(progress >20 && progress <= 40) {
            thumbResourceId = R.drawable.seekbar_thumb_dusty_orange;
            progressResourceId = R.drawable.seekbar_progress_dusty_orange;
            if(progress <= 30){
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 3));
            } else {
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 4));
            }
        } else if(progress > 40 && progress <= 60) {
            thumbResourceId = R.drawable.seekbar_thumb_mango;
            progressResourceId = R.drawable.seekbar_progress_mango;
            if(progress <= 50){
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 5));
            } else {
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 6));
            }
        } else if(progress > 60 && progress <= 80) {
            thumbResourceId = R.drawable.seekbar_thumb_dark_mint_green;
            progressResourceId = R.drawable.seekbar_progress_dark_mint_green;
            if(progress <= 70){
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 7));
            } else {
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 8));
            }
        } else if(progress > 80 && progress <= 100) {
            thumbResourceId = R.drawable.seekbar_thumb_french_blue;
            progressResourceId = R.drawable.seekbar_progress_french_blue;
            if(progress <= 90){
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 9));
            } else {
                score = String.valueOf(String.valueOf(SCORE_OF_LEVEL * 10));
            }
        }

        ViewScaleDef def = ViewScaleDef.getInstance(CIApplication.getContext());

        if(null != thumbText){
            thumbText.setText(score);
        }

        if(thumbResourceId == (getResourceIdByTag(tag))){
            return;
        }


        int size    = def.getLayoutMinUnit(18);
        int height  = def.getLayoutMinUnit(6.5);
        int width   = def.getLayoutMinUnit(1);
        int radius  = def.getLayoutMinUnit(2.5);
        if(true == progressBar instanceof SeekBar) {
            LayerDrawable layerDrawable = (LayerDrawable)getDrawable(thumbResourceId);
            layerDrawable.setLayerInset(0, 0, height, width, height);
            GradientDrawable gradientDrawable = (GradientDrawable)layerDrawable.getDrawable(1);
            gradientDrawable.setSize(size,size);
            ((SeekBar)progressBar).setThumb(layerDrawable);
        }
        LayerDrawable layerDrawable = (LayerDrawable)getDrawable(progressResourceId);
        GradientDrawable gradientDrawable0 = (GradientDrawable)layerDrawable.getDrawable(0);
        gradientDrawable0.setSize(size, size);
        gradientDrawable0.setCornerRadius(radius);
        layerDrawable.setLayerInset(1, 0, height, 0, height);
        GradientDrawable gradientDrawable1 = (GradientDrawable)layerDrawable.getDrawable(1);
        gradientDrawable1.setCornerRadius(radius);
        layerDrawable.setLayerInset(2, 0, height, 0, height);
        ScaleDrawable scaleDrawable = (ScaleDrawable)layerDrawable.getDrawable(2);
        ((GradientDrawable)scaleDrawable.getDrawable()).setCornerRadius(radius);
        progressBar.setProgressDrawable(layerDrawable);

        putResourceIdByTag(tag, thumbResourceId);
    }

    public static void setProgressByRange(int progress, ProgressBar progressBar){

        int valueOfLevel = 10;
        int level = progress / valueOfLevel;
        int firstProgress = (level * valueOfLevel);
        int lastProgress = ((level + 1) * valueOfLevel);

        firstProgress = firstProgress > 100 ? 100 : firstProgress;
        lastProgress = lastProgress > 100 ? 100 : lastProgress;
        firstProgress = firstProgress < 0 ? 0 : firstProgress;
        lastProgress = lastProgress < 0 ? 0 : lastProgress;

        if(progress > firstProgress && progress <= lastProgress) {
            progressBar.setProgress(lastProgress);
        }

    }

    private static Drawable getDrawable(int resourceId){

        Context context = CIApplication.getContext();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1){
            return context.getResources().getDrawable(resourceId);
        } else {
            return context.getResources().getDrawable(resourceId, null);
        }
    }

    private static void putResourceIdByTag(String tag, int resouceId){
        s_mapMemoryForViewTag.put(tag, resouceId);
    }

    private static int getResourceIdByTag(String tag){
        if(s_mapMemoryForViewTag.containsKey(tag)){
            return s_mapMemoryForViewTag.get(tag);
        } else {
            return 0;
        }
    }

    private static void clearMap(){
        s_mapMemoryForViewTag.clear();
    }
}
