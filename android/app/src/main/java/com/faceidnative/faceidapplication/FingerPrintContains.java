package com.faceidnative.faceidapplication;

public class FingerPrintContains {
    //Todo: Help
    private final String FINGERPRINT_ACQUIRED_GOOD = "FINGERPRINT_ACQUIRED_GOOD";
    private final String FINGERPRINT_ACQUIRED_IMAGER_DIRTY = "Vui lòng làm sạch cảm biến vân tay sau đó thử lại!";
    private final String FINGERPRINT_ACQUIRED_TOO_FAST = "Vui lòng giữ ngón tay trên cảm biến lâu hơn!";
    private final String FINGERPRINT_ACQUIRED_TOO_SLOW = "Vui lòng vuốt ngón tay chậm hơn!";
    //Todo: Error
    private final String FINGERPRINT_ERROR_HW_UNAVAILABLE = "Đặt ngón tay tiếp xúc toàn bộ cảm biến";
    private final String FINGERPRINT_ERROR_UNABLE_TO_PROCESS = "Không thể xử lý dấu vân tay";
    private final String FINGERPRINT_ERROR_TIMEOUT = "Xác thực vân tay hết hạn. Vui lòng mở lại ứng dụng để thực hiện!";
    private final String FINGERPRINT_ERROR_LOCKOUT = "Bạn đã vượt quá 5 lần thử. Cảm biến tạm thời bị khóa";
    private final String FINGERPRINT_ERROR_LOCKOUT_PERMANENT = "Xác thực vân tay đã bị khóa do thực hiện sai quá nhiều lần";
    private final String FINGERPRINT_ERROR_NO_FINGERPRINTS = "Không có vân tay nào được đăng ký";
    private final String FINGERPRINT_ERROR_HW_NOT_PRESENT = "Thiết bị không có cảm biến vân tay";
    private final String FINGERPRINT_ERROR = "Không thể xác thực vân tay. Vui lòng thử lại sau!";

    public final String FINGERPRINT_FAILED = "Xác thực vân tay thất bại";
    public final String FINGERPRINT_SUCCESS = "Xác thực vân tay thành công";

    public static String FINGER_PRINT_SUPPORT_NOT_DETECTED ="Thiết bị không hỗ trợ vân tay";
    public static String FINGER_PRINT_DONT_HAVE_PERMISSION ="Chưa được cấp quyền truy cập vân tay trên thiết bị";
    public static String FINGER_PRINT_SUPPORT_ENROLLED_ERROR ="Bạn chưa đăng ký vân tay trên thiết bị";
    public static String FINGER_PRINT_SUPPORT_KEYGUARD_SECURE_NOT_ENABLE = "Bạn chưa bật bảo vệ màn hình khóa";
    public static String FINGER_PRINT_SUPPORT_ERROR = "Xác thực vân tay không khả dụng. Vui lòng thử lại sau!";
    

    public String getHelpMessage(int helpID) {
        switch(helpID){
            case 0: return FINGERPRINT_ACQUIRED_GOOD;
            case 2: return FINGERPRINT_ACQUIRED_IMAGER_DIRTY;
            case 3: return FINGERPRINT_ACQUIRED_IMAGER_DIRTY;
            case 4: return FINGERPRINT_ACQUIRED_TOO_SLOW;
            case 5: return FINGERPRINT_ACQUIRED_TOO_FAST;
            default: return FINGERPRINT_ERROR;
        }
    }
    public String getErrorMessage(int errorID) {
        switch(errorID){
            case 1: return FINGERPRINT_ERROR_HW_UNAVAILABLE;
            case 2: return FINGERPRINT_ERROR_UNABLE_TO_PROCESS;
            case 3: return FINGERPRINT_ERROR_TIMEOUT;
            case 7: return FINGERPRINT_ERROR_LOCKOUT;
            case 9: return FINGERPRINT_ERROR_LOCKOUT_PERMANENT;
            case 11: return FINGERPRINT_ERROR_NO_FINGERPRINTS;
            case 12: return FINGERPRINT_ERROR_HW_NOT_PRESENT;
            default: return FINGERPRINT_ERROR;
        }
    }
    public FingerPrintContains() {}

}
