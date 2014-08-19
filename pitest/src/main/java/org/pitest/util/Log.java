package org.pitest.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import java.nio.file.Files;

import java.nio.file.Paths;

public class Log {

  private static final Logger LOGGER = Logger.getLogger("PIT");
  private static BufferedWriter bw = null; // AMIN

  public static Logger getLogger() {
	  if (bw == null){
		  File file = new File("mutation_result.txt");
		  
		  // if file doesnt exists, then create it
		  if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
		  }

		  FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		  
	  }
  
    return LOGGER;
   
  }

  public static void write(String str){
	  try {
		bw.append(str);
		bw.flush();	
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("str");
		e.printStackTrace();
	}
	  
  }
  
  static {
    if ((System.getProperty("java.util.logging.config.file") == null)
        && (System.getProperty("java.util.logging.config.class") == null)) {
      LOGGER.setUseParentHandlers(false);
      final Handler handler = new ConsoleHandler();
      handler.setFormatter(new PlainFormatter());
      addOrSetHandler(handler);
      LOGGER.setLevel(Level.INFO);
      handler.setLevel(Level.ALL);
    }
  }

  private static void addOrSetHandler(final Handler handler) {
    if (LOGGER.getHandlers().length == 0) {
      LOGGER.addHandler(handler);
    } else {
      LOGGER.getHandlers()[0] = handler;
    }
  }

  public static void setVerbose(final boolean on) {
    if (on) {
      setLevel(Level.FINEST);
    } else {
      setLevel(Level.INFO);
    }
  }

  private static void setLevel(final Level level) {

    LOGGER.setLevel(level);
    for (final Handler each : LOGGER.getHandlers()) {
      each.setLevel(level);
    }
  }

  static class PlainFormatter extends Formatter {

    private final static String LINE_SEPARATOR = System
                                                   .getProperty("line.separator");
    private final DateFormat    dateFormat     = DateFormat.getTimeInstance();

    @Override
    public String format(final LogRecord record) {
      final StringBuffer buf = new StringBuffer(180);

      buf.append(this.dateFormat.format(new Date(record.getMillis())));
      buf.append(" PIT >> ");
      buf.append(record.getLevel());
      buf.append(" : ");
      buf.append(formatMessage(record));

      buf.append(LINE_SEPARATOR);

      final Throwable throwable = record.getThrown();
      if (throwable != null) {
        final StringWriter sink = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sink, true));
        buf.append(sink.toString());
      }

      return buf.toString();
    }

  }

  public static boolean isVerbose() {
    return Level.FINEST.equals(LOGGER.getLevel());
  }

}
