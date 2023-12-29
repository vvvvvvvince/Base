/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.log
 * @className com.weaver.util.dev.log.EcologyLog
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.log;

import weaver.general.BaseBean;

/**
 * @BelongsPackage: com.weaver.util.dev.log
 * @description ecology 日志
 * @author fsl
 * @date 2023/3/30 18:53
 * @version 1.0
 */
public class EcologyLog implements LogFactory{
    private BaseBean baseBean = new BaseBean();

    @Override
    public void debug(Object message) {
        baseBean.writeLog(message);
    }

    @Override
    public void info(Object message) {
        baseBean.writeLog(message);
    }

    @Override
    public void warn(Object message) {
        baseBean.warningLog("",message);
    }

    @Override
    public void error(Object message) {
        error("",new Exception(String.valueOf(message)));
    }

    @Override
    public void error(Object message, Throwable exception) {
        baseBean.errorLog(String.valueOf(message),(Exception) exception);
    }
}
