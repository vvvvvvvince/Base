package com.weaver.util.dev;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.weaver.util.dev.dao.DaoUtils;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import com.weaver.util.dev.string.DevStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import weaver.conn.RecordSet;
import weaver.cpt.capital.CapitalComInfo;
import weaver.crm.Maint.CustomerInfoComInfo;
import weaver.docs.docs.DocComInfo;
import weaver.docs.senddoc.DocReceiveUnitComInfo;
import weaver.formmode.tree.CustomTreeUtil;
import weaver.general.BaseBean;
import weaver.general.StaticObj;
import weaver.general.Util;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.companyvirtual.DepartmentVirtualComInfo;
import weaver.hrm.companyvirtual.SubCompanyVirtualComInfo;
import weaver.hrm.resource.ResourceComInfo;
import weaver.interfaces.workflow.browser.Browser;
import weaver.interfaces.workflow.browser.BrowserBean;
import weaver.proj.Maint.ProjectInfoComInfo;
import weaver.systeminfo.SystemEnv;
import weaver.workflow.field.BrowserComInfo;
import weaver.workflow.request.ResourceConditionManager;
import weaver.workflow.workflow.WorkflowRequestComInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 表单字段操作工具类
 */
public class FieldUtils {

    private static final BaseBean baseBean = new BaseBean();
    private static final LogFactory log = new DevLog();

    // -------------------------------华丽的分割线 开始---------------------------------------
    /**
     * 根据field名称获取流程表单显示值  todo
     *
     * @param fieldname 字段名
     * @param fieldval  字段值
     * @param tablename 表名
     * @param detail    明细表编号 0表示为主表
     * @return
     */
    public static String fieldShowVal(String fieldname, String fieldval, String tablename, int detail,RecordSet recordSet) throws Exception {
        // 查询字段类型
        Map<String, String> fieldMapData = DaoUtils.executeQueryToMap(
                String.format("select id, billid, fieldname, fielddbtype, fieldhtmltype, type, viewtype, detailtable " +
                "from workflow_billfield " +
                "where billid = (select id from workflow_bill where tablename = ?) " +
                "and fieldname = ? and %s", detail <= 0 ? "viewtype = 0" : "detailtable = " + tablename + "_dt" + detail),recordSet, tablename, fieldname);
        if (MapUtils.isEmpty(fieldMapData)) return fieldval;
        // 根据fieldhtmltype 判断类型
        //# 1.单行文本
        //# 2.多行文本
        //# 3.浏览按钮
        //# 4.check框
        //# 5.选择框
        List<String> valueList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(fieldval);
        switch (fieldMapData.get("fieldhtmltype")) {
            case "3": { // 浏览框
                String type = Util.null2String(fieldMapData.get("type"));
                String dbtype = Util.null2String(fieldMapData.get("fielddbtype"));
                // 部门， 多部门
                switch (type) {
                    case "4": // 部门 多部门
                    case "57": {
                        DepartmentComInfo departmentComInfo = new DepartmentComInfo();
                        DepartmentVirtualComInfo deptVirComInfo = new DepartmentVirtualComInfo();
                        List<String> res = Lists.newArrayList();
                        for (String value : valueList) {
                            if (!"".equals(value)) {
                                if (Integer.parseInt(value) < -1) {
                                    res.add(deptVirComInfo.getDepartmentname(value));
                                } else {
                                    res.add(departmentComInfo.getDepartmentname(value));
                                }
                            }
                        }
                        return Joiner.on(",").skipNulls().join(res.toArray());
                    }
                    case "1": // 人力 多人力
                    case "17": {
                        ResourceComInfo resourceComInfo = new ResourceComInfo();
                        List<String> res = Lists.newArrayList();
                        for (String value : valueList) {
                            res.add(resourceComInfo.getResourcename(value));
                        }
                        return Joiner.on(",").skipNulls().join(res.toArray());
                    }
                    case "7": // 客户 多客户
                    case "18": {
                        CustomerInfoComInfo customerInfoComInfo = new CustomerInfoComInfo();
                        List<String> res = Lists.newArrayList();
                        for (String value : valueList) {
                            res.add(customerInfoComInfo.getCustomerInfoname(value));
                        }
                        return Joiner.on(",").skipNulls().join(res.toArray());
                    }
                    case "8": // 项目 多项目
                    case "135": {
                        ProjectInfoComInfo projectInfoComInfo = new ProjectInfoComInfo();
                        List<String> res = Lists.newArrayList();
                        for (String value : valueList) {
                            res.add(projectInfoComInfo.getProjectInfoname(value));
                        }
                        return Joiner.on(",").skipNulls().join(res.toArray());
                    }
                    case "161": { // 自定义单选
                        Browser browser = (Browser) StaticObj.getServiceByFullname(dbtype, Browser.class);
                        BrowserBean bb = browser.searchById(fieldval);
                        return Util.null2String(bb.getName());

                    }
                }
                return fieldval;
            }
            case "5": { // 选择框
                List<Map<String, String>> itemMapDatas = DaoUtils.executeQueryToMapList("select selectname, selectvalue from workflow_selectitem where fieldid = ?", fieldMapData.get("id"));
                Map<String, String> itemMapVal = Maps.newHashMap();
                itemMapDatas.forEach(x -> itemMapVal.put(x.getOrDefault("selectvalue", ""), x.getOrDefault("selectname", "")));
                List<String> resList = Lists.newArrayList();
                Splitter.on(",").trimResults().omitEmptyStrings().splitToList(fieldval).forEach(x -> resList.add(itemMapVal.getOrDefault(x, "")));
                return Joiner.on(",").skipNulls().join(resList.toArray());
            }
            case "1":
            case "2":
            case "4":
            default: {
                return fieldval;
            }
        }
    }

