package frtc.sdk.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class CPUFilter implements FileFilter {

    private final static String cpu_regex = "cpu[0-9]";
    @Override
    public boolean accept(File file) {
        return Pattern.matches(cpu_regex, file.getName());
    }
}