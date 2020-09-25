package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;
public class DBUtil {
    private static Logger log = Logger.getLogger(DBUtil.class.getClass());

    private static String driver = "org.postgresql.Driver";//驱动
    private static String postgre_database = "";
    private static String postgre_ip = "";
    private static String postgre_user = "";
    private static String postgre_passwd = "";

    private static String oracle_ip = "";
    private static String oracle_user = "";
    private static String oracle_passwd = "";
    private static String oracle_serviceName = "";
    private static String mysql_ip ="";
    private static String mysql_user ="";
    private static String mysql_passwd ="";
    private static String mysql_database ="";
    static {
        try {
            mysql_ip = "127.0.0.1";
            mysql_user = "root";
            mysql_passwd = "123456";
            mysql_database = "testdatabase";
        } catch (Exception e) {
            System.out.println("File 'DB/opt/connection.properties' read errors!");
            System.exit(0);
        }
    }

    public static Connection getConnection_postgres() {
        String url_postgre = "jdbc:postgresql://"+ postgre_ip +":5432/" + postgre_database;
        Connection con = null;
        try {
            con = DriverManager.getConnection(url_postgre, postgre_user, postgre_passwd);
        } catch (SQLException e) {
            e.printStackTrace();
        };
        return con;
    }
    public static Connection getConnection_oracle() {
        String url_oracle = "jdbc:oracle:thin:@"+oracle_ip+":1521/"+oracle_serviceName;
        Connection con = null;
        try {
            con = DriverManager.getConnection(url_oracle, oracle_user, oracle_passwd);
        } catch (SQLException e) {
            e.printStackTrace();
        };
        return con;
    }
    public static Connection getConnection_mysql() {
        log.info("开始连接mysql");
        String url_mysql = "jdbc:mysql://" + mysql_ip + ":3306/" + mysql_database + "?serverTimezone=Asia/Shanghai&tinyInt1isBit=false";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url_mysql, mysql_user, mysql_passwd);
        } catch (SQLException e) {
            log.error("e",e);
            e.printStackTrace();
        };
        return con;
    }

    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url ;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
////                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


}