    public static String fieldShowVal(String fieldname, String fieldval, String tablename,RecordSet recordSet) throws Exception {
        return fieldShowVal(fieldname, fieldval, tablename, 0,recordSet);
    }

    // -------------------------------华丽的分割线 结束---------------------------------------

    // 查询流程表单下拉框对应的值
    /**
     * @description:
     * @author: fsl
     * @param: formid  表单id
     * @param: fieldname
     * @param: fieldval
     * @param: recordSet
     * @return: java.lang.String
     **/
    public static String selectShowFieldVal(String formid, String fieldname, String fieldval,RecordSet recordSet) {
        return DaoUtils.querySingleVal("select selectname from workflow_SelectItem " +
                "where fieldid = (select id from workflow_billfield " +
                "where billid = ? and fieldname = ?) and selectvalue = ?", recordSet,formid, fieldname, fieldval);
    }

    /***
     *
     * @description 获取下拉框的中文名的值
     * @author slfang
     * @date 2021/8/2
     * @param fieldid  流程中的字段的且不要field
     * @param selectVal
     * @return {@link String}
     */
    public String getSelectChineseVal(String fieldid, String selectVal,RecordSet rs) {
        String sql = "select selectname from workflow_SelectItem where fieldid=? and selectvalue=?";
        rs.executeQuery(sql,fieldid,selectVal);
        return rs.next() ? Util.null2String(rs.getString(1)) : null;
    }

    // 查询流程表单下拉框文字对应的值
    public static String selectValByShowName(String formid, String fieldname, String showname,RecordSet recordSet) {
        return DaoUtils.querySingleVal("select selectvalue from workflow_SelectItem " +
                "where fieldid = (select id from workflow_billfield " +
                "where billid = ? and fieldname = ?) and selectname = ?",recordSet, formid, fieldname, showname);
    }

