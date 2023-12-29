/**
 * @projectName ebuCodes
 * @package com.api.fsl.test.impl
 * @className com.api.fsl.test.impl.TestLogin
 * @copyright weaver, Inc All rights reserved.
 */
package com.api.fsl.test.impl;

import com.weaverboot.frame.ioc.anno.classAnno.WeaSsoIocComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaSsoIoc;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaSsoParam;

/**
 * @BelongsPackage: com.api.fsl.test.impl
 * @description 测试单点登录
 * @author fsl
 * @date 2023/3/17 15:11
 * @version 1.0
 */
@WeaSsoIocComponent("demoService") //如不标注名称，则按类的全路径注入
public class TestLogin {
    //参数weaSsoParam，字段为 request response paramMap
    @WeaSsoIoc(order = 1, description = "单点登录逻辑1")
    public void sso1(WeaSsoParam weaSsoParam){
//        LogTools.info("sso1");
    }
}
