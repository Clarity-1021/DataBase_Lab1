import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class DB_Tools_Mysql {

    static final String DB_URL = "jdbc:mysql://localhost:3306/dblab";//建表和插入表用

    static final String DB_DRIVER = "com.mysql.jdbc.Driver";//mysql驱动

    private static final String sqlPath = "D://createTable.txt";//建表sql语句存放的位置

    static {
        try {
            Class.forName(DB_DRIVER);
            if (DB_Tools.isLog) {
                System.out.println("Mysql驱动注册成功.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用文本里的sql语句建表
     */
    public static void createTables(){
        Connection dbConnection = DB_Tools.getConnection(DB_Tools.DB_URL);
        PreparedStatement preparedStatement = null;
        List<String> sqls = DB_Tools.sqlsToExe(sqlPath);
        try {
            for (String sql : sqls) {
                preparedStatement = dbConnection.prepareStatement(sql);

                if (DB_Tools.isLog) {
                    System.out.println("正要执行:");
                    System.out.println(sql);
                }

                //可以反复执行
                preparedStatement.executeUpdate();

                if (DB_Tools.isLog) {
                    System.out.println("执行成功.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection(dbConnection);
        }
    }

    //args是排过序的，和表中属性的顺序一致
    public static void insertValue(String tablename, TableMetaInfo_Mysql tf, List<List<String>> argsList) {
        List<String> attribute_T = tf.getAttributes();//获得表中顺序排列的属性名
        StringBuilder sb = new StringBuilder();
        sb.append("insert ignore into ").append(tablename).append("(");
        for (int i = 0; i < attribute_T.size(); i++) {
            sb.append(attribute_T.get(i));
            if (i != attribute_T.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(") values(");
        for (int i = 0; i < attribute_T.size(); i++) {
            sb.append("?");
            if (i != attribute_T.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        String sql = sb.toString();

        Connection dbConnection = getConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(sql);

            if (DB_Tools.isLog) {
                System.out.println("正要执行:");
                System.out.println(sql);
            }

            for (List<String> args : argsList) {
                for (int i = 0; i < args.size(); i++) {
                    String arg = args.get(i);
                    String type = tf.getType(i);
                    if ("TINYINT".equals(type) || "SMALLINT".equals(type) || "MEDIUMINT".equals(type) || "BOOLEAN".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
                            }
                            else {
                                if ("TINYINT".equals(type) || "BOOLEAN".equals(type)) {
                                    preparedStatement.setNull(i + 1, Types.TINYINT);
                                }
                                else if ("SMALLINT".equals(type)) {
                                    preparedStatement.setNull(i + 1, Types.SMALLINT);
                                }
                                else {
                                    preparedStatement.setNull(i + 1, Types.INTEGER);
                                }
                            }
                        }
                        else {
                            preparedStatement.setInt(i + 1, Integer.parseInt(arg));
                        }
                    } else if ("INT".equals(type) || "INTEGER".equals(type) || "ID".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.INTEGER);
                            }
                        }
                        else {
                            preparedStatement.setLong(i + 1, Long.parseLong(arg));
                        }
                    } else if ("VARCHAR".equals(type) || "CHAR".equals(type) || "TEXT".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
//                            preparedStatement.setString(i + 1, args[i]);
                            }
                            else {
                                if ("VARCHAR".equals(type) || "TEXT".equals(type)) {
                                    preparedStatement.setNull(i + 1, Types.VARCHAR);
                                }
                                else {
                                    preparedStatement.setNull(i + 1, Types.CHAR);
                                }
                            }
                        }
                        else {
                            preparedStatement.setString(i + 1, arg);
                        }
                    } else if ("DATETIME".equals(type) || "TIMESTAMP".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
//                            System.out.println("数据完整性约束为非空不能插入.");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                java.util.Date date = sdf.parse("0000-00-00 00:00:00");
                                long lg = date.getTime(); //日期转时间戳
//                            java.sql.Date t = new java.sql.Date(lg);
                                preparedStatement.setTimestamp(i + 1, new java.sql.Timestamp(lg));
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.DATE);
                            }
                        }
                        else {
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//注意月份是MM
                            String pattern = CheckTimeDateFormate.matchDateTimeFormat(arg);
                            if (pattern == null) {
                                System.out.println("data time format wrong, this data will not insert in table.");
                                System.out.println("datatime=" + arg);
                                continue;
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            java.util.Date date = simpleDateFormat.parse(arg);
                            long lg = date.getTime(); //日期转时间戳
                            preparedStatement.setTimestamp(i + 1, new java.sql.Timestamp(lg));
                        }
                    } else if ("BLOB".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.BLOB);
                            }
                        }
                        else {
                            preparedStatement.setBytes(i + 1, arg.getBytes());
                        }
                    } else if ("BIT".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.BIT);
                            }
                        }
                        else {
                            if (!arg.equals("0")) {
                                preparedStatement.setBoolean(i + 1, true);
                            } else {
                                preparedStatement.setBoolean(i + 1, false);
                            }
                        }
                    } else if ("BIGINT".equals(type) || "DECIMAL".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
//                            preparedStatement.setBigDecimal(i + 1, BigDecimal.valueOf(0));
                            }
                            else {
                                if ("BIGINT".equals(type)) {
                                    preparedStatement.setNull(i + 1, Types.BIGINT);
                                }
                                else {
                                    preparedStatement.setNull(i + 1, Types.DECIMAL);
                                }
                            }
                        }
                        else {
                            preparedStatement.setBigDecimal(i + 1, new BigDecimal(arg));
                        }
                    } else if ("FLOAT".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
//                            preparedStatement.setFloat(i + 1, 0);
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.FLOAT);
                            }
                        }
                        else {
                            preparedStatement.setFloat(i + 1, Float.parseFloat(arg));
                        }
                    } else if ("DOUBLE".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
                                System.out.println("数据完整性约束为非空不能插入.");
//                            preparedStatement.setDouble(i + 1, 0);
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.DOUBLE);
                            }
                        }
                        else {
                            preparedStatement.setDouble(i + 1, Double.parseDouble(arg));
                        }
                    } else if ("DATE".equals(type) || "YEAR".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
//                            System.out.println("数据完整性约束为非空不能插入.");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date date = simpleDateFormat.parse("0000-00-00");
                                long lg = date.getTime(); //日期转时间戳
                                preparedStatement.setDate(i + 1, new java.sql.Date(lg));
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.DATE);
                            }
                        }
                        else {
                            String pattern = CheckTimeDateFormate.matchDateFormat(arg);
                            if (pattern == null) {
                                System.out.println("date format wrong, this data will not insert in table.");
                                continue;
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            java.util.Date date = simpleDateFormat.parse(arg);
                            long lg = date.getTime(); //日期转时间戳
                            preparedStatement.setDate(i + 1, new java.sql.Date(lg));

//                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//                        java.util.Date date = sdf.parse("00:00:00");
//                        long lg = date.getTime(); //日期转时间戳
//                        preparedStatement.setTime(i + 1, new java.sql.Time(lg));
                        }
                    } else if ("TIME".equals(type)) {
                        if (arg.equals("null")) {
                            if (!tf.getIsNullable(i)) {
//                            System.out.println("数据完整性约束为非空不能插入.");
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:");
                                java.util.Date date = sdf.parse("00:00:00");
                                long lg = date.getTime(); //日期转时间戳
                                preparedStatement.setTime(i + 1, new java.sql.Time(lg));
                            }
                            else {
                                preparedStatement.setNull(i + 1, Types.TIME);
                            }
                        }
                        else {
                            String pattern = CheckTimeDateFormate.matchTimeFormat(arg);
                            if (pattern == null) {
                                System.out.println("time format wrong, this data will not insert in table.");
                                continue;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                            java.util.Date date = sdf.parse(arg);
                            long lg = date.getTime(); //日期转时间戳
                            preparedStatement.setTime(i + 1, new java.sql.Time(lg));
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
//                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//注意月份是MM
//                        java.util.Date d = sdf.parse(args[i]);
//                        preparedStatement.setTime(i + 1, new Time(d.getTime()));
                        }
                    } else {
                        System.out.println("no such data type, this record not insert.");
                    }
                }
                preparedStatement.addBatch();
            }


            preparedStatement.executeBatch();

            if (DB_Tools.isLog) {
                System.out.println("执行成功.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection(dbConnection);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        return DB_Tools.getConnection(DB_URL);
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        DB_Tools.closeConnection(conn);
    }

    /**
     * 关闭预准备语句
     */
    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        DB_Tools.closePreparedStatement(preparedStatement);
    }

    /**
     * 关闭结果集
     */
    public static void closeResultSet(ResultSet rs) {
        DB_Tools.closeResultSet(rs);
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames() {
        return DB_Tools.getTableNames(DB_URL);
    }

    /**
     * 获取表中所有字段名称
     */
    public static List<String> getColumnNames(String tableName) {
        return DB_Tools.getColumnNames(DB_URL, tableName);
    }

    /**
     * 获取表中所有字段类型
     */
    public static List<String> getColumnTypes(String tableName) {
        return DB_Tools.getColumnTypes(DB_URL, tableName);
    }

    /**
     * 获取表中所有字段的完整性约束
     */
    public static List<Integer> isNullable(String tableName) {
        return DB_Tools.isNullable(DB_URL, tableName);
    }

    public static void main(String[] args) {
        List<String> tableNames = getTableNames();
        System.out.println("tableNames:" + tableNames);
        for (String tableName : tableNames) {
            TableMetaInfo_Mysql tf = new TableMetaInfo_Mysql(tableName);
            List<String> attributes =  tf.getAttributes();
            System.out.println("attributes=" + attributes);
            for (int i = 0; i < attributes.size(); i++) {
                System.out.println(attributes.get(i) + "->" + tf.getType(i) + ", " + tf.getIsNullable(i));
            }
//            System.out.println("ColumnNames:" + getColumnNames(DB_URL, tableName));
//            System.out.println("ColumnTypes:" + getColumnTypes(DB_URL, tableName));
//            System.out.println("Nullable:" + isNullable(DB_URL, tableName));
//            System.out.println("ColumnComments:" + getColumnComments(DB_URL, tableName));
        }
    }
}