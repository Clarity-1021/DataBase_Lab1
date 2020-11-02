import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableMetaInfo_Mysql {
    private List<String> Attributes;
    private Map<String, Integer> AttributeIndexs;
    private List<String> Types;
    private List<Boolean> IsNullables;

    public TableMetaInfo_Mysql(String tableName) {
        Attributes = DB_Tools_Mysql.getColumnNames(tableName);
        Types = DB_Tools_Mysql.getColumnTypes(tableName);

        List<Integer> isNullable = DB_Tools_Mysql.isNullable(tableName);
        IsNullables = new ArrayList<Boolean>();
        for (int i : isNullable) {
            if (i == 0) {
                IsNullables.add(false);
            }
            else {
                IsNullables.add(true);
            }
        }

        AttributeIndexs = new HashMap<String, Integer>();
        for (int i = 0; i < Attributes.size(); i++) {
            AttributeIndexs.put(Attributes.get(i), i);
        }
    }

    public List<String> getAttributes() {
        return Attributes;
    }

    public String getType(String attribute) {
        return Types.get(AttributeIndexs.get(attribute));
    }

    public boolean getIsNullable(String attribute) {
        return IsNullables.get(AttributeIndexs.get(attribute));
    }
}
