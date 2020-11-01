import java.sql.*;

public class InsertRoom {
    public static void main(String[] args) throws Exception {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        //sql语句
        String sql = "CREATE TABLE Person("
                + "USER_ID NUMBER(5) NOT NULL, "
                + "USERNAME VARCHAR(20) NOT NULL, "
                + "CREATED_BY VARCHAR(20) NOT NULL, "
                + "CREATED_DATE DATE NOT NULL, "
                + "PRIMARY KEY (USER_ID) "
                + ")";

        Class.forName(DB_Tools.DB_DRIVER);//加载驱动到内存并注册
        dbConnection = DriverManager.getConnection(DB_Tools.DB_URL, DB_Tools.DB_USER, DB_Tools.DB_PASSWORD);//连接数据库

        preparedStatement = dbConnection.prepareStatement(sql);

        //可以反复执行，这里只用执行一次
        preparedStatement.executeUpdate();

        //关闭来释放资源
        preparedStatement.close();
        dbConnection.close();
    }
}