    private static String fieldVal(String fieldid, String requestid,RecordSet recordSet) {
        Map<String, String> fieldData = DaoUtils.executeQueryToMap
                ("select fieldname,detailtable from workflow_billfield where id = ?",recordSet, fieldid);
        if (Objects.isNull(fieldData) || fieldData.isEmpty()) {
            return "";
        }
        String fieldname = Util.null2String(fieldData.get("fieldname"));
        String tablename = Util.null2String(fieldData.get("detailtable"));
        if (StringUtils.isEmpty(tablename)) {
            // 获取流程主表信息
            int formid = Util.getIntValue(DaoUtils.querySingleVal("select formid from workflow_requestbase " +
                    "left join workflow_base on workflow_requestbase.workflowid = workflow_base.id where requestid = ?",recordSet, requestid));
            if (formid > 0) { // 自定义流程数据
                baseBean.writeLog("requestid => " + requestid + " , formid => " + formid + " 为正数 非自定义表");
                return "";
            }
            tablename = "formtable_main_" + Math.abs(formid);
        }
        // 获取数据库存储值
        String dbv = dbVal(fieldname, tablename, requestid,recordSet);
        if (StringUtils.isEmpty(dbv)) { // 如果存放值为空  直接返回
            return dbv;
        }
        String type = Util.null2String(fieldData.get("type"));
        String htmltype = Util.null2String(fieldData.get("fieldhtmltype"));
        switch (htmltype) {
            case "1":  // 普通值, 直接返回数据库存储的值
            case "2":  // 多行文本框，直接放
                return dbv;
            case "3": // 浏览按钮

            case "4": // Check框
                if (StringUtils.equals("1", dbv)) {
                    return "true";
                } else if (StringUtils.equals("0", dbv)) {
                    return "false";
                } else {
                    return dbv;
                }
            case "5": // 选择框
//                break;
            case "6": // 附件上传
//                break;
            case "7": // 特殊字段
//                break;
            case "9": // 位置
//                break;
            default:
                return dbv;
        }
    }

    /**
     * 获取数据库中存储的值
     */
    private static String dbVal(String fieldname, String tablename, String requestid,RecordSet recordSet) {
        return DaoUtils.querySingleVal(String.format("select %s from %s where requestid = ?", fieldname, tablename),recordSet, requestid);
    }

    public static boolean checkIsFile(String fileid,RecordSet rs) { // 判断是否为附件
        Map<String, String> detailFieldMap = DaoUtils.executeQueryToMap(
                "select fieldhtmltype from workflow_billfield where id = ?",rs, fileid);
        if (StringUtils.equals(Util.null2String(detailFieldMap.get("fieldhtmltype")), "6")) {
            return true;
        }
        return false;
    }

    // ------------------------分割线------------------------

//    public static String replace(String template1, Map<String, String> replaceValue) {
////        String result = "";
////        StrSubstitutor strSubstitutor = new StrSubstitutor(replaceValue);
////        result = strSubstitutor.replace(template1);
//        return StringUtils.replaceTpl(replaceValue, template1);
//    }



    public static String changeFieldValue(String fieldid, String requestName, String htmltypes, String fieldvalue, String fieldtype, int languageid, String isBill, String dbtype, int qfws) {
        RecordSet rs3 = new RecordSet();
        BaseBean bb = new BaseBean();
        String tempvalue = "";
        String tempDateShowType = "";
        if ("-3".equals(fieldid)) {
            tempvalue = requestName;
        } else if (!htmltypes.equals("3") && !htmltypes.equals("6") && !htmltypes.equals("5") && !htmltypes.equals("4")) {
            if ("3".equals(fieldtype)) {
                if (!"".equals(fieldvalue)) {
                    double d = Double.parseDouble(fieldvalue);
                    String formatstr = "######0.";

                    for (int i = 0; i < qfws; ++i) {
                        formatstr = formatstr + "0";
                    }
                    DecimalFormat df = new DecimalFormat(formatstr);
                    BigDecimal bigDecimal = new BigDecimal(roundValue(d, qfws));
                    tempvalue = df.format(bigDecimal);
                } else {
                    bb.writeLog("fieldid==>" + fieldid);
                    fieldvalue = "0.00";
                    bb.writeLog("fieldid==>" + fieldid + ",fieldvalue=>" + fieldvalue);
                    tempvalue = fieldvalue;
                }
            } else {
                tempvalue = fieldvalue;
            }
        } else if ("3".equals(htmltypes) && "2".equals(fieldtype)) {
            String dateShowTypesql = "select dateShowType as dateShowType from workflow_docshow where fieldId=" + fieldid;
            rs3.execute(dateShowTypesql);
            if (rs3.next()) {
                tempDateShowType = rs3.getString("dateShowType");
                tempvalue = getValueByDateShowType(tempvalue, tempDateShowType);
            } else {
                tempvalue = fieldvalue;
            }
        } else {
            try {
                tempvalue = getFieldValue(htmltypes, fieldtype, fieldvalue, languageid, fieldid, isBill, dbtype);
            } catch (Exception var18) {
                var18.printStackTrace();
            }
        }

        if (tempvalue.indexOf(">") > 0) {
            tempvalue = StringUtils.replace(tempvalue, ">", "&gt;");
        }

        if (tempvalue.indexOf("<") > 0) {
            tempvalue = StringUtils.replace(tempvalue, "<", "&lt;");
        }

        return tempvalue;
    }

