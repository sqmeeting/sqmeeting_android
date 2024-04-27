package frtc.sdk.util;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;


public class BaseDialog extends Dialog{
    private Context mContext;

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        if(!LanguageUtil.isSharePreferenceLan(mContext)) {
            LanguageUtil.setLanguage(mContext);
        }
    }

    @Override
    public void dismiss(){
        super.dismiss();
        if(!LanguageUtil.isSharePreferenceLan(mContext)) {
            LanguageUtil.setLanguage(mContext);
        }
    }
}
