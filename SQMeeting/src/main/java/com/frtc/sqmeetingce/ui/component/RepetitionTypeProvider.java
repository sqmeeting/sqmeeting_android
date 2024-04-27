package com.frtc.sqmeetingce.ui.component;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.contract.LinkageProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RepetitionTypeProvider implements LinkageProvider {
    private Context mContext;
    int INDEX_NO_FOUND = -1;

    public RepetitionTypeProvider(Context context) {
        mContext = context;
    }

    @Override
    public boolean firstLevelVisible() {
        return true;
    }

    @Override
    public boolean thirdLevelVisible() {
        return false;
    }

    @NonNull
    @Override
    public List<String> provideFirstData() {
        return Arrays.asList(mContext.getResources().getString(R.string.repetition_type_day), mContext.getResources().getString(R.string.repetition_type_week),
                mContext.getResources().getString(R.string.repetition_type_month));
    }

    @NonNull
    @Override
    public List<String> linkageSecondData(int firstIndex) {
        switch (firstIndex) {
            case 0:
                List<String> data = new ArrayList<>();
                for (int i = 1; i <= 99; i++) {
                    data.add(i + "");
                }
                return data;
            case 1:
            case 2:
                List<String> data1 = new ArrayList<>();
                for (int i = 1; i <= 12; i++) {
                    data1.add(i + "");
                }
                return data1;

        }
        return new ArrayList<>();
    }

    @NonNull
    @Override
    public List<String> linkageThirdData(int firstIndex, int secondIndex) {
        return new ArrayList<>();
    }

    @Override
    public int findFirstIndex(Object firstValue) {
        if(firstValue.equals(mContext.getResources().getString(R.string.repetition_type_day))){
            return 0;
        }else if(firstValue.equals(mContext.getResources().getString(R.string.repetition_type_week))){
            return 1;
        }else if(firstValue.equals(mContext.getResources().getString(R.string.repetition_type_month))){
            return 2;
        }
        return 0;
    }

    @Override
    public int findSecondIndex(int firstIndex, Object secondValue) {
        if (secondValue == null) {
            return INDEX_NO_FOUND;
        }
        List<String> letters = linkageSecondData(firstIndex);
        for (int i = 0, n = letters.size(); i < n; i++) {
            String letter = letters.get(i);
            if (letter.equals(secondValue.toString())) {
                return i;
            }
        }
        return INDEX_NO_FOUND;
    }

    @Override
    public int findThirdIndex(int firstIndex, int secondIndex, Object thirdValue) {
        return 0;
    }

}
