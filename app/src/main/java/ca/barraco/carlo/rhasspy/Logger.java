package ca.barraco.carlo.rhasspy;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

@SuppressWarnings("unused")
public class Logger {
    private static final String TAG = "Rhasspy";
    public static final int RELATIVE_STACK_DEPTH = 4;

    private static boolean enabled = true;

    private Logger() {
    }

    public static void debug(String message, Object... args) {
        if (isDisabled()) {
            return;
        }
        String finalMessage = getFinalLogMessage(message, args);
        Log.d(TAG, finalMessage);
    }

    public static void information(String message, Object... args) {
        if (isDisabled()) {
            return;
        }
        String finalMessage = getFinalLogMessage(message, args);
        Log.i(TAG, finalMessage);
    }

    public static void warning(String message, Object... args) {
        if (isDisabled()) {
            return;
        }
        String finalMessage = getFinalLogMessage(message, args);
        Log.w(TAG, finalMessage);
    }

    public static void error(String message, Object... args) {
        if (isDisabled()) {
            return;
        }
        String finalMessage = getFinalLogMessage(message, args);
        Log.e(TAG, finalMessage);
    }

    public static void error(String message, Exception exception, Object... args) {
        if (isDisabled()) {
            return;
        }
        String finalMessage = getFinalLogMessage(message, args);
        Log.e(TAG, finalMessage, exception);
    }

    @NonNull
    private static String getFinalLogMessage(String message, Object[] args) {
        // build the actual log message
        String formattedMessage = String.format(message, args);

        // extracts the information about what class and what method created the log
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[RELATIVE_STACK_DEPTH];
        String[] classNameParts = stackTraceElement.getClassName().split("\\.");
        String className;
        if (classNameParts.length == 0) {
            className = stackTraceElement.getClassName();
        } else {
            className = classNameParts[classNameParts.length - 1];
        }

        return String.format(
                Locale.getDefault(),
                "%s.%s: %s (%s:%d)",
                className,
                stackTraceElement.getMethodName(),
                formattedMessage,
                // makes the log message clickable in Android Studio Logcat window!
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber());
    }

    public static boolean isDisabled() {
        return !enabled;
    }

    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }
}
