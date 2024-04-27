package frtc.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import frtc.sdk.R;
import frtc.sdk.log.Log;

public class FixItemListView extends ListView {

    protected final String TAG = this.getClass().getSimpleName();
    private int mMaxItemCount;
    private float mMaxHeight = 700;

    public FixItemListView(Context context) {
        super(context);
    }

    public FixItemListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixItemListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMaxHeight = dip2px(context, 240);
        Log.d(TAG, "FixItemListView  mMaxHeight = "+mMaxHeight);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ConstraintHeightListView, 0, defStyleAttr);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int type = array.getIndex(i);
            if (type == R.styleable.ConstraintHeightListView_maxHeight) {
                mMaxHeight = array.getDimension(type, -1);
                Log.d(TAG, "FixItemListView 1 mMaxHeight = "+mMaxHeight);
            }
        }
        array.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure  mMaxHeight = "+mMaxHeight + ",  specSize = "+specSize);
        if (mMaxHeight <= specSize && mMaxHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(mMaxHeight).intValue(),
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void setFixItemCount(int count){
        this.mMaxItemCount = count;
        resetListViewHeight();
    }

    private void resetListViewHeight(){
        ListAdapter  listAdapter = getAdapter();
        if(listAdapter == null || mMaxItemCount == 0 || listAdapter.getCount() == 0){
            return;
        }
        View itemView = listAdapter.getView(0, null, this);
        itemView.measure(0,0);
        int itemHeight = itemView.getMeasuredHeight();
        int itemCount = listAdapter.getCount();
        Log.d(TAG, "itemHeight = "+itemHeight + ", itemCount = "+itemCount + ", mMaxItemCount = "+mMaxItemCount);
        LinearLayout.LayoutParams layoutParams = null;
        if(itemCount <= mMaxItemCount) {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            Log.d(TAG, "itemHeight*mMaxItemCount = "+itemHeight*mMaxItemCount);
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,itemHeight*mMaxItemCount);
        }
        setLayoutParams(layoutParams);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}