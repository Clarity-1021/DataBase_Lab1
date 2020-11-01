import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_Tools {

    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    static final String DB_DBLAB_URL = "jdbc:mysql://localhost:3306/dblab";

    static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    static final boolean isLog = true;

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    static {
        try {
            Class.forName(DB_DRIVER);
            if (isLog) {
                System.out.println("驱动注册成功.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(String DB_URL) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (isLog) {
                System.out.println("数据库连接成功.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
                if (isLog) {
                    System.out.println("数据库连接已关闭.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭预准备语句
     */
    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                if (isLog) {
                    System.out.println("预准备语句已关闭.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭结果集
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                if (isLog) {
                    System.out.println("结果集已关闭.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得sql指令
     */
    public static List<String> sqlsToExe(String sqlPath){
        List<String> sqls = new ArrayList<String>();//sql语句
        StringBuilder sb = new StringBuilder();//读文件用的sb
        try {
            BufferedReader br = new BufferedReader(new FileReader(sqlPath));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
                if (sb.charAt(sb.length() - 1) == ';') {
                    sqls.add(sb.toString());
                    sb = new StringBuilder();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sqls;
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames(String DB_URL) {
        List<String> tableNames = new ArrayList<String>();
        Connection conn = getConnection(DB_URL);
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, new String[] { "TABLE" });
            while(rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closeConnection(conn);
        }
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     * @param tableName 表名
     */
    public static List<String> getColumnNames(String DB_URL, String tableName) {
        List<String> columnNames = new ArrayList<String>();
        //与数据库的连接
        Connection conn = getConnection(DB_URL);
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pStemt);
            closeConnection(conn);
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     */
    public static List<String> getColumnTypes(String DB_URL, String tableName) {
        List<String> columnTypes = new ArrayList<String>();
        //与数据库的连接
        Connection conn = getConnection(DB_URL);
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pStemt);
            closeConnection(conn);
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     */
    public static List<String> getColumnComments(String DB_URL, String tableName) {
        List<String> columnTypes = new ArrayList<String>();
        //与数据库的连接
        Connection conn = getConnection(DB_URL);
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        List<String> columnComments = new ArrayList<String>();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
            closePreparedStatement(pStemt);
            closeConnection(conn);
        }
        return columnComments;
    }

//    public static void main(String[] args) {
//        String DB_URL = DB_DBLAB_URL;
//        List<String> tableNames = getTableNames(DB_URL);
//        System.out.println("tableNames:" + tableNames);
//        for (String tableName : tableNames) {
//            System.out.println("ColumnNames:" + getColumnNames(DB_URL, tableName));
//            System.out.println("ColumnTypes:" + getColumnTypes(DB_URL, tableName));
//            System.out.println("ColumnComments:" + getColumnComments(DB_URL, tableName));
//        }
//    }
}