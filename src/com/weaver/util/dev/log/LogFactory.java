/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.log
 * @className com.weaver.util.dev.log.LogFactory
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.log;
/**
 * @BelongsPackage: com.weaver.util.dev.log
 * @description 开发日志工厂  目前集成了三种日志  ecology 日志  集成日志与 自定义日志
 * 为了适配所有日志的参数，只能抽线兼容的方法
 * @author fsl
 * @date 2023/3/30 17:51
 * @version 1.0
 */
public interface LogFactory {
    public void debug(Object message);
    public void info(Object message);
    public void warn(Object message);
    public void error(Object message);
    public void error(Object message,Throwable exception );
}
