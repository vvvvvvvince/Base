/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.log
 * @className com.weaver.util.dev.log.DebLog
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.log;

import weaver.general.BaseBean;

/**
 * @BelongsPackage: com.weaver.util.dev.log
 *
 * ##自定义开发日志配置
 * log4j.logger.dev=INFO,ERROR,dev
 * log4j.appender.dev=org.apache.log4j.DailyRollingFileAppender
 * log4j.appender.dev.DatePattern='_'yyyyMMdd'.log'
 * log4j.appender.dev.File=@dev/dev.log
 * log4j.appender.dev.layout=org.apache.log4j.PatternLayout
 * log4j.appender.dev.layout.ConversionPattern=%d{yyyy-MM-dd HH\:mm\:ss,SSS} %-5p [Thread\:%t] %m%n
 * log4j.additivity.dev=false
 *
 * @description 自定义开发日志
 * @author fsl
 * @date 2023/3/30 19:29
 * @version 1.0
 */
public class DevLog implements LogFactory{

    //Logger logger = LoggerFactory.getLogger("debug"); 该方法不同客户环境不能很好兼容
    BaseBean bean  = new BaseBean();


    @Override
    public void debug(Object message) {
        bean.debugLog("dev",message);
    }

    @Override
    public void info(Object message) {
        bean.infoLog("dev",message);
    }

    @Override
    public void warn(Object message) {
        bean.warningLog("dev",message);
    }

    @Override
    public void error(Object message) {
        bean.errorLog("dev",new Exception(String.valueOf(message)));
    }

    @Override
    public void error(Object message, Throwable exception) {
        bean.errorLog("dev",(Exception) exception);
    }
}
