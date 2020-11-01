
import java.io.*;


class Test2
{
    public static void main(String[] args) throws IOException
    {
        /*
         * 文件由ANSI转化为UTF-8
         * 需要用到流InputStreamReader和OutputStreamWriter
         * 这两个流有charset功能
         * */
        File srcFile = new File("D://room.csv");
        File destFile = new File("r.csv");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(srcFile), "GBK"); //ANSI编码
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8"); //存为UTF-8

        int len = isr.read();
        while(-1 != len)
        {

            osw.write(len);
            len = isr.read();
        }
        //刷新缓冲区的数据，强制写入目标文件
        osw.flush();
        osw.close();
        isr.close();
    }
}