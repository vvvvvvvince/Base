<%@ page import="java.io.InputStream" %>
<%@ page import="weaver.hrm.User" %>
<%@ page import="weaver.file.ImageFileManager" %>
<%@ page import="weaver.conn.RecordSet" %>
<%@ page import="com.weaver.util.dev.doc.DocUtils" %>
<%@ page import="com.weaver.util.dev.log.LogFactory" %>
<%@ page import="com.weaver.util.dev.log.DevLog" %>
<%
    LogFactory log = new DevLog();
    ImageFileManager imageFileManager = DocUtils.getImageFileInfoByDocid(6711, new RecordSet());
    InputStream imageStream = DocUtils.getInputStream(6716,new RecordSet());
    log.info("..........................Stream===="+imageStream+"..............................");
    Integer myInteger = new Integer(696);
    int result=DocUtils.uploadFileStream(imageStream,imageFileManager.getImageFileName(),1,548);
    log.info("result：："+result);

%>
