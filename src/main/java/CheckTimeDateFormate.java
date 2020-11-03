public class CheckTimeDateFormate {
    public static String matchDateTimeFormat(String datatime) {
        String result = null;
        if (datatime.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s{1,}\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            result = "yyyy-MM-dd HH:mm:ss";
        }
        else if (datatime.matches("\\d{4}/\\d{1,2}/\\d{1,2}\\s{1,}\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            result = "yyyy/MM/dd HH:mm:ss";
        }
        else if (datatime.matches("\\d{4}.\\d{1,2}.\\d{1,2}\\s{1,}\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            result = "yyyy.MM.dd HH:mm:ss";
        }
        else if (datatime.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s{1,}\\d{1,2}:\\d{1,2}")) {
            result = "yyyy-MM-dd HH:mm";
        }
        else if (datatime.matches("\\d{4}/\\d{1,2}/\\d{1,2}\\s{1,}\\d{1,2}:\\d{1,2}")) {
            result = "yyyy/MM/dd HH:mm";
        }
        else if (datatime.matches("\\d{4}.\\d{1,2}.\\d{1,2}\\s{1,}\\d{1,2}:\\d{1,2}")) {
            result = "yyyy.MM.dd HH:mm";
        }
        return result;
    }

    public static String matchDateFormat(String data) {
        String result = null;
        if (data.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            result = "yyyy-MM-dd";
        }
        else if (data.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
            result = "yyyy/MM/dd";
        }
        else if (data.matches("\\d{4}.\\d{1,2}.\\d{1,2}")) {
            result = "yyyy.MM.dd";
        }
        else if (data.matches("\\d{4}")) {
            result = "yyyy";
        }
        return result;
    }

    public static String matchTimeFormat(String datatime) {
        String result = null;
        if (datatime.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            result = "HH:mm:ss";
        }
        else if (datatime.matches("\\d{1,2}:\\d{1,2}")) {
            result = "HH:mm";
        }
        return result;
    }
}
