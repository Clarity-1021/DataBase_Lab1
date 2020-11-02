import java.util.List;

public class T4 {
    private List<String> tableNames;

    T4(String DB_URL) {
        tableNames = DB_Tools.getTableNames(DB_URL);

    }

    public static void main(String[] args) {
        T4 A = new T4(DB_Tools.DB_DBLAB_URL);

    }
}
