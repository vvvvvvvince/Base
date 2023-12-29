/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.log
 * @className com.weaver.util.dev.log.IntegrationLog
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.log;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
/**
 * @BelongsPackage: com.weaver.util.dev.log
 * @description 集成日志
 * @author fsl
 * @date 2023/3/30 19:41
 * @version 1.0
 */
public class IntegrationLog implements LogFactory {
    public static Logger logger = LoggerFactory.getLogger(IntegrationLog.class);
    @Override
    public void debug(Object message) {
        logger.debug(message);
    }

    @Override
    public void info(Object message) {
        logger.info(message);
    }

    @Override
    public void warn(Object message) {
        logger.warn(message);
    }

    @Override
    public void error(Object message) {
        logger.error(message);

    }

    @Override
    public void error(Object message, Throwable exception) {
        logger.error(message,exception);
    }
}
