/**
 * @projectName ebuCodes
 * @package com.engine.fsl.service.impl
 * @className com.engine.fsl.service.impl.Test1ServiceImpl
 * @copyright weaver, Inc All rights reserved.
 */
package com.engine.fsl.service.impl;
import com.engine.fsl.cmd.Test1Cmd;
import com.engine.fsl.service.Test1Service;
import com.engine.core.impl.Service;
import java.util.Map;
/**
 * @BelongsPackage: com.engine.fsl.service.impl
 * @description 测试实现类
 * @author fsl
 * @date 2023/3/17 10:21
 * @version 1.0
 */
public class Test1ServiceImpl extends Service implements Test1Service {
    @Override
    public Map<String, Object> getData(Map<String, Object> params) {
        return commandExecutor.execute(new Test1Cmd(params, user));
    }
}
 