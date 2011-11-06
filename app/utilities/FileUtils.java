package utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import play.Logger;

public class FileUtils {
    private static final double BYTE = 1024;
    private static final double K_BYTE = 1024 * 1024;
    private static final double M_BYTE = 1024 * 1024 * 1024;
    private static final double G_BYTE = 1024 * 1024 * 1024 * 1024;
    private static final double T_BYTE = 1024 * 1024 * 1024 * 1024 * 1024;

    public static String getReadableSize(Long sizeByte) {
        Double size = 0.0;
        String suffix = "";
        if (sizeByte < BYTE) {
            size = new Double(sizeByte);
        }
        if (sizeByte < K_BYTE) {
            size = new Double(sizeByte / BYTE);
            suffix = "K";
        } else if (sizeByte < M_BYTE) {
            size = new Double(sizeByte / K_BYTE);
            suffix = "M";
        } else if (size < G_BYTE) {
            size = new Double(sizeByte / M_BYTE);
            suffix = "G";
        } else if (size < T_BYTE) {
            size = new Double(sizeByte / G_BYTE);
            suffix = "T";
        }
        String sizeStr = new BigDecimal(size).setScale(1, RoundingMode.HALF_UP).toString();
        if (sizeStr.endsWith(".0")) {
            return sizeStr.substring(0, sizeStr.length() - 2) + suffix;
        } else {
            return sizeStr + suffix;
        }
    }

}
