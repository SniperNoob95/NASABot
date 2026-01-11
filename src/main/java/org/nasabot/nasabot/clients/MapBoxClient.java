package org.nasabot.nasabot.clients;

import net.dv8tion.jda.api.utils.FileUpload;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapBoxClient extends NASABotClient {
    private final String url = "https://api.mapbox.com/styles/v1/mapbox/dark-v11/static/pin-s+ff0000(%s,%s)/%s,%s,2,0/600x600";
    private String token;

    private MapBoxClient() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            token = resourceBundle.getString("mapboxToken");
        } catch (Exception e) {
            errorLoggingClient.handleError("MapBoxClient", "MapBoxClient", "Cannot load MapBox token.", e);
            System.exit(0);
        }
    }

    private static class MapBoxClientSingleton {
        private static final MapBoxClient INSTANCE = new MapBoxClient();
    }

    public static MapBoxClient getInstance() {
        return MapBoxClient.MapBoxClientSingleton.INSTANCE;
    }

    public FileUpload getMapImageForLocation(String longitude, String latitude) {
        String pathName = "ISS-" + System.currentTimeMillis() + ".png";
        try {
            HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(String.format(url, longitude, latitude, longitude, latitude))).newBuilder();
            builder.addQueryParameter("attribution", "false").addQueryParameter("logo", "false").addQueryParameter("access_token", token);
            Request request = new Request.Builder()
                    .url(builder.build())
                    .header("Accept", "image/png")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody body = response.body();
                // Save the image bytes to a file

                File destinationFile = new File(pathName);
                try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = body.byteStream().read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                FileUpload fileUpload = FileUpload.fromData(destinationFile, "image.png").setName(pathName);
                return fileUpload;
            }
        } catch (Exception e) {
            errorLoggingClient.handleError("MapBoxClient", "getMapImageForLocation", "Unable to process MapBox image.", e);
            return null;
        }
    }

    public void deleteFile(String fileName) {
        try {
            Files.delete(Paths.get("./" + fileName));
        } catch (IOException e) {
            errorLoggingClient.handleError("MapBoxClient", "deleteFile", "Unable to delete ISS file.", e);
        }
    }
}
