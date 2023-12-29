package weaver.action.Base64convert;


import java.io.*;
import java.util.Base64;

/**
 * 文件处理工具类
 * 1、文件转字符   2、将一个字符串转化为输入流 3、删除文件 4、解决带有dom编码乱码问题
 * by fsl
 */
public class FileUtils {


    public static void main(String[] args) {
//        System.out.println("大苏打打赏");
//        System.out.println(readFileToString("D:\\项目信息 .txt",true));

       // delAllFile("D:\\fileTest");

        //deleteFileByPath("D:\\fileTest");//删除文件  文件夹除外
        System.out.println(getBase64str("D:\\settings.xml"));
    }

    /**
     * 根据文件绝对地址返回该文件的base64编码
     *
     * @param aFilePath 文件绝对路径
     * @return
     */
    public static String getBase64str(String aFilePath) {
        String re = null;
        try {
            File file = new File(aFilePath);
            if (file.exists()) {
                Base64.Encoder encoder =  Base64.getEncoder();
                byte[] bytes = fileToByteArray(aFilePath);
                re = encoder.encodeToString(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public static byte[] fileToByteArray(String filePath) {
        //1.创建源yu目的地
        File file = new File(filePath);
        byte[] ds = null;
        //选择流
        InputStream zp = null;
        ByteArrayOutputStream boos = null;
        boos = new ByteArrayOutputStream();
        try {
            zp = new FileInputStream(file);
            byte[] frush = new byte[1024];//1024表示1k为一段
            int len = -1;
            while((len=zp.read(frush))!=-1) {
                boos.write(frush,0,len);//写出到字节数组中

            }
            boos.flush();
            return boos.toByteArray();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(zp!=null) {
                try {
                    zp.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * 根据本地路径获取字符串数据
     * @param path 本地文件路径
     * @return string
     */
    public static String readFileToString(String path,Boolean isChangeLine)
    {
        int len=0;
        StringBuffer str=new StringBuffer("");
        File file=new File(path);
        try {
            FileInputStream is=new FileInputStream(file);
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader in= new BufferedReader(isr);
            String line=null;
            while( (line=in.readLine())!=null )
            {
                if(isChangeLine){
                    if(len != 0) // 处理换行符的问题
                    {
                        str.append("\r\n"+line);
                    }
                    else
                    {
                        str.append(line);
                    }
                }
                str.append(line);
                len++;
            }
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    //将一个字符串转化为输入流
    public static InputStream getStringStream(String sInputString){
        if (sInputString != null && !sInputString.trim().equals("")){
            try{
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 遍历路径下文件
     * @param path
     */
    public static File[] files(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }


    /**
     * 通过路径删除文件文件夹除外
     * @param path 本地绝对路径
     */
    private static void deleteFileByPath(String path) {
        int flag;
        File file = new File(path);
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            flag = 0;
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                //deleteFileByPath(name);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        //file.delete();
    }

    /**
     * 删除文件夹下所有文件
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解决带有bom乱码问题
     * @param file
     * @return
     * @throws IOException
     */
    public static char[] loadWithBomFile(String file) throws IOException {
        // read text file, auto recognize bom marker or use
        // system default if markers not found.
        BufferedReader reader = null;
        CharArrayWriter writer = null;
        UnicodeReader r = new UnicodeReader(new FileInputStream(file), null);

        char[] buffer = new char[16 * 1024];   // 16k buffer
        int read;
        try {
            reader = new BufferedReader(r);
            writer = new CharArrayWriter();
            while( (read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
            writer.flush();
            return writer.toCharArray();
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                writer.close(); reader.close(); r.close();
            } catch (Exception ex) { }
        }
    }
}
