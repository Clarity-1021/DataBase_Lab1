import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DB_Tools_SQLite {

    private static final String DB_URL = "jdbc:sqlite:D:\\xxxdatabase.db";

    private static final String DB_DRIVER = "org.sqlite.JDBC";//sqlite的驱动

    static {
        try {
            Class.forName(DB_DRIVER);
            if (DB_Tools.isLog) {
                System.out.println("SQLite驱动注册成功.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(String DB_URL) {
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
    public static List<String> getTableNames(String DB_URL) {
        return DB_Tools.getTableNames(DB_URL);
    }

    /**
     * 获取表中所有字段名称
     */
    public static List<String> getColumnNames(String DB_URL, String tableName) {
        return DB_Tools.getColumnNames(DB_URL, tableName);
    }

    public static void insertValues(String dbPath, String tablename, List<String> orderedAttrs, TableMetaInfo_Mysql tf) {
        Connection dbConnection = getConnection(dbPath);
        PreparedStatement preparedStatement = null;
        String sql = "select * from " + tablename;
        try {
            preparedStatement = dbConnection.prepareStatement(sql);

            if (DB_Tools.isLog) {
                System.out.println("正要执行:");
                System.out.println(sql);            }

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                StringBuilder sb = new StringBuilder();

                for (String attr : orderedAttrs) {
//                    System.out.println(attr + "=" + rs.getString(attr));
                    String a = rs.getString(attr);
                    if (a.equals("")) {
                        sb.append("null");
                    }
                    else {
                        sb.append(a);
                    }
                    sb.append(",");
//                    System.out.println(sb.toString());
                }
                String line = sb.toString();
//                System.out.println("line=" + line);
//                line += "\r\n";
//                appendintxt(txtPath, line);
                String[] args = line.split(",");

                DB_Tools_Mysql.insertValue(tablename, tf, args);
            }

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
}