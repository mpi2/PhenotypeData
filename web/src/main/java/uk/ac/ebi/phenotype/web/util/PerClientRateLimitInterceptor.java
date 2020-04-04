package uk.ac.ebi.phenotype.web.util;

import io.github.bucket4j.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PerClientRateLimitInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    // Cache the most recent 50 hosts and apply rate limiting
    public static final int MAX_SIZE = 50;

    // MAX number of gene page requests from one host in a minute
    public static final long MAX_GENE_PAGE_REQUESTS = 10;

    // MIN number of seconds between gene page requests from one host
    public static final long DELAY_GENE_PAGE_REQUESTS = 2;


    private final Map<String, Bucket> buckets = Collections.synchronizedMap(new LinkedHashMap<String, Bucket>(MAX_SIZE + 1, .75F, true) {
        // This method is called just after a new entry has been added
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_SIZE;
        }
    });


    /**
     * Rate limit the requests to one every 3 seconds and no more than 1 per second
     */
    private static Bucket rateLimitBucket() {
        return Bucket4j.builder()
                .addLimit(
                        Bandwidth.classic(MAX_GENE_PAGE_REQUESTS,
                                Refill.greedy(MAX_GENE_PAGE_REQUESTS, Duration.ofMinutes(1)))
                                .withInitialTokens(MAX_GENE_PAGE_REQUESTS))
                .addLimit(Bandwidth.classic(1,
                        Refill.greedy(1, Duration.ofSeconds(DELAY_GENE_PAGE_REQUESTS))))
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {

        if (log.isDebugEnabled()) {
            // Avoid concurrent modification exception
            synchronized (this.buckets) {
                log.debug("Buckets: " + String.join(", ", this.buckets.keySet()));
            }
        }

        String xForwarded = request.getHeader("x-forwarded-for");
        String host = StringUtils.isNotEmpty(xForwarded) ? xForwarded : request.getHeader("host");
        Bucket requestBucket = this.buckets.computeIfAbsent(host, key -> rateLimitBucket());

        log.debug("Using host: " + host + " With bucket: " + requestBucket);

        ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    Long.toString(probe.getRemainingTokens()));
            return true;
        }

        final String waitTime = Long.toString(TimeUnit.NANOSECONDS.toMillis(probe.getNanosToWaitForRefill()));
        log.info(String.format("Rate limiting request for page: %s\n  From host: %s\n  User-Agent: %s\n  For: %sms",
                request.getRequestURI(), host, request.getHeader("User-Agent"), waitTime));

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
        response.addHeader("X-Rate-Limit-Retry-After-Milliseconds", waitTime);

        return false;
    }


}