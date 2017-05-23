package com.central1.profiler;


import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by delliot on 2017-05-12.
 */
public class HardwareInvestigator {
    private static final double BILLION = 1000000000.0;

    private SystemInfo system;
    private Map<String, String> sysInfo;




    HardwareInvestigator() {
        this.system = new SystemInfo();
    }

    public Map<String, String> getSysInfo () {
        return sysInfo;
    }

    public void buildSysInfo() {
        HardwareAbstractionLayer hardware = system.getHardware();
        CentralProcessor cpu = hardware.getProcessor();
        GlobalMemory mem = hardware.getMemory();
        long totalMem = mem.getTotal();

        sysInfo = new HashMap<String, String>();

        sysInfo.put("memory", humanReadableByteCount(totalMem, false));
        sysInfo.put("cpu_model", cpu.getName());
        sysInfo.put("cpu_freq", (String.format("%.1fGHz", cpu.getVendorFreq() / BILLION)));
    }


    //credit aioobe at
    //http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java/4888400?stw=2
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


}
