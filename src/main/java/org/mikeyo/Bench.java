package org.mikeyo;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Slf4jReporter;

public abstract class Bench {
    private static final Logger LOG = LoggerFactory.getLogger(Bench.class);
    public static final int DEFAULT_NUM_TESTS = 50;
    public static final int PORT = 50051;
    public static final String HTTP_METHOD = "POST";
    public static final String HTTP_BASE_PATH = "contentListener";

    public static final String TEN_B = "byGH3cilE9";
    public static final String ONE_HUNDRED_B = "byGH3cilE9QTzVLcBGFqsnfZegaSy7Cw6UFrWKbclnHZ27E472SomhB2JyTtK1svDk2P5SFsgHnM1crhAOUxe9fLhFATCqkrlpRu";
    public static final String ONE_KB = "7zfntkwKcrHUVqzLKpXNHE2Y9Xrf9a6BYp0DmS2KTwgttgHoBMrBvTf8NxtqXGFhe7puBjLDvgjF8KbIoVgbbkQewUxW2ZJ7a0uTjzc16BnYxILy2gKapu2xQ9MGArfo7ysIUBGtFnqI3w6NOUQzTUlo4wyy99JoW2FjEHafSDWNWKogfBRPY67mMpJNnAUeWrGlfDfghlh19uivpnryiQ2HSoNqE2RcFMWtnyOtJoTGoUjSPKI2khRZ85xuY4NAmxPBVP3CyzZjKUnnjgNLlaFkCPNkPEyYZrvxj3STPcGsWxycUl6HMfRgObkOrfPzFQ2EaP5hHBCJiRUNzB3MEPM15nL0oyqXphVHRELhto1uU1DPO0m41l1Z3G3hR9UZ29vCN8D6mtUxkDH2iD0bVgXlRsGCzEjTJM73N3nXRgt92UIeaAaXCx2Ip8ibYoP1oZYb2I6QDJohZIhCD4olL9SrGMXab3G5ezFc28kYbVTna6L2EEycxBTl5h7LzlqKR96Gj7AF0OWvycCBWnAiwu1Av4KO0TFimtQ46tnp7F31uk6RiRB8gk4pXebEGpp7XmAPMIFmzGE84AUIS5vOP7ROXZODkfH6DZnUXyaAz8qSsYLrUXJbwr2V8O2NnjGttPptyjYx1QlBUvLMXB1kPMp9CeNmTSHBM2gcWcgRsajkj0bwkRkYiwqA5JcpTcmPAAbZOX3XyKNU9yHtFguLvtAQ7ZreqTR2DZKcaIEAuDjDTqmqj2RGgsnZNhebv7Jz8KhoOXoSiNYsV19l09O4beHhUYhHAX29uKGqObqab1RZjFt2ZhW3M1Xqlf5S6gvnVDHX93hgguLLwZQoIbv4R6Xt8cW5EQp9ctkVojy9sLqP8hiIQGQoA4iFUnKT27TxBIqSvBKSDn2xCT9iKqZWboONE3iOm5WGHDR4tc73Z6YUV9j6zRbZoJvbbSSBZYbTYBiVIfsFOCb1Zqk0Hha0Mq79okGwQ3Arzly4hZSDAkBq2QzYx24aCDmlXOTjI0IF";
    public static final String ONE_MB = generateRandomString(ThreadLocalRandom.current(), 1048576);
    public static final String ONE_HUNDRED_MB = generateRandomString(ThreadLocalRandom.current(), 104857600);

    protected final MetricRegistry registry;
    protected final ScheduledReporter reporter;
    private int numTests = DEFAULT_NUM_TESTS;

    public Bench() {
        this.registry = new MetricRegistry();
        this.reporter = Slf4jReporter
                .forRegistry(this.registry)
                .build();
        this.reporter.start(60, TimeUnit.SECONDS);
    }

    public void setNumTests(final int numTests) {
        this.numTests = numTests;
    }

    public int getNumTests() {
        return this.numTests;
    }

    public void cleanup() {
        LOG.info("Done.");
        reporter.report();
        reporter.stop();
    }

    /**
     * generate random alphanumeric string
     */
    public static String generateRandomString(final Random random, final int length){
        return random.ints(48,122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public abstract void run() throws Exception;
}
