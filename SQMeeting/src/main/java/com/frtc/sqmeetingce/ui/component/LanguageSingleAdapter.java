package com.frtc.sqmeetingce.ui.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

public class LanguageSingleAdapter extends BaseAdapter {
    private String[] languages;
    private LayoutInflater mInflate;
    private Context mContext;
    private ItemClickListener listener;
    private int currCheckedPos;

    public LanguageSingleAdapter(Context context, String[] languages,
                                 int currCheckedPos, ItemClickListener listener) {
        mContext = context;
        this.languages = languages;
        this.listener = listener;
        this.currCheckedPos = currCheckedPos;
        mInflate = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return languages == null ? 0 : languages.length;
    }

    @Override
    public Object getItem(int position) {
        return languages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        if (contentView == null) {
            contentView = mInflate
                    .inflate(R.layout.language_list_view_item, null);
        }
        View itemLayout = contentView.findViewById(R.id.item_layout);
        TextView nameView = (TextView) contentView.findViewById(R.id.name);
        nameView.setText(languages[position]);
        final CheckBox checkBox = (CheckBox) contentView
                .findViewById(R.id.check_box);
        checkBox.setChecked(currCheckedPos == position);
        itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
                checkBox.setChecked(true);
                currCheckedPos = position;
                notifyDataSetChanged();

            }
        });
        return contentView;
    }

    public interface ItemClickListener {
        void onClick(int pos);
    }
}
