package weaver.interfaces.schedule.fsl.fdc;

import com.weaver.util.dev.PropertiesUtil;
import com.weaver.util.dev.dao.DaoUtils;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import com.weaver.util.dev.workflow.WorkflowUtils;
import org.junit.Test;
import weaver.conn.RecordSet;
import weaver.general.GCONST;
import weaver.general.Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ebuCodeGit
 * @ClassName FDCTest
 * @description:
 * @author: slfang
 * @create: 2023-09-06 16:19
 * @Version 1.0
 **/
public class FDCTest {

    public static String  rootPath = "D:\\workplace\\weaver\\ecology\\";
    static {
        GCONST.setRootPath(rootPath);
        GCONST.setServerName("ecology");
    }

    LogFactory log = new DevLog();

    private static final String CATEGORY = "FDC_OOS";//10

    //FDC数据sql
    private static final String FDC_DATA_SQL="select \n" +
            "    t.name as tool_chamber, uh.run_id, --uh.collection_key,   \n" +
            "    cd.collection_name, cd.context_group, \n" +
            "    rc.lot_id, rc.wafer_id, rc.batch_id,\n" +
            "    cd.input as sensor, cd.window, cd.statistics,\n" +
            "    uh.category,uh.status,--uh.result, \n" +
            "    uh.actions,\n" +
            "    --to_char(rc.start_time, 'yyyy mm-dd hh24:mi:ss.ff3') as start_time, to_char(rc.end_time, 'yyyy-mm-dd hh24:mi:ss.ff3') as end_time,    \n" +
            "    to_char(uh.calc_time_stamp, 'yyyy-mm-dd hh24:mi:ss.ff3') as calc_time_stamp\n" +
            "from \n" +
            "    ees_fd_uva_hist uh, \n" +
            "    ees_fd_config_details cd,\n" +
            "    ees_tool t,\n" +
            "    ees_run_context rc,\n" +
            "    ees_fd_collection fc\n" +
            "where \n" +
            "    cd.seq_id = uh.seq_id \n" +
            "    and cd.collection_name=fc.name and fc.collection_key=uh.collection_key\n" +
            "    and uh.tool_id = t.id\n" +
            "    and rc.tool_id = t.id\n" +
            "    and uh.run_id = rc.run_id and uh.partition_id=rc.partition_id and t.partition_id=rc.partition_id\n" +
            "    and uh.start_time = rc.start_time\n" +
            "    and uh.category = 'Key' \n" +
            "    and (uh.status = 'CRITICAL' OR uh.status = 'OUTLIER')\n" +
            "    and uh.calc_time_stamp > to_date('%s', 'yyyy-MM-dd hh24:mi:ss') \n" +
            "    and uh.calc_time_stamp < to_date('%s', 'yyyy-MM-dd hh24:mi:ss')";

    //报表db查找wafer信息
    private static final String getWaferInfoSql="SELECT \n" +
            "distinct \n" +
            "    CURRENTPARENT,\n" +
            "    LISTAGG(COMPONENTID, ',') WITHIN GROUP(ORDER BY CURRENTPARENT) \n" +
            "    OVER( PARTITION BY CURRENTPARENT) \n" +
            "    listagg\n" +
            "FROM (\n" +
            "SELECT ORIGINALPARENT,CURRENTPARENT,COMPONENTID \n" +
            "FROM view_FWCOMPONENT ft WHERE 1 = 1 AND substr(ORIGINALPARENT,1,2) NOT IN ('FP','FS','S0') AND\n" +
            "CURRENTPARENT='%s'\n" +
            ") ";

    //报表db查找 机台部门
    private static final String findJtBm="SELECT SECTION,EQPID FROM RPT_TEST.BASE_EQP WHERE EQPID='%s'";

    //查找oa中设置的oa    owner也是最终赋值
    private static final String findOaOwnerSetValSql="select bmshr from uf_ozdyr where bm=?";

