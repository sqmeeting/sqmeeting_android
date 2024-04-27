package com.frtc.sqmeetingce.ui.picker.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import java.util.List;

public class MonthContentAdapter extends RecyclerView.Adapter<MonthContentAdapter.MonthContentHolder>{
    private Context context;
    private List<String> datas;
    private OnKeyboardClickListener listener;
    private int startDayPosition;
    private String[] dataOfMonth = new String[31];

    public MonthContentAdapter(Context context, List datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public MonthContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        MonthContentHolder holder = new MonthContentHolder(view);
        setListener(holder);
        return holder;
    }

    private void setListener(final MonthContentHolder holder) {
        holder.tvKey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onKeyClick(view, holder, holder.getAdapterPosition());
                }
            }

        });
    }

    @Override
    public void onBindViewHolder(MonthContentHolder holder, int position) {
        holder.tvKey.setText(datas.get(position));
        if(position == startDayPosition){
            holder.tvKey.setBackground(context.getResources().getDrawable(R.drawable.start_day_circle_blue));
            holder.tvKey.setTextColor(context.getResources().getColor(R.color.white));
        }else if(!TextUtils.isEmpty(dataOfMonth[position])) {
            holder.tvKey.setBackground(context.getResources().getDrawable(R.drawable.blue_oval));
        }
    }

    @Override

    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setStartDayPosition(int startDayPosition) {
        this.startDayPosition = startDayPosition;
    }

    public void setDataMonth(String[] dataMonth) {
        System.arraycopy(dataMonth,0,dataOfMonth,0,dataMonth.length);
    }

    class MonthContentHolder extends RecyclerView.ViewHolder {
        public TextView tvKey;
        public RelativeLayout rlDel;
        private View convertView;

        public MonthContentHolder(View itemView) {
            super(itemView);
            convertView = itemView;
            tvKey = itemView.findViewById(R.id.tv_key);
        }

        public View getconvertView() {
            return convertView;
        }

        public void setVisibility(boolean b) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (b) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }

    public interface OnKeyboardClickListener {
        void onKeyClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnKeyboardClickListener(OnKeyboardClickListener listener) {
        this.listener = listener;
    }

}

