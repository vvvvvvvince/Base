/**
 * @projectName ebuCodes
 * @package com.engine.fsl.web
 * @className com.engine.fsl.web.Test1Action
 * @copyright weaver, Inc All rights reserved.
 */
package com.engine.fsl.web;

import com.alibaba.fastjson.JSON;
import com.engine.common.util.ParamUtil;
import com.engine.common.util.ServiceUtil;
import com.engine.fsl.service.Test1Service;
import com.engine.fsl.service.impl.Test1ServiceImpl;
import com.engine.workflow.util.CommonUtil;
import weaver.hrm.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * @BelongsPackage: com.engine.fsl.web
 * @description 测试action
 * @author fsl
 * @date 2023/3/17 10:18
 * @version 1.0
 */
public class Test1Action {

    private Test1Service getService(HttpServletRequest request, HttpServletResponse response){
        User user = CommonUtil.getUserByRequest(request,response);
        return (Test1ServiceImpl) ServiceUtil.getService(Test1ServiceImpl.class, user);
    }

    /** 获取查询条件 **/
    @POST
    @Path("/getTestData")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCondition(@Context HttpServletRequest request, @Context HttpServletResponse response){
        Map<String,Object> apidatas = getService(request, response).getData(ParamUtil.request2Map(request));
        return JSON.toJSONString(apidatas);
    }

}
 