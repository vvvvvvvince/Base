/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.auth
 * @className com.weaver.util.dev.auth.FormmodeAuth
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.auth;

import com.weaver.util.dev.date.DateUtils;
import weaver.formmode.setup.ModeRightInfo;

/**
 * @BelongsPackage: com.weaver.util.dev.auth
 * @description 建模构建权限类
 * @author fsl
 * @date 2023/3/31 10:36
 * @version 1.0
 */
public class FormmodeAuth {

    public static int modedatacreater = 1;
    public static int modedatacreatertype = 0;
    public static String modedatacreatedate = DateUtils.getDate("yyyy-MM-dd");
    public static String modedatacreatetime = DateUtils.getDate("HH:mm:ss");

    /**
     * @description:构建建模权限
     * @param: formmodeId  建模模块id
     * @param: dataId  数据id
     **/
    public static void buildAuth(int formmodeId,int dataId) throws Exception{
        ModeRightInfo moderightinfo = new ModeRightInfo();
        moderightinfo.setNewRight(true);
        moderightinfo.editModeDataShare(modedatacreater, formmodeId, Integer.valueOf(dataId));
    }
}
