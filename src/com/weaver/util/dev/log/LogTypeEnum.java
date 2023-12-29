/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.log
 * @className com.weaver.util.dev.log.LogTypeEnum
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.log;
import weaver.integration.logging.Logger;
import weaver.general.BaseBean;
/**
 * @BelongsPackage: com.weaver.util.dev.log
 * @description 日志类型枚举
 * @author fsl
 * @date 2023/3/30 17:53
 * @version 1.0
 */
public enum LogTypeEnum {

    /**
     * @description: 0 ecology日志 /log/ecology  1 集成日志 /log/integration/integration.log  2 开发日志 /log/dev/dev.log
     **/
    ECOLOGY(0, BaseBean.class),
    INTEGRATION(1,Logger.class),
    DEV(2,org.slf4j.Logger.class);

    public final Integer type;
    public final Class name;

    LogTypeEnum(Integer type,Class name){
        this.type = type;
        this.name = name;
    }

}
