package org.nasabot.nasabot.clients;

import org.nasabot.nasabot.NASABot;

public class ErrorLoggingClient {
    private final DBClient dbClient = DBClient.getInstance();

    private ErrorLoggingClient() {
    }

    private static class ErrorLoggingClientSingleton {
        private static final ErrorLoggingClient INSTANCE = new ErrorLoggingClient();
    }

    public static ErrorLoggingClient getInstance() {
        return ErrorLoggingClient.ErrorLoggingClientSingleton.INSTANCE;
    }

    public void handleError(String className, String method, String log, Exception e) {
        if (NASABot.loggingEnabled) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        if (stackTraceElements.length < 3) {
            for (StackTraceElement element : stackTraceElements) {
                stringBuilder.append(String.format("%s;", element));
            }
        } else {
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(String.format("%s;", stackTraceElements[i]));
            }
        }

        dbClient.insertErrorLog(className, method, log, stringBuilder.toString());
    }

    public void handleError(String className, String method, String log, String exceptionClass) {
        dbClient.insertErrorLog(className, method, log, exceptionClass);
    }

    public void handleError(String className, String method, String log) {
        dbClient.insertErrorLog(className, method, log);
    }
}
