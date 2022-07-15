import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Util {

    interface Command {
        public void run();
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
