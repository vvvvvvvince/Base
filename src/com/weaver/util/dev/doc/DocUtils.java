/**
 * @projectName ebuCodes
 * @package com.weaver.util.dev.doc
 * @className com.weaver.util.dev.doc.DocUtils
 * @copyright weaver, Inc All rights reserved.
 */
package com.weaver.util.dev.doc;

import cn.hutool.core.io.FileUtil;
import com.api.doc.detail.service.DocSaveService;
import com.weaver.util.dev.log.DevLog;
import com.weaver.util.dev.log.LogFactory;
import weaver.conn.RecordSet;
import weaver.file.ImageFileManager;
import weaver.general.Util;
import weaver.hrm.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static cn.hutool.core.io.IoUtil.readBytes;
import static org.apache.commons.io.IOUtils.toByteArray;

/**
 * @BelongsPackage: com.weaver.util.dev.doc
 * docimagefile 文档附件关联表
 * imagefile 文件基础信息表
 *
 * doc相关操作类  ImageFileManager
 * DocSaveService 封装了很多关于文档的操作
 *
 *
 * 给文档设置共享权限  DocShareUtil.docSave(user, docid, shareMap, true);
 * 给微搜设置查询权限  DocViewer dv = new DocViewer(); dv.setDocShareByDoc(docid + "");
 * DocAccService   buildRelForDoc  文档、附件建立关系
 *
 * 给指定人员进行文档赋权
 *   1.docShareService.addShareForUser 2.docViewer.setDocShare
 *
 * @description doc操作类
 * @author fsl
 * @date 2023/4/4 16:20
 * @version 1.0
 */
public class DocUtils {

    private static LogFactory log = new DevLog();

    private static ImageFileManager imageFileManager = new ImageFileManager();
    //  文件保存服务类
    private static DocSaveService docSaveService = new DocSaveService();

    /**
     * @description:流程附件目录id
     * @author: fsl
     * @date: 2023/4/5 16:25
     * @param: worklfowId
     * @param: rs
     * @return: java.lang.String
     **/
    public static String getWorkflowDoccategory(int worklfowId, RecordSet rs){
        String getSecIdSql="select doccategory from  workflow_base where id = ?";
        rs.executeQuery(getSecIdSql,worklfowId);
        rs.next();
        String doccategory = Util.null2String(rs.getString("doccategory"));
        log.info("流程【"+worklfowId+"】目录路径 =》 "+doccategory);
        String dirId = doccategory.substring(doccategory.lastIndexOf(",") + 1, doccategory.length());
        return dirId;
    }

    /**
     * @description:根据文档id获取附件信息
     * @author: fsl
     * @param: docid
     * @param: rs
     * @return: weaver.file.ImageFileManager
     **/
    public static ImageFileManager getImageFileInfoByDocid(int docid, RecordSet rs) {
        int imagefileidByDocId = getImagefileidByDocId(docid, rs);
        ImageFileManager imageFileManager = new ImageFileManager();
        imageFileManager.getImageFileInfoById(imagefileidByDocId);
        return imageFileManager;
    }


    /**
     * @description: 根据文档id获取附件id
     * @author: fsl
     * @date: 2023/4/5 17:16
     * @param: docid
     * @param: rs
     * @return: int
     **/
    public static int getImagefileidByDocId(int docid,RecordSet rs){
        String imagefileidSql = "select d.imagefileid from docimagefile d where d.docid =?";
        rs.executeQuery(imagefileidSql,docid);
        if(rs.next()){
            return  rs.getInt(1);
        }else{
            return -1;
        }
    }

    /**
     * @description:根据docid获取文件流
     * @author: fsl
     * @param: docId
     * @param: rs
     * @return: java.io.InputStream
     **/
    public static InputStream getInputStream(int docId, RecordSet rs){
        log.info("getStreamByFjId::start");
        int imagefileidByDocId = getImagefileidByDocId(docId, rs);
        //获取文件流
        return ImageFileManager.getInputStreamById(Integer.valueOf(imagefileidByDocId));
    }



    /**
     * 文件路径方式存储文件到系统中
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @param userId 用户id
     * @param dirId 文件目录
     * @return 文件id(直接赋值流程字段)
     */
    public static int enclosureImageUpload(String filePath, String fileName, Integer userId, int dirId){
        //  初始化用户信息
        User user = new User(userId);
        File file = new File(filePath);
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        byte[] bytes = readBytes(inputStream);
        return enclosureImageUpload(bytes,fileName,user,dirId);
    }
    /**
     * 数据流格式存在文件到系统中
     * @param inputStream 输入流
     * @param fileName 文件名称
     * @param userId 用户id
     * @param dirId 文件目录
     * @return 文件id(直接赋值流程字段)
     */
    public static int uploadFileStream(InputStream inputStream, String fileName, Integer userId, int dirId) {
        //  初始化用户信息
        User user = new User(userId);
        //  调用存储文件方法，返回文件id
        return uploadFileStream(inputStream, fileName, user, dirId);
    }
    /**
     * 数据流格式存在文件到系统中（.docx不支持）
     * @param inputStream 输入流
     * @param fileName 文件名称
     * @param user 用户信息
     * @param dirId 文件目录
     * @return 文件id(直接赋值流程字段)
     */
    public static int uploadFileStream(InputStream inputStream, String fileName, User user, int dirId) {
        Integer result = 0;
        try {
            byte[] bytes = toByteArray(inputStream);
            result = enclosureImageUpload(bytes, fileName, user, dirId);
        } catch (IOException e) {
            log.info("文件存储到OA异常");
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.info("关闭数据流错误！");
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    /**
     * 本地文件存储到OA系统文件服务器
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @param user 用户信息
     * @param dirId 文件目录
     * @return 文件id
     */
    public static Integer enclosureImageUpload(String filePath, String fileName, User user, int dirId){
        File file = new File(filePath);
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        byte[] bytes = readBytes(inputStream);
        return enclosureImageUpload(bytes,fileName,user,dirId);
    }
    /**
     * 以字节方式存在文件到OA系统
     * @param bytes 文件字节数组
     * @param fileName 文件名称
     * @param user 用户信息
     * @param dirId 文件目录
     * @return 文件id（直接赋值到文件字段）
     */
    private static Integer enclosureImageUpload(byte[] bytes, String fileName, User user, int dirId) {
        Integer docId = -1;
        //  存储文件名称
        imageFileManager.setImagFileName(fileName);
        //  保存文件数据
        imageFileManager.setData(bytes);
        //  保存文件返回文件id
        int imageFileId = imageFileManager.saveImageFile();
        try {
            //  文件存储到docimage
            docId = docSaveService.accForDoc(dirId, imageFileId, user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return docId;
    }
}
