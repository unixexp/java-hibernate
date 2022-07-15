import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Util {

    interface Command {
        public void run();
    }

    public static String timestampToString(Timestamp timestamp, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(timestamp.toLocalDateTime());
    }

    public static Timestamp stringToTimestamp(String timestampString, String pattern) {
        try {
            return new Timestamp(new SimpleDateFormat(pattern).parse(timestampString).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String datetimeStringFromTimestamp(Timestamp timestamp) {
        return timestampToString(timestamp, "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateStringFromTimestamp(Timestamp timestamp) {
        return timestampToString(timestamp, "yyyy-MM-dd");
    }

    public static Timestamp datetimeStringToTimestamp(String datetimeString) {
        return stringToTimestamp(datetimeString, "yyyy-MM-dd HH:mm:ss");
    }

    public static Timestamp dateStringToTimestamp(String datetimeString) {
        return stringToTimestamp(datetimeString, "yyyy-MM-dd");
    }

    public static void waitBeforeRun(Integer delaySec, String description, Command command) {
        System.out.println(new StringBuilder()
                .append("Waiting ")
                .append(delaySec)
                .append(" seconds before run '")
                .append(description)
                .append("'")
        );

        Integer sec = 0;
        while (sec < delaySec) {
            try {
                Thread.sleep(1000);
                sec += 1;
            } catch (InterruptedException e) {
                System.out.println("Process was interrupted.");
            }
        }

        System.out.println(description);
        command.run();
    }

}
