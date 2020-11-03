package Test;

public class TimeFormateTest {
    public static void main(String[] args) {
        String pattern ="\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        System.out.println("0000-00-00 00:00:00".matches(pattern));

        pattern ="\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}";
        System.out.println("0000-00-00 00:00".matches(pattern));
    }

    public static String matchDateTimeFormat(String datatime) {
        String result = null;
        if (datatime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            result = "yyyy-MM-dd HH:mm:ss";
        }
        else if (datatime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            result = "yyyy-MM-dd HH:mm";
        }
        return result;
    }

    public static String matchTimeFormat(String datatime) {
        String result = null;
        if (datatime.matches("\\d{2}:\\d{2}:\\d{2}")) {
            result = "HH:mm:ss";
        }
        else if (datatime.matches("\\d{2}:\\d{2}")) {
            result = "HH:mm";
        }
        return result;
    }
}
