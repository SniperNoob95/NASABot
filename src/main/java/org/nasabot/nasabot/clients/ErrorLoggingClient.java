package org.nasabot.nasabot.clients;

import org.nasabot.nasabot.NASABot;

public class ErrorLoggingClient {
    private DBClient dbClient;

    private DBClient getDbClient() {
        if (dbClient == null) {
            dbClient = DBClient.getInstance();
        }
        return dbClient;
    }

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
            for (int i = 0; i < Math.min(5, stackTraceElements.length); i++) {
                stringBuilder.append(String.format("%s;", stackTraceElements[i]));
            }
        }

        getDbClient().insertErrorLog(className, method, log, stringBuilder.toString());
    }

    public void handleError(String className, String method, String log, String exceptionClass) {
        getDbClient().insertErrorLog(className, method, log, exceptionClass);
    }

    public void handleError(String className, String method, String log) {
        getDbClient().insertErrorLog(className, method, log);
    }
}
