import java.sql.*;
import java.util.List;

public class CreateTable {
    private static final String sqlPath = "src/main/sql/createTable.txt";

    /**
     * 用文本里的sql语句建表
     */
    public static void main(String[] args){
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
            DB_Tools.closePreparedStatement(preparedStatement);
            DB_Tools.closeConnection(dbConnection);
        }
    }
}
