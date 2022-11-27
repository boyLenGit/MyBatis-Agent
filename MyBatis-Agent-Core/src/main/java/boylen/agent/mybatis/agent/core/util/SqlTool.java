package boylen.agent.mybatis.agent.core.util;

import org.apache.ibatis.binding.MapperMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SqlTool {
    public static void main(String[] args) {
        test_formatSqlForExtractTable();
    }

    private static void test_formatSqlForExtractTable() {
        String sql = "SELECT * FROM user_status WHERE userId = ?\n" +
                "        ORDER BY endTime DESC\n" +
                "        LIMIT 1;";
        String sql2 = "SELECT * FROM user_status WHERE userId = ? AND orderId = \"2022101716415174505BFE08A65274FF\"\n" +
                "        ORDER BY endTime DESC\n" +
                "        LIMIT 1;";
        String sql3 = "SELECT\n" +
                "COUNT(*) count\n" +
                "        FROM\n" +
                "            payment\n" +
                "        WHERE\n" +
                "            status = 2 AND sandbox = false AND payFee > 0 AND platform != 'partner' \n" +
                "\t\t\t\t\t\tAND planId = 22";
        String sqlFormat = formatSqlNoParameter(sql3);
        System.out.println(sqlFormat);
        System.out.println(getTableName(sqlFormat));
    }

    private static void test_ExtractTableName() {
        //SELECT 列名称（*所有列） FROM 表名称
        //SELECT 列名称 FROM 表名称 where 条件
        System.out.println(getTableName("select * from aaa"));
        System.out.println(getTableName("select id,name,password from bbb where id = 1 "));
        //INSERT INTO 表名称 VALUES (值1, 值2,....)
        //INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
        System.out.println(getTableName("insert into ccc value(1,'neo','password')"));
        System.out.println(getTableName("insert into ddd(id,name,password) values(1,'neo','password')"));
        //UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
        System.out.println(getTableName("update eee set name = 'neo' where id = 1 "));
        //DELETE FROM 表名称 WHERE 列名称 = 值
        System.out.println(getTableName("delete from fff where id = 1 "));

        String sql = "delete from fff where id = 1 ";
        String changedSql = getTableName(sql);
        System.out.println(changedSql);
    }

    public static String formatSqlNoParameter(String sql) {
        StringBuilder stringNoParam = new StringBuilder();
        int cnt = 1;
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '\'' || sql.charAt(i) == '\"') {
                i++;
                for (; i < sql.length(); i++) {
                    if (sql.charAt(i) == '\'' || sql.charAt(i) == '\"') {
                        stringNoParam.append('!').append(cnt++);
                        break;
                    }
                }
            } else {
                stringNoParam.append(sql.charAt(i));
            }
        }
        String sqlNoParam = stringNoParam.toString().replace("\n", " ").replace("\t", " ");
        StringBuilder stringFinal = new StringBuilder();
        for (int i = 0; i < sqlNoParam.length(); i++) {
            if (i != 0 && (sqlNoParam.charAt(i) == ' ' && sqlNoParam.charAt(i - 1) == ' ') || (sqlNoParam.charAt(i) == ';')) {
                continue;
            }
            stringFinal.append(sqlNoParam.charAt(i));
        }
        return stringFinal.toString();
    }

    public static String[] formatSqlWithParameter(String sql, Object arg) {
        StringBuilder params = new StringBuilder();
        if (arg instanceof String) {
            params.append(arg);
        } else if (arg instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) arg;
            for (int i = 1; i <= paramMap.size() / 2; i++) {
                params.append(paramMap.get("param" + i)).append(", ");
            }
        }
        String sqlNoParam = sql.replace("\n", " ").replace("\t", " ");
        StringBuilder stringFinal = new StringBuilder();
        for (int i = 0; i < sqlNoParam.length(); i++) {
            if (i != 0 && (sqlNoParam.charAt(i) == ' ' && sqlNoParam.charAt(i - 1) == ' ') || (sqlNoParam.charAt(i) == ';')) {
                continue;
            }
            stringFinal.append(sqlNoParam.charAt(i));
        }
        return new String[]{stringFinal.toString(), params.toString()};
    }

    /**
     * 提取数据表的名字
     *
     * @param sql lowcase
     * @return
     */
    public static String getTableName(String sql) {
        sql = sql.toLowerCase();
        Matcher matcher = null;
        //SELECT 列名称 FROM 表名称
        //SELECT * FROM 表名称
        if (sql.startsWith("select")) {
            if (sql.contains("where")) {
                matcher = Pattern.compile("select\\s.+from\\s(.+)where\\s(.*)").matcher(sql);
            } else {
                matcher = Pattern.compile("select\\s.+from\\s(.+)").matcher(sql);
            }
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        // INSERT INTO 表名称 VALUES (值1, 值2,....)
        // INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
        if (sql.startsWith("insert")) {
            matcher = Pattern.compile("insert\\sinto\\s(.+)\\(.*\\)\\s.*").matcher(sql);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        // UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
        if (sql.startsWith("update")) {
            matcher = Pattern.compile("update\\s(.+)set\\s.*").matcher(sql);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        // DELETE FROM 表名称 WHERE 列名称 = 值
        if (sql.startsWith("delete")) {
            matcher = Pattern.compile("delete\\sfrom\\s(.+)where\\s(.*)").matcher(sql);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    public static String getTableNameByRawSql(String sql) {
        return getTableName(formatSqlNoParameter(sql)).trim();
    }
}