    public static String getValueByDateShowType(String dateValue, String dateShowType) {
        String returnDateShowValue = dateValue;
        //安全检查
        if (dateValue == null || dateValue.trim().equals("")
                || dateShowType == null || dateShowType.trim().equals("")
                || dateValue.length() < 10) {
            return returnDateShowValue;
        }
        if ("1".equals(dateShowType)) {
            returnDateShowValue = "";
            String yearId = "";
            int monthId = 0;
            int dayId = 0;
            int indexFirst = dateValue.indexOf("-");
            int indexSecond = dateValue.lastIndexOf("-");
            if (indexFirst < 0 || indexSecond < 0) {
                return returnDateShowValue;
            }
            yearId = dateValue.substring(0, indexFirst);
            monthId = Util.getIntValue(dateValue.substring(indexFirst + 1, indexSecond), 0);
            dayId = Util.getIntValue(dateValue.substring(indexSecond + 1), 0);
            char yearChar[] = yearId.toCharArray();
            int i = 0;
            char ch;
            while (i < yearChar.length) {
                ch = yearChar[i++];
                if (ch == '0') {
                    returnDateShowValue += "○";
                } else if (ch == '1') {
                    returnDateShowValue += "一";
                } else if (ch == '2') {
                    returnDateShowValue += "二";
                } else if (ch == '3') {
                    returnDateShowValue += "三";
                } else if (ch == '4') {
                    returnDateShowValue += "四";
                } else if (ch == '5') {
                    returnDateShowValue += "五";
                } else if (ch == '6') {
                    returnDateShowValue += "六";
                } else if (ch == '7') {
                    returnDateShowValue += "七";
                } else if (ch == '8') {
                    returnDateShowValue += "八";
                } else if (ch == '9') {
                    returnDateShowValue += "九";
                }
            }
            returnDateShowValue += "年";
            switch (monthId) {
                case 1:
                    returnDateShowValue += "一";
                    break;
                case 2:
                    returnDateShowValue += "二";
                    break;
                case 3:
                    returnDateShowValue += "三";
                    break;
                case 4:
                    returnDateShowValue += "四";
                    break;
                case 5:
                    returnDateShowValue += "五";
                    break;
                case 6:
                    returnDateShowValue += "六";
                    break;
                case 7:
                    returnDateShowValue += "七";
                    break;
                case 8:
                    returnDateShowValue += "八";
                    break;
                case 9:
                    returnDateShowValue += "九";
                    break;
                case 10:
                    returnDateShowValue += "十";
                    break;
                case 11:
                    returnDateShowValue += "十一";
                    break;
                case 12:
                    returnDateShowValue += "十二";
                    break;

            }
            returnDateShowValue += "月";
            switch (dayId) {
                case 1:
                    returnDateShowValue += "一";
                    break;
                case 2:
                    returnDateShowValue += "二";
                    break;
                case 3:
                    returnDateShowValue += "三";
                    break;
                case 4:
                    returnDateShowValue += "四";
                    break;
                case 5:
                    returnDateShowValue += "五";
                    break;
                case 6:
                    returnDateShowValue += "六";
                    break;
                case 7:
                    returnDateShowValue += "七";
                    break;
                case 8:
                    returnDateShowValue += "八";
                    break;
                case 9:
                    returnDateShowValue += "九";
                    break;
                case 10:
                    returnDateShowValue += "十";
                    break;
                case 11:
                    returnDateShowValue += "十一";
                    break;
                case 12:
                    returnDateShowValue += "十二";
                    break;
                case 13:
                    returnDateShowValue += "十三";
                    break;
                case 14:
                    returnDateShowValue += "十四";
                    break;
                case 15:
                    returnDateShowValue += "十五";
                    break;
                case 16:
                    returnDateShowValue += "十六";
                    break;
                case 17:
                    returnDateShowValue += "十七";
                    break;
                case 18:
                    returnDateShowValue += "十八";
                    break;
                case 19:
                    returnDateShowValue += "十九";
                    break;
                case 20:
                    returnDateShowValue += "二十";
                    break;
                case 21:
                    returnDateShowValue += "二十一";
                    break;
                case 22:
                    returnDateShowValue += "二十二";
                    break;
                case 23:
                    returnDateShowValue += "二十三";
                    break;
                case 24:
                    returnDateShowValue += "二十四";
                    break;
                case 25:
                    returnDateShowValue += "二十五";
                    break;
                case 26:
                    returnDateShowValue += "二十六";
                    break;
                case 27:
                    returnDateShowValue += "二十七";
                    break;
                case 28:
                    returnDateShowValue += "二十八";
                    break;
                case 29:
                    returnDateShowValue += "二十九";
                    break;
                case 30:
                    returnDateShowValue += "三十";
                    break;
                case 31:
                    returnDateShowValue += "三十一";
                    break;
            }
            returnDateShowValue += "日";
        } else if ("2".equals(dateShowType)) {
            String yearId = "";
            int monthId = 0;
            int dayId = 0;
            int indexFirst = dateValue.indexOf("-");
            int indexSecond = dateValue.lastIndexOf("-");
            if (indexFirst < 0 || indexSecond < 0) {
                return returnDateShowValue;
            }
            yearId = dateValue.substring(0, indexFirst);
            monthId = Util.getIntValue(dateValue.substring(indexFirst + 1, indexSecond), 0);
            dayId = Util.getIntValue(dateValue.substring(indexSecond + 1), 0);
            returnDateShowValue = yearId + "年" + monthId + "月" + dayId + "日";
        }
        return returnDateShowValue;
    }


