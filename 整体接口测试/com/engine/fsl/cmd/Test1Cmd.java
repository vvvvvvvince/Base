/**
 * @projectName ebuCodes
 * @package com.engine.fsl.cmd
 * @className com.engine.fsl.cmd.Test1Cmd
 * @copyright weaver, Inc All rights reserved.
 */
package com.engine.fsl.cmd;
import com.engine.common.biz.AbstractCommonCommand;
import com.engine.common.biz.SimpleBizLogger;
import com.engine.common.constant.BizLogSmallType4Workflow;
import com.engine.common.constant.BizLogType;
import com.engine.common.entity.BizLogContext;
import com.engine.core.interceptor.CommandContext;
import weaver.general.Util;
import weaver.hrm.User;

import java.util.List;
import java.util.Map;
/**
 * @BelongsPackage: com.engine.fsl.cmd
 * @description 测试1cmd
 * @author fsl
 * @date 2023/3/17 10:28
 * @version 1.0
 */
public class Test1Cmd extends AbstractCommonCommand<Map<String,Object>> {

    private SimpleBizLogger logger;

    public Test1Cmd(Map<String,Object> params, User user){
        this.params = params;
        this.user = user;
        this.logger = new SimpleBizLogger();
    }

    /**
     * 批量记录日志
     */
    @Override
    public List<BizLogContext> getLogContexts() {
    //计算修改记录并返回， 注意， 必须在业务代码执行完毕后调用，否则本次操作记录不会被记录
        return logger.getBizLogContexts();
    }

    @Override
    public BizLogContext getLogContext() {
        return null;
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        this.bofore();

        return null;
    }

    /**
     * 处理日志
     */
    public void bofore(){
        BizLogContext bizLogContext = new BizLogContext();
        bizLogContext.setLogType(BizLogType.WORKFLOW_ENGINE);//模块类型
        bizLogContext.setBelongType(BizLogSmallType4Workflow.WORKFLOW_ENGINE_PATH_PATHSET_NODESET);//所属大类型
        bizLogContext.setBelongTypeTargetId(Util.null2String(params.get("nodeid")));//所属大类型id
        bizLogContext.setBelongTypeTargetName(Util.null2String(params.get("nodename")));//所属大类型名称示例 2 使用SimpleBizLogger进行主日志批量记录
        bizLogContext.setLogSmallType(BizLogSmallType4Workflow.WORKFLOW_ENGINE_OPERATORSET);//当前小类型
        logger.setUser(user);//当前操作人
        logger.setParams(params);//request请求参数(request2Map)
        String mainSql = "select id,groupname from workflow_nodegroup where nodeid = " +
                Util.getIntValue(Util.null2String(params.get("nodeid")));
        logger.setMainSql(mainSql);//主表sql
        logger.setMainPrimarykey("id");//主日志表唯一key
        logger.setMainTargetNameColumn("groupname");//当前targetName对应的列（对应日志中的对象名）
        SimpleBizLogger.SubLogInfo subLogInfo = logger.getNewSubLogInfo();
        String subSql = "select g.*,n.groupname from workflow_groupdetail g ,workflow_nodegroupn where g.groupid = n.id and g.groupid = " +
        Util.getIntValue(Util.null2String(params.get("groupid"))) + " order by g.id";
        subLogInfo.setSubSql(subSql,"id");
        subLogInfo.setSubTargetNameColumn("groupname");
        subLogInfo.setGroupId("0"); //所属分组， 按照groupid排序显示在详情中， 不设置默认按照add的顺序。
        subLogInfo.setSubGroupNameLabel(234212); //在详情中显示的分组名称，不设置默认显示明细x
        logger.addSubLogInfo(subLogInfo);
        //开始记录
        logger.before(bizLogContext);
    }
}
 