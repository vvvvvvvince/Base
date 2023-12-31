/**
 * @projectName ebuCodes
 * @package weaver.interfaces.fsl.formmode
 * @className weaver.interfaces.fsl.formmode.CustomBtnShowTemplate
 * @copyright weaver, Inc All rights reserved.
 */
package weaver.interfaces.fsl.formmode;

/**
 * @BelongsPackage: weaver.interfaces.fsl.formmode
 * @description 建模自定义按钮后台接口
 * @author fsl
 * @date 2023/3/17 13:57
 * @version 1.0
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weaver.formmode.interfaces.PopedomCommonAction;
public class CustomBtnShowTemplate implements PopedomCommonAction {
    private Logger logger = LoggerFactory.getLogger(CustomBtnShowTemplate.class);
    /**
     * 得到是否显示操作项
     * @param modeid 模块id
     * @param customid 查询列表id
     * @param uid 当前用户id
     * @param billid 表单数据id
     * @param buttonname 按钮名称
     * @retrun "true"或者"false"true显示/false不显示
     */
    @Override
    public String getIsDisplayOperation(String modeid, String customid,String uid, String billid, String buttonname) {
        logger.debug("modeId: {}", modeid);
        logger.debug("customId: {}", customid);
        logger.debug("uid: {}", uid);
        logger.debug("billId: {}", billid);
        logger.debug("buttonname: {}", buttonname);
        return "false";
    }
}
 