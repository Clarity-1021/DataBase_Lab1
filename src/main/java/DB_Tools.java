import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_Tools {

    static final String DB_URL = "jdbc:mysql://localhost:3306/";//建数据库用

    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    static final boolean isLog = false;

    private static final String SQL = "SELECT * FROM ";// 数据库操作

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
     * 关闭预编译语句
     */
    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                if (isLog) {
                    System.out.println("预编译语句已关闭.");
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
     */
    public static List<String> getColumnNames(String DB_URL, String tableName) {
        List<String> columnNames = new ArrayList<String>();
        //与数据库的连接
        Connection conn = getConnection(DB_URL);
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            if (isLog) {
                System.out.println("正要执行:");
                System.out.println(tableSql);
            }
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
            if (isLog) {
                System.out.println("正要执行:");
                System.out.println(tableSql);
            }
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
     * 获取表中所有字段的完整性约束
     */
    public static List<Integer> isNullable(String DB_URL, String tableName) {
        List<Integer> columnTypes = new ArrayList<Integer>();
        //与数据库的连接
        Connection conn = getConnection(DB_URL);
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            if (isLog) {
                System.out.println("正要执行:");
                System.out.println(tableSql);
            }
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.isNullable(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePreparedStatement(pStemt);
            closeConnection(conn);
        }
        return columnTypes;
    }
}