    public static String getFieldValue(String htmltypes, String fieldtype, String fieldvalue,
                                       int languageid, String fieldid, String isBill, String dbtype) throws Exception {
        String showname = "";
        String showid = "";
        RecordSet RecordSet = new RecordSet();
        ArrayList tempshowidlist;
        if (htmltypes.equals("3")) {
            if (fieldtype.equals("290")) {
                return fieldvalue;
            }
            if (!fieldtype.equals("2") && !fieldtype.equals("19")) {
                if (!fieldvalue.equals("")) {
                    tempshowidlist = Util.TokenizerString(fieldvalue, ",");
                    int k;
                    if (fieldtype.equals("8") || fieldtype.equals("135")) {
                        ProjectInfoComInfo ProjectInfoComInfo1 = new ProjectInfoComInfo();

                        for (k = 0; k < tempshowidlist.size(); ++k) {
                            showname = showname + ProjectInfoComInfo1.getProjectInfoname((String) tempshowidlist.get(k)) + " ";
                        }
                    } else if (fieldtype.equals("1") || fieldtype.equals("17")) {
                        ResourceComInfo ResourceComInfo = new ResourceComInfo();

                        for (k = 0; k < tempshowidlist.size(); ++k) {
                            showname = showname + ResourceComInfo.getResourcename((String) tempshowidlist.get(k)) + " ";
                        }
                    } else if (fieldtype.equals("7") || fieldtype.equals("18")) {
                        CustomerInfoComInfo CustomerInfoComInfo = new CustomerInfoComInfo();

                        for (k = 0; k < tempshowidlist.size(); ++k) {
                            showname = showname + CustomerInfoComInfo.getCustomerInfoname((String) tempshowidlist.get(k)) + " ";
                        }
                    } else {
                        String keycolumname;
                        int j;
                        String sql;
                        if (fieldtype.equals("4") || fieldtype.equals("57")) {
                            DepartmentComInfo DepartmentComInfo1 = new DepartmentComInfo();
                            DepartmentVirtualComInfo deptVirComInfo = new DepartmentVirtualComInfo();

                            for (j = 0; j < tempshowidlist.size(); ++j) {
                                keycolumname = "";
                                sql = (String) tempshowidlist.get(j);
                                if (!"".equals(sql)) {
                                    if (Integer.parseInt(sql) < -1) {
                                        keycolumname = deptVirComInfo.getDepartmentname(sql);
                                    } else {
                                        keycolumname = DepartmentComInfo1.getDepartmentname(sql);
                                    }
                                }

                                showname = showname + keycolumname + " ";
                            }
                        } else if (fieldtype.equals("164") || fieldtype.equals("194")) {
                            SubCompanyComInfo SubCompanyComInfo1 = new SubCompanyComInfo();
                            SubCompanyVirtualComInfo subCompVirComInfo = new SubCompanyVirtualComInfo();

                            for (j = 0; j < tempshowidlist.size(); ++j) {
                                keycolumname = "";
                                sql = (String) tempshowidlist.get(j);
                                if (!"".equals(sql)) {
                                    if (Integer.parseInt(sql) < -1) {
                                        keycolumname = subCompVirComInfo.getSubCompanyname(sql);
                                    } else {
                                        keycolumname = SubCompanyComInfo1.getSubCompanyname(sql);
                                    }
                                }

                                showname = showname + keycolumname + " ";
                            }
                        } else if (fieldtype.equals("9") || fieldtype.equals("37")) {
                            DocComInfo DocComInfo1 = new DocComInfo();

                            for (k = 0; k < tempshowidlist.size(); ++k) {
                                showname = showname + DocComInfo1.getDocname((String) tempshowidlist.get(k)) + " ";
                            }
                        } else if (fieldtype.equals("23")) {
                            CapitalComInfo CapitalComInfo1 = new CapitalComInfo();

                            for (k = 0; k < tempshowidlist.size(); ++k) {
                                showname = showname + CapitalComInfo1.getCapitalname((String) tempshowidlist.get(k)) + " ";
                            }
                        } else if (fieldtype.equals("16")) {
                            WorkflowRequestComInfo WorkflowRequestComInfo1 = new WorkflowRequestComInfo();

                            for (k = 0; k < tempshowidlist.size(); ++k) {
                                showname = showname + WorkflowRequestComInfo1.getRequestName((String) tempshowidlist.get(k)) + " ";
                            }
                        } else if (fieldtype.equals("141")) {
                            ResourceConditionManager ResourceConditionManager = new ResourceConditionManager();
                            showname = showname + ResourceConditionManager.getFormShowNameOfNoLink(fieldvalue, languageid);
                        } else if ("142".equals(fieldtype)) {
                            DocReceiveUnitComInfo docReceiveUnitComInfo = new DocReceiveUnitComInfo();

                            for (k = 0; k < tempshowidlist.size(); ++k) {
                                showname = showname + docReceiveUnitComInfo.getReceiveUnitName((String) tempshowidlist.get(k)) + " ";
                            }
                        } else {
                            String columname;
                            Browser browser;
                            if (fieldtype.equals("161")) {
                                showname = "";
                                showid = fieldvalue;

                                try {
                                    browser = (Browser) StaticObj.getServiceByFullname(dbtype, Browser.class);
                                    BrowserBean bb = browser.searchById(showid);
                                    Util.null2String(bb.getDescription());
                                    columname = Util.null2String(bb.getName());
                                    showname = columname;
                                } catch (Exception var18) {
                                }
                            } else {
                                String tempshowname;
                                if (fieldtype.equals("162")) {
                                    showname = "";
                                    showid = fieldvalue;

                                    try {
                                        browser = (Browser) StaticObj.getServiceByFullname(dbtype, Browser.class);
                                        List l = Util.TokenizerString(showid, ",");

                                        for (j = 0; j < l.size(); ++j) {
                                            keycolumname = (String) l.get(j);
                                            BrowserBean bb = browser.searchById(keycolumname);
                                            tempshowname = Util.null2String(bb.getName());
                                            Util.null2String(bb.getDescription());
                                            showname = showname + tempshowname + " ";
                                        }
                                    } catch (Exception var19) {
                                    }
                                } else if (!fieldtype.equals("256") && !fieldtype.equals("257")) {
                                    if (!fieldtype.equals("226") && !fieldtype.equals("227")) {
                                        BrowserComInfo BrowserComInfo = new BrowserComInfo();
                                        String tablename = BrowserComInfo.getBrowsertablename(fieldtype);
                                        columname = BrowserComInfo.getBrowsercolumname(fieldtype);
                                        keycolumname = BrowserComInfo.getBrowserkeycolumname(fieldtype);
                                        sql = "";
                                        if (fieldvalue.indexOf(",") != -1) {
                                            sql = "select " + keycolumname + "," + columname + " from " + tablename + " where " + keycolumname + " in( " + fieldvalue + ")";
                                        } else {
                                            sql = "select " + keycolumname + "," + columname + " from " + tablename + " where " + keycolumname + "=" + fieldvalue;
                                        }

                                        RecordSet.executeSql(sql);

                                        while (RecordSet.next()) {
                                            showid = Util.null2String(RecordSet.getString(1));
                                            tempshowname = Util.toScreen(RecordSet.getString(2), languageid);
                                            showname = showname + tempshowname + " ";
                                        }
                                    } else {
                                        showname = fieldvalue;
                                    }
                                } else {
                                    showname = "";
                                    CustomTreeUtil customTreeUtil = new CustomTreeUtil();
                                    showname = showname + customTreeUtil.getTreeFieldShowName(fieldvalue, dbtype, "onlyname");
                                }
                            }
                        }
                    }
                }
            } else {
                showname = fieldvalue;
            }
        } else {
            String sql;
            if (htmltypes.equals("6")) {
                if (!fieldvalue.equals("")) {
                    sql = "select id,docsubject,accessorycount from docdetail where id in(" + fieldvalue + ") order by id asc";
                    RecordSet.executeSql(sql);

                    while (RecordSet.next()) {
                        showname = showname + Util.toScreen(RecordSet.getString(2), languageid) + " ";
                    }
                }
            } else if (htmltypes.equals("5")) {
                if (!fieldvalue.equals("")) {
                    tempshowidlist = null;
                    if (!"1".equals(isBill) && !"0".equals(isBill)) {
                        sql = "select * from workflow_SelectItem where selectvalue=" + fieldvalue + " and fieldid=" + fieldid;
                    } else {
                        sql = "select * from workflow_SelectItem where selectvalue=" + fieldvalue + " and fieldid=" + fieldid + " and isBill=" + isBill;
                    }

                    RecordSet.executeSql(sql);
                    if (RecordSet.next()) {
                        showname = RecordSet.getString("selectname");
                    }
                }
            } else if (htmltypes.equals("4")) {
                if ("1".equals(fieldvalue)) {
                    showname = SystemEnv.getHtmlLabelName(163, languageid);
                } else {
                    showname = SystemEnv.getHtmlLabelName(161, languageid);
                }
            }
        }

        return showname.trim();
    }




