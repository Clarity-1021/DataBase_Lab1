import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
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

    public static void insertValues(String txtPath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(txtPath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] args = line.split("\t");
                System.out.println("args=" + Arrays.toString(args));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行sql
     */
    public static void exesql(String sql){
        Connection dbConnection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement(sql);

            if (DB_Tools.isLog) {
                System.out.println("正要执行:");
                System.out.println(sql);
            }

            preparedStatement.execute();

            if (DB_Tools.isLog) {
                System.out.println("执行成功.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection(dbConnection);
        }
    }

    public static void insertValue(String sql, String[] args) {
        Connection dbConnection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.prepareStatement(sql);

            if (DB_Tools.isLog) {
                System.out.println("正要执行:");
                System.out.println(sql);
            }

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setString(i + 1, args[i]);
            }

            preparedStatement.executeUpdate();

            if (DB_Tools.isLog) {
                System.out.println("执行成功.");
            }
        } catch (SQLException e) {
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
            for (String attribute : attributes) {
                System.out.println(attribute + "->" + tf.getType(attribute) + ", " + tf.getIsNullable(attribute));
            }
//            System.out.println("ColumnNames:" + getColumnNames(DB_URL, tableName));
//            System.out.println("ColumnTypes:" + getColumnTypes(DB_URL, tableName));
//            System.out.println("Nullable:" + isNullable(DB_URL, tableName));
//            System.out.println("ColumnComments:" + getColumnComments(DB_URL, tableName));
        }
    }
}