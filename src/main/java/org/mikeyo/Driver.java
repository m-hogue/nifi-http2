package org.mikeyo;

import org.mikeyo.http.HTTPBench;
import org.mikeyo.http2.HTTP2Bench;

public class Driver {

    public static void main(String[] args) throws Exception {
        final HTTP2Bench http2Bench = new HTTP2Bench();
        http2Bench.setNumTests(100);
        http2Bench.run();
        http2Bench.cleanup();

        final HTTPBench httpBench = new HTTPBench();
        httpBench.setNumTests(100);
        httpBench.run();
        httpBench.cleanup();
    }
}
