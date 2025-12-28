package org.nasabot.nasabot.clients;

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
        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        if (stackTraceElements.length < 3) {
            for (StackTraceElement element : stackTraceElements) {
                stringBuilder.append(String.format("%s;", element));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(String.format("%s;", stackTraceElements[i]));
            }
        }

        dbClient.insertErrorLog(className, method, log, stringBuilder.toString());
    }

    public void handleError(String className, String method, String log, String exceptionClass) {
        dbClient.insertErrorLog(className, method, log, exceptionClass);
    }
}