    public static String replaceStr(String str, int len) {
        str = str.replaceAll("<br>", "");
        str = str.replaceAll("\r|\n", "");
        str = str.replaceAll("&lt;", "");
        str = str.replaceAll("&gt;", "");
        str = str.replaceAll("&nbsp;", " ");
        str = str.replaceAll("  ", "");
        str = str.replaceAll("\t", "");
        str = DevStringUtils.subStringByChar(str, len);
        return str;
    }


    public static Double roundValue(Double d, int newScale) {
        Double retValue = null;
        if (d != null) {
            BigDecimal bd = new BigDecimal(d);
            retValue = bd.setScale(newScale, 4).doubleValue();
        }

        return retValue;
    }

    public static Map<String, String> getTreeNodeById(String treeid,RecordSet recordSet) {
        try {
            String id = treeid.split("_")[0];
            String nodeid = treeid.split("_")[1];
            String tablename = DaoUtils.querySingleVal("select tablename from mode_customtreedetail where id = ?",recordSet, id);
            return DaoUtils.executeQueryToMap(String.format("select * from %s where id = ?", tablename),recordSet , nodeid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据数据库名称获取字段名称 labelname
     *
     * @param fieldName  字段名称
     * @param billid  单据id  流程建模都负号
     * @param detailtable  主表为空，明细需要传参数 传明细表全称
     * @return
     */
    public static String getLableName(String fieldName, String billid, String detailtable, RecordSet rs) {
        detailtable = StringUtils.isEmpty(detailtable)?"is null":"='"+detailtable+"'";
        String getLableName = "select  b.labelname,a.fieldname,a.billid,a.detailtable from workflow_billfield " +
                "a inner join HtmlLabelInfo b on a.fieldlabel=b.indexid where a.fieldname='" + fieldName
                + "' and a.detailtable "
                + detailtable + " and  a.billid=" + billid;
        log.info("getLableName::" + getLableName);
        rs.execute(getLableName);
        return rs.next() ? Util.null2String(rs.getString("labelname")) : null;
    }
}
