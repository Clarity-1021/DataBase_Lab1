import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class CheckUTF8 {

    public static void main(String[] args) {
        try {
            System.out.println(isUtf("D://student.csv"));
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 用有限状态机写的
     */
    public static boolean isUtf(String filePath) throws IOException{
        FileInputStream fis=new FileInputStream(filePath);
        byte[] bbuf=new byte[1024];
        int L=-1;
        int status=0;//状态
        int oneByteCount=0;
        int twoByteCount=0;
        int threeByteCount=0;
        int fourByteCount=0;
        int errorCount=0;
        while((L=fis.read(bbuf))!=-1){
            for (int i = 0; i <L; i++) {
                byte b=bbuf[i];
// System.out.print(Integer.toHexString(b&0xff)+"|");
                switch (status) {
                    case 0:
                        if(b >= 0)//一个字节的首字节范围
                            oneByteCount++;//一个字节的数量加一
                        else if(b>=(byte)0xC0&&b<=(byte)0xDF)//两个字节的首字节范围
                            status=2;//跳到状态2
                        else if(b>=(byte)0xE0&&b<=(byte)0XEF)//三个字节的首字节范围
                            status=4;//跳到状态4
                        else if(b>=(byte)0xF0&&b<=(byte)0xF7)//四个字节的首字节范围
                            status=7;//跳到状态7
                        else
                            errorCount++;

                        break;
                    case 2:
                        if(b <= (byte) 0xBF){//两个字节的第二个字节范围
                            twoByteCount++;//
                            status=0;
                        }else{
                            errorCount+=2;
                            status=0;
                        }
                        break;
                    case 4:
                        if(b <= (byte) 0xBF)//三个字节的第二个字节的范围
                            status=5;
                        else{
                            errorCount+=2;
                            status=0;
                        }
                        break;
                    case 5:
                        if(b <= (byte) 0xBF){//三个字节的第三个字节的范围
                            threeByteCount++;
                            status=0;
                        }else{
                            errorCount+=3;
                            status=0;
                        }
                        break;
                    case 7:
                        if(b <= (byte) 0xBF){//四个字节的第二个字节的范围
                            status=8;
                        }else{
                            errorCount+=2;
                            status=0;
                        }
                        break;
                    case 8:
                        if(b <= (byte) 0xBF){//四个字节的第三个字节的范围
                            status=9;
                        }else{
                            errorCount+=3;
                            status=0;
                        }
                        break;
                    case 9:
                        if(b <= (byte) 0xBF){//四个字节的第四个字节的范围
                            fourByteCount+=4;
                            status=0;
                        }else{
                            errorCount++;
                            status=0;
                        }
                        break;
                    default:
                        break;
                }
            }

        }

//        System.out.println("一个字节的有："+oneByteCount);
//        System.out.println("两个字节的有："+twoByteCount);
//        System.out.println("三个字节的有："+threeByteCount);
//        System.out.println("四个字节的有："+fourByteCount);
//        System.out.println("错误个数："+errorCount);
//        System.out.println("总共字节数有："+(oneByteCount+twoByteCount*2+threeByteCount*3+fourByteCount*4+errorCount));

        return errorCount == 0;
    }

}