package ci.ui.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by kevincheng on 2017/9/20.
 */

public class TextHandle {

    public interface ICallBack {
        void onClick();
    }

    public static void initTermsAndConditionsTextFormat(TextView view,
                                                  String msg,
                                                  final ICallBack callBack) {
        SpannableString        spannableString = new SpannableString(msg);
        int                    TextHeadIndex   = -1;
        int                    TextTailIndex   = -1 ;
        int                    AdjustTextIndex = 0;
        SpannableStringBuilder stringBuilder   = new SpannableStringBuilder(spannableString.toString().replace("/", ""));
        while(true){
            TextHeadIndex  = spannableString.toString().indexOf("/", TextTailIndex + 1);
            TextTailIndex  = spannableString.toString().indexOf("/", TextHeadIndex + 1);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    callBack.onClick();
                }
            };
            if(-1 < TextHeadIndex && -1 < TextTailIndex){

                int HeadIndex = TextHeadIndex - AdjustTextIndex;
                int TailIndex = TextTailIndex - (AdjustTextIndex + 1);
                stringBuilder.setSpan(clickableSpan,
                                      HeadIndex,
                                      TailIndex,
                                      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),
                                      HeadIndex,
                                      TailIndex,
                                      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
                                      HeadIndex,
                                      TailIndex,
                                      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                AdjustTextIndex = AdjustTextIndex + 2 ;
            } else {
                break;
            }
        }
        view.setText(stringBuilder);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
