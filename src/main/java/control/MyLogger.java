package control;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.*;

/**
 * Created by Viktor on 07.01.2016.
 */
public class MyLogger extends Logger {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        StringBuilder builder = new StringBuilder(256);

        builder.append(getDateTime());

        switch (level) {
            case LEVEL_ERROR:
                builder.append(" ERROR: ");
                break;
            case LEVEL_WARN:
                builder.append("  WARN: ");
                break;
            case LEVEL_INFO:
                builder.append("  INFO: ");
                break;
            case LEVEL_DEBUG:
                builder.append(" DEBUG: ");
                break;
            case LEVEL_TRACE:
                builder.append(" TRACE: ");
                break;
        }

        if (category != null) {
            builder.append('[');
            builder.append(category);
            builder.append("] ");
        }

        builder.append(message);

        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            ex.printStackTrace(new PrintWriter(writer));
            builder.append('\n');
            builder.append(writer.toString().trim());
        }

        print(builder.toString());
    }

    @Override
    protected void print(String message) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getYearMonth() + "_" + getUsername() + "@" + getComputerName() + ".txt", true)));
            out.println(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getDateTime() {
        LocalDateTime ldt = LocalDateTime.now();
        return ldt.format(dtf);
    }

    private String getYearMonth() {
        LocalDate ld = LocalDate.now();
        return "" + ld.getYear() + ld.getMonthValue();
    }

    private String getUsername() {
        return System.getProperty("user.name");
    }

    private String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }
}
