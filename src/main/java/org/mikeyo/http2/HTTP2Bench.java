package org.mikeyo.http2;

import static com.codahale.metrics.MetricRegistry.name;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.thread.Invocable;
import org.mikeyo.Bench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

public class HTTP2Bench extends Bench {
    private static final Logger LOG = LoggerFactory.getLogger(HTTP2Bench.class);

    @Override
    public void run() throws Exception {
        final HTTP2Client client = new HTTP2Client();
        client.start();

        // we don't get any responses, so no need to inspect them.
        final Stream.Listener responseListener = new Stream.Listener.Adapter(){};
        final MetaData.Request request = new MetaData.Request(HTTP_METHOD,
                new HttpURI("http://localhost:" + PORT + "/" + HTTP_BASE_PATH),
                HttpVersion.HTTP_2,
                new HttpFields());
        final HeadersFrame headersFrame = new HeadersFrame(request, null, false);

        performTest("10B", TEN_B, client, headersFrame, responseListener);
        performTest("100B", ONE_HUNDRED_B, client, headersFrame, responseListener);
        performTest("1KB", ONE_KB, client, headersFrame, responseListener);
        performTest("1MB", ONE_MB, client, headersFrame, responseListener);
        performTest("100MB", ONE_HUNDRED_MB, client, headersFrame, responseListener);

        client.stop();
    }

    private void performTest(final String friendlyName,
                             final String message,
                             final HTTP2Client client,
                             final HeadersFrame headersFrame,
                             final Stream.Listener responseListener) throws InterruptedException, ExecutionException, TimeoutException {
        final Timer timer = registry.timer(name("HTTP2-" + friendlyName + "-timer"));
        final Meter meter = registry.meter(name("HTTP2-" + friendlyName + "-meter"));
        for(int i = 0; i < getNumTests(); i++) {
            final ByteBuffer content = ByteBuffer.wrap(message.getBytes());
            try (final Timer.Context ignored = timer.time()) {
                executeCall(client, headersFrame, responseListener, content);
                meter.mark();
            }
        }
    }

    private void executeCall(final HTTP2Client client,
                             final HeadersFrame headersFrame,
                             final Stream.Listener responseListener,
                             final ByteBuffer content) throws InterruptedException, ExecutionException, TimeoutException {

        final FuturePromise<Session> sessionPromise = new FuturePromise<>();
        client.connect(new InetSocketAddress("localhost", PORT), new ServerSessionListener.Adapter(), sessionPromise);
        final Session session = sessionPromise.get(5, TimeUnit.SECONDS);
        final FuturePromise<Stream> streamPromise = new FuturePromise<>();
        session.newStream(headersFrame, streamPromise, responseListener);
        final Stream stream = streamPromise.get(5, TimeUnit.SECONDS);
        final DataFrame requestContent = new DataFrame(stream.getId(), content, true);
        final Callback.Completable completable = new Callback.Completable(Invocable.InvocationType.NON_BLOCKING);
        stream.data(requestContent, completable);
        completable.get();
    }
}
