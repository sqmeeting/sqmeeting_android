package com.frtc.sqmeetingce.ui.picker.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

public class WeekAdapter extends BaseAdapter {
    private ColorScheme colorScheme = new ColorScheme();
    private int startWeekDayPosition = -1;
    private String[] dataOfWeek = new String[7];

    private Context context;
    private String[] DATA;

    public WeekAdapter(Context context) {
        this.context = context;
        DATA = new String[]{
                context.getResources().getString(R.string.sunday), context.getResources().getString(R.string.monday), context.getResources().getString(R.string.tuesday),
                context.getResources().getString(R.string.wednesday), context.getResources().getString(R.string.thursday), context.getResources().getString(R.string.friday),
                context.getResources().getString(R.string.saturday)
        };
    }

    public void setColorScheme(ColorScheme colorScheme) {
        if (colorScheme == null) {
            colorScheme = new ColorScheme();
        }
        this.colorScheme = colorScheme;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return DATA.length;
    }

    @Override
    public Object getItem(int position) {
        return DATA[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*TextView textView = new TextView(parent.getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        int padding = (int) (parent.getResources().getDisplayMetrics().density * 10);
        textView.setPadding(0, padding, 0, padding);
        textView.setText(DATA[position]);
        if(position == startWeekDayPosition){
            textView.setBackground(parent.getResources().getDrawable(R.drawable.week_start_day_blue));
        }else if(!TextUtils.isEmpty(dataOfWeek[position])){
            textView.setBackground(parent.getResources().getDrawable(R.drawable.week_blue));
        }else {
            textView.setBackground(parent.getResources().getDrawable(R.drawable.week_gray));
        }
        textView.setTextColor(0xFFFFFFFF);
        //return textView;*/

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);

            holder = new ViewHolder();
            holder.tvWeek = convertView.findViewById(R.id.tv_weekDay);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvWeek.setText(DATA[position]);
        if(position == startWeekDayPosition){
            holder.tvWeek.setBackground(parent.getResources().getDrawable(R.drawable.week_start_day_blue));
        }else if(!TextUtils.isEmpty(dataOfWeek[position])){
            holder.tvWeek.setBackground(parent.getResources().getDrawable(R.drawable.week_blue));
        }else {
            holder.tvWeek.setBackground(parent.getResources().getDrawable(R.drawable.week_gray));
        }

        holder.tvWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(view, holder, position);
                }
            }
        });

        return convertView;
    }

    public void setStartWeekDay(int startWeekDayPosition) {
        this.startWeekDayPosition = startWeekDayPosition;
    }

    public void setDataWeek(String[] dataWeek) {
        System.arraycopy(dataWeek,0,dataOfWeek,0,dataWeek.length);
    }


    public class ViewHolder {
        public TextView tvWeek;
    }

    private OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, ViewHolder holder, int position);

    }

}