    @Test
    public void test1(){
        //
        log.info("拉取FDC数据库生产DRB流程计划任务开始执行================》");
        String startFdcTime="2023-08-29 16:20:00";
        String endFdcTime="2023-08-29 16:25:00";

        long startTime_all = System.currentTimeMillis();
        HashMap<String,Object> baseInfo = new HashMap<>();
        PropertiesUtil.readProAndSetMap("Fdc2DrbWorkflowJob",baseInfo);
        RecordSet rsExist = new RecordSet();
        RecordSet rsOwner = new RecordSet();
        RecordSet rsUf = new RecordSet();
        WorkflowUtils workflowUtils = new WorkflowUtils();
        String tableName = (String)baseInfo.get("tableName");
        String ufTable = (String)baseInfo.get("ufTable");
        String formmid = (String)baseInfo.get("formmid");
        String checkFields = (String)baseInfo.get("checkFields");
        String dtFields = (String)baseInfo.get("dtFields");
        String[] checkArr = checkFields.split(",");
        log.info("FDC_DATA_SQL::"+FDC_DATA_SQL);
        log.info("startFdcTime::"+startFdcTime);
        log.info("endFdcTime::"+endFdcTime);
        String fdcSql = String.format(FDC_DATA_SQL, startFdcTime, endFdcTime);
        log.info("fdcSql::"+fdcSql);
        Connection fdc;
        Connection report;
        ResultSet resultSet;
        Statement statementReport;
        try {
            fdc = DaoUtils.getConnectionByDatasourceName("FDC");
            Statement statement = fdc.createStatement();
            resultSet = statement.executeQuery(fdcSql);

            report = DaoUtils.getConnectionByDatasourceName("REPORT");
            statementReport = report.createStatement();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return;
        }
        try {
            while (resultSet.next()){
                String lot_id="";
                String batch_id="";
                String desc="";
                String waferID="";
                String owner="";
                try {
                    String tool_chamber = Util.null2String(resultSet.getString("tool_chamber"));
                    lot_id = Util.null2String(resultSet.getString("lot_id"));
                    batch_id = Util.null2String(resultSet.getString("batch_id"));
                    List<Object> values = new ArrayList<>();
                    values.add(lot_id);
                    values.add(batch_id);
                    log.info(values+"==>开始执行");
                    long startTime = System.currentTimeMillis();
                    if(DaoUtils.isDataExit(tableName,checkArr,values,rsExist)){//判重
                        log.info(values+"数据重复");
                        continue;
                    }
                    //获取wafer信息
                    ResultSet resultSetWaferId = statementReport.executeQuery(String.format(getWaferInfoSql, lot_id));
                    if(resultSetWaferId.next()){
                        waferID = Util.null2String(resultSetWaferId.getString("listagg"));

                    }

                    int length = waferID.split(",").length;
                    //获取owner 信息


                    ResultSet resultSetFindBm = statementReport.executeQuery(String.format(findJtBm,tool_chamber));
                    if(resultSetFindBm.next()){
                        String SECTION = Util.null2String(resultSetFindBm.getString("SECTION"));
                        DaoUtils.executeQuery(findOaOwnerSetValSql,rsOwner,SECTION);
                        rsOwner.next();
                        owner = Util.null2String(rsOwner.getString("bmshr"));
                    }

                    //创建流程
                    //主表数据
                    List<String> mainDataList = new ArrayList<>();
                    baseInfo.put("MAIN_DATA_LIST",mainDataList);
                    mainDataList.add(lot_id);
                    mainDataList.add("10");
                    mainDataList.add(batch_id);
                    mainDataList.add(desc);
                    //明细数据
                    Map<String, Map<String,Object>> allDtInfo = new HashMap<String, Map<String, Object>>();
                    Map<String,Object> dtInfo =new HashMap<String, Object>();
                    List<List<String>> dtAllDataArr = new ArrayList<List<String>>();
                    List<String> dtDataList = new ArrayList<>();
                    dtDataList.add(lot_id);
                    dtDataList.add(desc);
                    dtDataList.add(waferID);
                    dtDataList.add(String.valueOf(length));
                    dtDataList.add(CATEGORY);
                    dtDataList.add(owner);
                    dtAllDataArr.add(dtDataList);
                    dtInfo.put("fields",dtFields);
                    dtInfo.put("dataList",dtAllDataArr);
                    allDtInfo.put("1",dtInfo);
                    baseInfo.put("allDtInfo",allDtInfo);
                    String requestId = workflowUtils.createWorkflow(baseInfo);
                    log.info("生成requestid==>"+requestId);

                    Map<String,Object> params = new HashMap<>();
                    params.put("lotid",lot_id);
                    params.put("batchid",batch_id);
                    params.put("waferid",waferID);
                    params.put("owner",owner);
                    params.put("fromSystem",CATEGORY);
                    if(Integer.valueOf(requestId)<0){
                        //执行报错
                        log.info("流程创建失败::"+WorkflowUtils.ERROR_REQUEST_CODE_MAP.get(requestId));

                        params.put("status",1);
                        params.put("sbyy",WorkflowUtils.ERROR_REQUEST_CODE_MAP.get(requestId));

                    }else {
                        log.info("流程创建成功::"+requestId);
                        params.put("status",0);
                        DaoUtils.insertFormmode(ufTable,params,Integer.valueOf(formmid),rsUf);
                    }
                    long endTime = System.currentTimeMillis();
                    log.info(values+"程序运行时间为：" + (endTime - startTime) + "ms");
                }catch (Exception e){
                    //插入建模数据
                    Map<String,Object> params = new HashMap<>();
                    params.put("lotid",lot_id);
                    params.put("batchid",batch_id);
                    params.put("waferid",waferID);
                    params.put("owner",owner);
                    params.put("fromSystem",CATEGORY);
                    params.put("status",1);
                    params.put("sbyy","执行报错::"+e.getMessage());
                    try {
                        DaoUtils.insertFormmode(ufTable,params,Integer.valueOf(formmid),rsUf);
                    } catch (Exception ex) {
                        log.error("插入建模数据失败::"+ex.getMessage());
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        long endTime_all = System.currentTimeMillis();
        log.info("计划任务总程序运行时间为：" + (endTime_all - startTime_all) + "ms");
    }
}
