package frtc.sdk.util;

import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import frtc.sdk.log.Log;

public class DeviceUtil {
    private static final String TAG = DeviceUtil.class.getSimpleName();
    private final static String cpuinfo_max_freq = "/sys/devices/system/cpu/cpu%s/cpufreq/cpuinfo_max_freq";
    private final static String cpu_core_file_path = "/sys/devices/system/cpu/";

    private final static String BRAND_HUAWEI = "HUAWEI";
    private final static String BRAND_QCOM = "qcom";
    private final static String BRAND_MT = "mt";
    private final static String CPU_KIRIN = "kirin";
    private final static String CPU_9000E = "9000e";

    private final static int CPU_980 = 980;

    private static final int defaultCoreNum = 1;
    private static final int level_1 = 1;
    private static final int level_2 = 2;
    private static final int level_3 = 3;

    public static int getCPUCoreNum() {
        try {
            File dir = new File(cpu_core_file_path);
            File[] files = dir.listFiles(new CPUFilter());
            if (files != null) {
                return files.length;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception:"+e);
            return defaultCoreNum;
        }
        return defaultCoreNum;
    }

    private static int getCPUInfoMaxFreq() {
        int f0 = readFile(String.format(cpuinfo_max_freq, 0));
        int f1 = 0;
        int androidCpuCount = DeviceUtil.getCPUCoreNum();
        if (androidCpuCount > 4) {
            f1 = readFile(String.format(cpuinfo_max_freq, androidCpuCount - 1));
        }
        return Math.max(f0, f1);
    }

    private static int readFile(String path) {
        int r = 0;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String text = bufferedReader.readLine();
            r = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            Log.e(TAG,"FileNotFoundException:"+e);
        } catch (Exception e) {
            Log.e(TAG,"Exception:"+e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG,"IOException:"+e);
                }
            }
        }
        return r;
    }

    public static int getCPUCapabilityLevel() {
        int level = level_3;
        String brand = Build.BRAND;
        String hardware = Build.HARDWARE;
        String hardwareBrand = "";
        String hardwareNum = "";
        if (brand.equals(BRAND_HUAWEI)) {
            if (hardware.length() >= 8) {
                hardwareBrand = hardware.substring(0, 5);
                hardwareNum = hardware.substring(5, hardware.length());
                if (CPU_KIRIN.equals(hardwareBrand)) {
                    try {
                        int nHardwareNum = Integer.parseInt(hardwareNum);
                        if (nHardwareNum >= CPU_980) {
                            level = level_2;
                        } else {
                            level = level_3;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception:"+ e.getMessage());
                        if(CPU_9000E.equalsIgnoreCase(hardwareNum)) {
                            level = level_2;
                        }
                    }
                }
                return level;
            }
        }
        if (!hardware.isEmpty() && (hardware.length() >= 6) && BRAND_MT.equals(hardware.substring(0, 2))) {
            hardwareBrand = hardware.substring(0, 2);
            hardwareNum = hardware.substring(2, hardware.length());
            if (hardwareBrand.equals(BRAND_MT)) {
                int nHardwareNum = Integer.parseInt(hardwareNum);
                if (nHardwareNum >= 6885) {
                    level = 2;
                } else {
                    level = 3;
                }
            }
            return level;
        }
        int cpuFreq = getCPUInfoMaxFreq();
        int cpuCoreNum = DeviceUtil.getCPUCoreNum();
        if (BRAND_QCOM.equals(hardwareBrand)) {
            if (cpuFreq >= 2800000) {
                level = 2;
            } else {
                level = 3;
            }
            return level;
        }
        level = 3;
        if (cpuCoreNum >= 8) {
            if (cpuFreq >= 2500000) {
                level = 2;
            } else {
                level = 3;
            }
            return level;
        }
        return level;
    }

}
