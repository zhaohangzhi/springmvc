package bo;

import com.alibaba.fastjson.JSONObject;
import util.DBUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class insertJiJin {

//http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=100038&sdate=2001-12-18&edate=2020-09-07&per=49&page=6
    public static void main(String[] args) throws SQLException {
        String code = "001180";
        int maxPage = getMaxPage(code);
        query_insert(code,maxPage);
    }

    public static int getMaxPage(String code){
        String url = "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code="+code+"&sdate=2001-12-18&edate=2020-09-07&per=49&page=1";
        String s = sendGet(url);
        String replace = s.replace("var apidata=", "").replace(";","");
        JSONObject jo = JSONObject.parseObject(replace);
        int totlepage = Integer.parseInt(jo.get("pages").toString());
        System.out.println("共"+totlepage+"页");
        return totlepage;
    }

    public static void query_insert(String code ,int page) throws SQLException {
        for (int i = 1; i <=page ; i++) {
            System.out.println("开始查询第"+i+"页,共"+page+"页");
            String url = "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code="+code+"&sdate=2001-12-18&edate=2020-09-07&per=49&page="+i;
//            System.out.println(url);
            String s = sendGet(url);
            String replace = s.replace("var apidata=", "").replace(";","");
            JSONObject jo = JSONObject.parseObject(replace);
            String content = jo.get("content").toString().replace("<table class='w782 comm lsjz'><thead><tr><th class='first'>净值日期</th><th>单位净值</th><th>累计净值</th><th>日增长率</th><th>申购状态</th><th>赎回状态</th><th class='tor last'>分红送配</th></tr></thead>","")
                    .replace("<tbody>","")
                    .replace("</tbody></table>","")
                    .replace(" class='tor bold'","")
                    .replace(" class='tor bold red'","")
                    .replace(" class='tor bold bck'","")
                    .replace(" class='red unbold'","")
                    .replace(" class='tor bold grn'","")
                    .replace("<tr>","")
                    .replace("<td></td>","")
                    ;
            String[] split = content.split("</tr>");
            ArrayList<String> sqlList = new ArrayList<>();
            for (int k = 0; k <split.length ; k++) {
                String[] day = split[k].replace("<td>", "").replace("%","").split("</td>");
                if(day[3].equals("开放申购")||day[3].equals("封闭期")){
                    day[3]="0.00";
                }
                String sql = "INSERT INTO code_history_value (code, date, market_value, total_value, daily_growth_rate, code_name) VALUES ('"+code+"', '"+day[0]+"', '"+day[1]+"', '"+day[2]+"', '"+day[3]+"', '');";
                sqlList.add(sql);
//                System.out.println(sql);
            }
            Connection connection_postgres = DBUtil.getConnection_mysql();
            Statement statement = connection_postgres.createStatement();
            System.out.println(sqlList.size());
            for (int j = 0; j <sqlList.size() ; j++) {
                statement.execute(sqlList.get(j));
            }
        }
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
