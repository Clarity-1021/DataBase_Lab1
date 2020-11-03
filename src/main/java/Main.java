import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
//        String isCreate = readFromConsole("Delate database name dblab and Create database dblab?[yes to create]");
//        if (!isCreate.equals("yes")) {
//            System.out.println("bye~");
//            return;
//        }

        //建表
        DB_Tools_Mysql.createTables();

        //获取表的元数据
        List<String> TableNames = DB_Tools_Mysql.getTableNames();//获得表名
        HashMap<String, TableMetaInfo_Mysql> TableMetas = new HashMap<String, TableMetaInfo_Mysql>();
        for (String tableName : TableNames) {
            TableMetas.put(tableName, new TableMetaInfo_Mysql(tableName));
        }

        //插csv
//        for (String tablename : TableNames) {
//            String hascsv = readFromConsole("INSERT csv DATA for table " + tablename + "?[yes to enter csv]");
//            if (hascsv.equals("yes")) {
//                String csvPath = readFromConsole("Enter csv Path[eg.D://room.csv;D://student.csv]");

                String csvPath = "D://room.csv";
                String tablename = "room";
                File file = new File(csvPath);
                if (!file.exists()) {
                    System.out.println("csv not exist.");
//                    continue;
                }

                try {
                    boolean isutf8 = CheckUTF8.isUtf(csvPath);
                    System.out.println(isutf8);
                    if (!isutf8) {//这次lab不是utf8就是gbk，所以直接gbk转utf8了
                        String destPath = TimeStampNaming();
                        parseANSItoUTF8(csvPath, destPath);//转码为utf8
                        csvPath = destPath;
                    }
                    System.out.println("csvPath=" + csvPath);

                    insertcsv(csvPath, TableMetas.get(tablename), tablename);

                    if (!isutf8) {//转utf8，用完之后删掉
                        File newFile = new File(csvPath);
                        if (!newFile.delete()) {
                            System.out.println("fail to delete utf8 file.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
//        }

        //插db
//        String hasdb = readFromConsole("INSERT db DATA?[yes to enter db]");
////        if (hasdb.equals("yes")) {
////            String dbPath = readFromConsole("Enter db Path[eg.D://xxxdatabase.db]");
////            File file = new File(dbPath);
////            if (!file.exists()) {
////                System.out.println("db not exist.");
////                return;
////            }
////
////            dbPath = "jdbc:sqlite:" + dbPath;
////            insertDB(dbPath, TableMetas);
////        }

        System.out.println("insert data end.");
        System.out.println("bye~");
    }

    /**
     * 从控制台读一行的字符串
     */
    public static String readFromConsole(String INFO){
        String result = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(INFO + " (Only in one line):");
            result = br.readLine();
//            System.out.println("Your input is: \n" + content.substring(0, content.lastIndexOf("\n")));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 插csv格式的数据
     */
    public static void insertcsv(String csvPath, TableMetaInfo_Mysql tf, String tablename) {
        List<String> attributes_T = tf.getAttributes();//拿到被插的有序的属性名
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvPath));
            String[] attribute_csv = null;
            String line = null;
            if ((line = br.readLine()) != null) {//读第一行名字
                attribute_csv = line.split(",");
            }

            if(attribute_csv != null) {
                HashMap<String, Integer> attributeIndexs = new HashMap<String, Integer>();
                for (int i = 0; i < attribute_csv.length; i++) {
                    attributeIndexs.put(attribute_csv[i], i);
                }
                List<String> orderedAttrs = new ArrayList<String>();
                for (String attr : attributes_T) {
//                    System.out.println("attr_T=" + attr);
                    String atr = null;
                    for (String attr_csv : attribute_csv) {
//                        System.out.println("attr_db=" + attr_db);
                        if (attr.equals(attr_csv) | attr.contains(attr_csv) | attr_csv.contains(attr)) {//默认字段的名字如果包含或者被包含则是一致的
                            atr = attr_csv;
                            break;
                        }
                    }
                    if (atr == null) {
                        System.out.println("插入的数据不合法.");
                        return;
                    }
                    orderedAttrs.add(atr);
                }
                while ((line = br.readLine()) != null) {
                    int lastEnd = 0;
                    int newStart = 0;
                    String[] as = new String[attributes_T.size()];
                    int inx = 0;
                    while (newStart < line.length()) {
                        if (inx == attributes_T.size() - 1) {
                            as[inx] = line.substring(newStart);
                            break;
                        }
                        for (int i = newStart; i < line.length(); i++) {
                            if (line.charAt(i) == ',') {
                                lastEnd = i;
                                break;
                            }
                        }
                        if (lastEnd == newStart) {
                            as[inx++] = "null";
                        }
                        else {
                            as[inx++] = line.substring(newStart, lastEnd);
                        }
                        newStart = lastEnd + 1;
                    }
                    if (inx == attributes_T.size() - 1) {
                        as[inx] = "null";
                    }

                    String[] args = new String[attributes_T.size()];
                    for (int i = 0; i < orderedAttrs.size(); i++) {
                        String str = as[attributeIndexs.get(orderedAttrs.get(i))];
                        while (str.indexOf("\"") == 0){
                            if (str.length() > 1) {
                                str = str.substring(1);   //去掉第一个 "
                            }
                            else {
                                str = "null";
                            }
                        }
                        while (str.lastIndexOf("\"") == (str.length() - 1)){
                            if (str.length() > 1) {
                                str = str.substring(0, str.length() - 1);  //去掉最后一个 "
                            }
                            else {
                                str = "null";
                            }
                        }
                        args[i] = str;
                    }

                    DB_Tools_Mysql.insertValue(tablename, tf, args);
//                        System.out.println("args=" + Arrays.toString(args));
                }

                br.close();
            }
            else {
                System.out.println("表不对.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插db格式的数据
     */
    public static void insertDB(String dbPath, HashMap<String, TableMetaInfo_Mysql> TableMetas) {
        List<String> tnames = DB_Tools_SQLite.getTableNames(dbPath);
//        System.out.println("tableNames:" + tnames);
        for (String tname : tnames) {
            List<String> attribute_db = DB_Tools_SQLite.getColumnNames(dbPath, tname);
            if (TableMetas.containsKey(tname)) {//匹配表名和被插入的表一致
                TableMetaInfo_Mysql tf = TableMetas.get(tname);
                List<String> attributes_T = tf.getAttributes();//拿到被插的有序的属性名
                List<String> orderedAttrs = new ArrayList<String>();
                for (String attr : attributes_T) {
//                    System.out.println("attr_T=" + attr);
                    String atr = null;
                    for (String attr_db : attribute_db) {
//                        System.out.println("attr_db=" + attr_db);
                        if (attr.equals(attr_db) | attr.contains(attr_db) | attr_db.contains(attr)) {//默认字段的名字如果包含或者被包含则是一致的
                            atr = attr_db;
                            break;
                        }
                    }
                    if (atr == null) {
                        System.out.println("插入的数据不合法.");
                        return;
                    }
                    orderedAttrs.add(atr);
                }
                DB_Tools_SQLite.insertValues(dbPath, tname, orderedAttrs, tf);
//                    File txt=new File(txtPath);
//                    if (!txt.delete()) {
//                        System.out.println("txt数据删除失败.");
//                    }
            }
        }
    }

    /**
     * 得到当前时间的时间戳精确到毫秒
     */
    private static String TimeStampNaming(){
        Long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "temp-" + sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp)))) + ".csv";
    }

    /**
     * 把csv从ansi编码转成utf8编码
     */
    public static void parseANSItoUTF8(String csvPath, String destPath) {
        File srcFile = new File(csvPath);
        File destFile = new File(destPath);

        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(srcFile), "GBK"); //ANSI编码
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8"); //存为UTF-8

            int len = isr.read();
            while(-1 != len){
                osw.write(len);
                len = isr.read();
            }
            //刷新缓冲区的数据，强制写入目标文件
            osw.flush();

            osw.close();
            isr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
