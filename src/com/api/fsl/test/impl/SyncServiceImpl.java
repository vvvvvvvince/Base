/**
 * @projectName ebuCodes
 * @package com.api.fsl.test.impl
 * @className com.api.fsl.test.impl.SyncServiceImpl
 * @copyright weaver, Inc All rights reserved.
 */
package com.api.fsl.test.impl;

import com.weaverboot.frame.ioc.anno.classAnno.WeaPageLoadComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaPageLoad;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaSyncParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsPackage: com.api.fsl.test.impl
 * @description 全局数据测试
 * @author fsl
 * @date 2023/3/16 16:29
 * @version 1.0
 */
@WeaPageLoadComponent("")
public class SyncServiceImpl {
    @WeaPageLoad(order = 1, description = "执行方法1")
    public Map<String, Object> Sync1(WeaSyncParam weaSyncParam) {
        Map<String, Object> map = weaSyncParam.getResponseParamMap();
        if (map == null)
            map = new HashMap<>();
        map.put("UUID1", "value1");
        System.out.println("执行sync方法1");
        return map;
    }
    @WeaPageLoad(order = 2, description = "执行方法2")
    public Map<String, Object> Sync2(WeaSyncParam weaSyncParam) {
        Map<String, Object> map = weaSyncParam.getResponseParamMap();
        if (map == null)
            map = new HashMap<>();
        map.put("UUID2", "value2");
        System.out.println("执行sync方法2");
        return map;
    }
}
