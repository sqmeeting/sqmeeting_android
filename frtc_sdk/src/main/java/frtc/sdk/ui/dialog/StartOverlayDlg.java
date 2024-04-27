package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import frtc.sdk.R;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.util.BaseDialog;


public class StartOverlayDlg extends BaseDialog {

    private static final String TAG = StartOverlayDlg.class.getSimpleName();
    private Context mContext;

    private EditText etContent;
    private Switch swScroll;
    private TextView repeatTimesTitle;
    private ConstraintLayout timesLayout;
    private EditText etTimes;
    private ConstraintLayout topLayout;
    private ConstraintLayout centerLayout;
    private ConstraintLayout bottomLayout;
    private ImageView topSelected;
    private ImageView centerSelected;
    private ImageView bottomSelected;

    private ConstraintLayout selectedLayout;

    private IMeetingControlDlgListener listener;

    private final int minValue = 1;
    private final int maxValue = 999;
    private final int maxLength = 1024;
    private int times = 3;

    public StartOverlayDlg(Context context) {
        super(context, R.style.NoMaskDialogTheme);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        }

        setContentView(R.layout.start_overlay_dialog);

        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnComplete = findViewById(R.id.btn_complete);

        etContent = findViewById(R.id.content_input);
        etContent.setText(mContext.getString(R.string.overlay_dlg_content_default_value));

        swScroll = findViewById(R.id.switch_scrolling);
        repeatTimesTitle = findViewById(R.id.repeat_times_title);

        timesLayout = findViewById(R.id.times_layout);
        etTimes = findViewById(R.id.times_input);
        Button btnMinus = findViewById(R.id.minus);
        Button btnAdd = findViewById(R.id.add);

