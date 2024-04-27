/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.frtc.sqmeetingce.ui.picker.wheelpicker;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;

import com.frtc.sqmeetingce.ui.picker.dialog.ModalDialog;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.contract.OnDatimePickedListener;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.DatimeWheelLayout;

/**
 * 日期时间选择器
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/6/5 18:21
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DatimePicker extends ModalDialog {
    protected DatimeWheelLayout wheelLayout;
    private OnDatimePickedListener onDatimePickedListener;

    public DatimePicker(@NonNull Activity activity) {
        super(activity);
    }

    public DatimePicker(@NonNull Activity activity, @StyleRes int themeResId) {
        super(activity, themeResId);
    }

    @NonNull
    @Override
    protected View createBodyView() {
        wheelLayout = new DatimeWheelLayout(activity);
        return wheelLayout;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected void onOk() {
        if (onDatimePickedListener != null) {
            int year = wheelLayout.getSelectedYear();
            int month = wheelLayout.getSelectedMonth();
            int day = wheelLayout.getSelectedDay();
            int hour = wheelLayout.getSelectedHour();
            int minute = wheelLayout.getSelectedMinute();
            int second = wheelLayout.getSelectedSecond();
            onDatimePickedListener.onDatimePicked(year, month, day, hour, minute, second);
        }
    }

    public void setOnDatimePickedListener(OnDatimePickedListener onDatimePickedListener) {
        this.onDatimePickedListener = onDatimePickedListener;
    }

    public final DatimeWheelLayout getWheelLayout() {
        return wheelLayout;
    }

}
