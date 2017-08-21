package org.mikeyo.http;

import static com.codahale.metrics.MetricRegistry.name;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.mikeyo.Bench;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

public class HTTPBench extends Bench {

    @Override
    public void run() throws Exception {
        final URL url= new URL("http://localhost:" + PORT + "/" + HTTP_BASE_PATH);

        performTest("10B", TEN_B, url);
        performTest("100B", ONE_HUNDRED_B, url);
        performTest("1KB", ONE_KB, url);
        performTest("1MB", ONE_MB, url);
        performTest("100MB", ONE_HUNDRED_MB, url);
    }

    private void performTest(final String friendlyName, final String message, final URL url) throws IOException {
        final Timer timer = registry.timer(name("HTTP=" + friendlyName + "-timer"));
        final Meter meter = registry.meter("HTTP-" + friendlyName + "-meter");
        for(int i = 0; i < getNumTests(); i++) {
            try (final Timer.Context ignored = timer.time()) {
                executeCall(url, message);
                meter.mark();
            }
        }
    }

    private void executeCall(final URL url, final String message) throws IOException {
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(HTTP_METHOD);
        con.setDoOutput(true);
        final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(message);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        if(responseCode != 200) {
            throw new RuntimeException("Oh Snap.");
        }
    }
}
