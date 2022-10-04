package org.nasabot.nasabot.utils;

import org.nasabot.nasabot.NASABot;

public class ErrorLogging {

    public static void handleError(String className, String method, String log, Exception e) {
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

        NASABot.dbClient.insertErrorLog(className, method, log, stringBuilder.toString());
    }
}
