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

package com.frtc.sqmeetingce.ui.picker.dialog;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * 调试日志工具类
 *
 * @author 贵州山魈羡民 (1032694760@qq.com)
 * @since 2021/3/26 21:34
 */
public final class DialogLog {
    private static final String TAG = "AndroidPicker";
    private static boolean enable = false;

    private DialogLog() {
        super();
    }

    public static void enable() {
        enable = true;
    }

    public static void print(@NonNull Object log) {
        if (!enable) {
            return;
        }
        Log.d(TAG, log.toString());
    }

}
