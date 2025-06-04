package core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }

    public static <T> T retry(RetryableOperation<T> operation) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    logger.info("Retrying in {} ms...", RETRY_DELAY_MS);
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }
        }
        
        logger.error("All {} retry attempts failed", MAX_RETRIES);
        throw lastException;
    }
} 