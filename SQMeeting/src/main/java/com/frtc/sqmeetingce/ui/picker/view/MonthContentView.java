package com.frtc.sqmeetingce.ui.picker.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.frtc.sqmeetingce.R;

import java.util.ArrayList;
import java.util.List;

public class MonthContentView extends RelativeLayout {

    private RecyclerView recyclerView;
    private List<String> datas;
    private MonthContentAdapter adapter;


    public MonthContentView(Context context) {
        this(context, null);
    }

    public MonthContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_month_content, this);
        recyclerView = findViewById(R.id.recycler_view);

        initData();
        initView();
    }

    // 填充数据
    private void initData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            datas.add(String.valueOf(i + 1));
        }
    }

    // 设置适配器
    private void initView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        adapter = new MonthContentAdapter(getContext(), datas);
        recyclerView.setAdapter(adapter);
    }

    public void setOnKeyBoardClickListener(MonthContentAdapter.OnKeyboardClickListener listener) {
        adapter.setOnKeyboardClickListener(listener);
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setStartDayPosition(int startDayPosition) {
        adapter.setStartDayPosition(startDayPosition);
    }

    public void setDataMonth(String[] dataMonth) {
        adapter.setDataMonth(dataMonth);
    }
}
