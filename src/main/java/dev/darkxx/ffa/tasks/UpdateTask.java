package dev.darkxx.ffa.tasks;

import dev.darkxx.ffa.Main;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;

public class UpdateTask {

    public static boolean isOutdated;
    public static int latestVersion;

    public static void run() {
        try {
            String remoteVersion = fetchRemoteVersion();
            if (remoteVersion != null) {
                latestVersion = Integer.parseInt(remoteVersion.replaceAll("[^0-9]", ""));
                String pluginVersion = Main.getInstance().getDescription().getVersion();
                if (!remoteVersion.equals(pluginVersion)) {
                    Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&cThe plugin is not up to date, please update to the latest version, v" + remoteVersion));
                    isOutdated = true;
                } else {
                    Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&aThe plugin is up to date."));
                    isOutdated = false;
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&cFailed to fetch remote version. Please check your internet connection."));
            }
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private static String fetchRemoteVersion() throws IOException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI("https://darkxx.xyz/minecraft/ffa/version.txt"));
            HttpResponse httpResponse = httpClient.execute(httpGet, new BasicHttpContext());
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            return reader.readLine();
        }
    }
}