        topLayout = findViewById(R.id.top_layout);
        centerLayout = findViewById(R.id.center_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        topSelected = findViewById(R.id.top_selected);
        centerSelected = findViewById(R.id.center_selected);
        bottomSelected = findViewById(R.id.bottom_selected);
        ImageView top = findViewById(R.id.top);
        ImageView center = findViewById(R.id.center);
        ImageView bottom = findViewById(R.id.bottom);

        swScroll.setChecked(true);
        updateTimesLayoutVisibility(true);

        times = 3;
        etTimes.setText(String.valueOf(times));

        selectedLayout = topLayout;
        updateSelectedPosition(selectedLayout);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOverlay();
            }
        });

        etContent.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
            }
        });

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = etContent.getText().toString().trim();
                if(content.isEmpty()){
                    BaseToast.showToast(mContext, mContext.getString(R.string.overlay_content_empty_notice), Toast.LENGTH_SHORT);
                }

                if(content.length() > maxLength){
                    BaseToast.showToast(mContext, mContext.getString(R.string.overlay_content_length_notice), Toast.LENGTH_SHORT);
                }
            }
        });

        swScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTimesLayoutVisibility(isChecked);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRepeatTimes();
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractRepeatTimes();
            }
        });

        etTimes.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
            }
        });

        etTimes.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etTimes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updateRepeatTimes();
                return false;
            }
        });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLayout = topLayout;
                updateSelectedPosition(selectedLayout);
            }
        });

        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLayout = centerLayout;
                updateSelectedPosition(selectedLayout);
            }
        });

        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLayout = bottomLayout;
                updateSelectedPosition(selectedLayout);
            }
        });

    }

    private void addRepeatTimes(){
        String num = etTimes.getText().toString();
        if(num.isEmpty()){
            etTimes.setText(String.valueOf(times));
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
            return;
        }

        int oldNum = Integer.parseInt(num);
        int newNum = oldNum + 1;

        if(newNum < minValue){
            newNum = minValue;
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
        }
        if(newNum > maxValue){
            newNum = maxValue;
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
        }
        times = newNum;
        etTimes.setText(String.valueOf(newNum));
    }

    private void subtractRepeatTimes(){
        String num = etTimes.getText().toString();
        if(num.isEmpty()){
            etTimes.setText(String.valueOf(times));
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
            return;
        }

        int oldNum = Integer.parseInt(num);
        int newNum = oldNum - 1;

        if(newNum < minValue){
            newNum = minValue;
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
        }
        if(newNum > maxValue){
            newNum = maxValue;
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
        }
        times = newNum;
        etTimes.setText(String.valueOf(newNum));
    }

    private void updateRepeatTimes(){
        String timesString = etTimes.getText().toString();
        if(timesString.isEmpty()){
            etTimes.setText(String.valueOf(times));
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
        }else{
            int newNum = Integer.parseInt(timesString);
            if(newNum < minValue){
                newNum = minValue;
                BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
            }
            if(newNum > maxValue){
                newNum = maxValue;
                BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
            }
            times = newNum;
            etTimes.setText(String.valueOf(newNum));
        }

    }

    private void updateTimesLayoutVisibility(boolean visible){
        if(visible){
            repeatTimesTitle.setVisibility(View.VISIBLE);
            timesLayout.setVisibility(View.VISIBLE);
        }else{
            repeatTimesTitle.setVisibility(View.GONE);
            timesLayout.setVisibility(View.GONE);
        }
    }

    private void updateSelectedPosition(ConstraintLayout selected){
        if(selected == topLayout){
            topLayout.setBackground(mContext.getDrawable(R.drawable.border_selected));
            centerLayout.setBackground(mContext.getDrawable(R.drawable.border_unselected));
            bottomLayout.setBackground(mContext.getDrawable(R.drawable.border_unselected));
            topSelected.setVisibility(View.VISIBLE);
            centerSelected.setVisibility(View.GONE);
            bottomSelected.setVisibility(View.GONE);
        }else if(selected == centerLayout){
            topLayout.setBackground(mContext.getDrawable(R.drawable.border_unselected));
            centerLayout.setBackground(mContext.getDrawable(R.drawable.border_selected));
            bottomLayout.setBackground(mContext.getDrawable(R.drawable.border_unselected));
            topSelected.setVisibility(View.GONE);
            centerSelected.setVisibility(View.VISIBLE);
            bottomSelected.setVisibility(View.GONE);
        }else if(selected == bottomLayout){
            topLayout.setBackground(mContext.getDrawable(R.drawable.border_unselected));
            centerLayout.setBackground(mContext.getDrawable(R.drawable.border_unselected));
            bottomLayout.setBackground(mContext.getDrawable(R.drawable.border_selected));
            topSelected.setVisibility(View.GONE);
            centerSelected.setVisibility(View.GONE);
            bottomSelected.setVisibility(View.VISIBLE);
        }
    }

    private int getPosition(){
        if(selectedLayout == topLayout) {
            return 0;
        }else if (selectedLayout == centerLayout){
            return 50;
        }else if(selectedLayout == bottomLayout){
            return 100;
        }
        return 0;
    }


    private void startOverlay(){
        String content = etContent.getText().toString().trim();
        if(content.isEmpty()){
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_content_empty_notice), Toast.LENGTH_SHORT);
            return;
        }

        if(content.length() > maxLength){
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_content_length_notice), Toast.LENGTH_SHORT);
            return;
        }
        boolean scroll = swScroll.isChecked();
        int position = getPosition();
        int repeat = 1;
        if(scroll){
            String repeatString= etTimes.getText().toString();
            if(repeatString.isEmpty()){
                etTimes.setText(String.valueOf(times));
                BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
                return;
            }else{
                repeat = Integer.parseInt(repeatString);
            }
        }
        if(repeat >= minValue && repeat <= maxValue){
            if(listener != null){
                listener.onStartOverlay(content,repeat,position, scroll);
            }
            dismiss();
        }else{
            BaseToast.showToast(mContext, mContext.getString(R.string.overlay_repeat_value_notice), Toast.LENGTH_SHORT);
        }
    }

    public void setStartOverlayListener(IMeetingControlDlgListener mListener){
        this.listener = mListener;
    }
}
