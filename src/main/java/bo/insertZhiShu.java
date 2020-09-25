package bo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;



public class insertZhiShu {
    //index_code  以sh为准
    //date
    //pe值  15.3122
    //pb值  2.3759
    //roe值 0.1202
    //index_name 基金名称
    //https://danjuanfunds.com/dj-valuation-table-detail/SH000016
    public static void main(String[] args) throws SQLException, InterruptedException, ParseException {
//        updateZhiShu();
    }


    public static void updateZhiShu() throws SQLException, InterruptedException, ParseException {
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        String sql = "select index_code ,update_time from index_details";// where changying ='1'
        ResultSet rs = statement.executeQuery(sql);

        while(rs.next()){
            String index_code = rs.getString("index_code");
            System.out.println(index_code);
            if(index_code.equals("000992")){//全指金融不更新
                continue;
            }
            query_insert(index_code);
            updateDetail(index_code);
            //更新update时间
            updateTime(index_code);//上次的更新时间
            Thread.sleep(3000);

        }
    }

    /**
     * 更新指数蛋卷pe pb 百分比等详细信息
     */
    public static void  updateDetail(String code ) throws  InterruptedException {
        String detail = "https://danjuanfunds.com/djapi/index_eva/detail/"+code;
        String detail_res = DBUtil.sendGet(detail);
        JSONObject jo = JSONObject.parseObject(detail_res);
        JSONObject data = jo.getJSONObject("data");
        Double pe = data.getDouble("pe");
        Double pe_percentile = data.getDouble("pe_percentile")*100;
        Double pb = data.getDouble("pb");
        Double pb_percentile = data.getDouble("pb_percentile")*100;
        Double roe = data.getDouble("roe")*100;
        Double peg = data.getDouble("peg");
        if(peg == null){
            peg=0.0;
        }
        Double dyr = data.getDouble("yeild")*100;
        String sql = "";
        try {
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
         sql = "UPDATE `index_details` SET now_pe='"+pe+"'" +
                " , now_pe_per='"+pe_percentile +"'" +
                " , now_pb='" +pb+"'" +
                " , now_pb_per='" +pb_percentile+"'" +
                " , roe='" +roe+"'" +
                " , peg='" + peg+"'" +
                " , dyr='" + dyr+"'" +
                " WHERE index_code ='"+code+"'";
//        System.out.println(sql);

            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("错误sql="+sql);
            e.printStackTrace();
        }
        return ;
    }


    /**
     * 更新update时间
     */
    public static void updateTime(String code)throws SQLException{
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String ymd = simpleDateFormat.format(new Date());
        String sql = "UPDATE `testdatabase`.`index_details` SET `update_time`='"+ymd+"' WHERE index_code ='"+code+"'";
        statement.execute(sql);
        System.out.println("  更新完成");

    }
    //danjuanfunds.com/djapi/index_eva/roe_history/SH000300?day=3y

    /**
     * 更新pe pb等信息
     */
    public static void query_insert(String code) throws SQLException {
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        String maxDateSql = "SELECT max(date) from index_history_value where index_code='"+code+"'";
        ResultSet rs = statement.executeQuery(maxDateSql);
        Date maxdate = new Date(337315111000l);
        while(rs.next()) {
            if(rs.getDate("max(date)")!=null){
                maxdate = rs.getDate("max(date)");
            }
        }
        long time = maxdate.getTime();

        HashMap<Long, Double> peMap = new HashMap<>();
        HashMap<Long, Double> pbMap = new HashMap<>();
        HashMap<Long, Double> roeMap = new HashMap<>();
        ArrayList<Long> tsList = new ArrayList<>();

        String roe_url = "https://danjuanfunds.com/djapi/index_eva/roe_history/"+code+"?day=all";
        String roe_res = DBUtil.sendGet(roe_url);
        JSONObject jo = JSONObject.parseObject(roe_res);
        JSONObject data = jo.getJSONObject("data");
        JSONArray index_eva_roe_growths = data.getJSONArray("index_eva_roe_growths");
        for (int i = 0; i <index_eva_roe_growths.size() ; i++) {
            Long ts = index_eva_roe_growths.getJSONObject(i).getLong("ts");
            if(ts<=time){
                continue;
            }
            Double roe = Double.parseDouble(index_eva_roe_growths.getJSONObject(i).getString("roe"));
            roeMap.put(ts,roe);
        }
        String pe_url = "https://danjuanfunds.com/djapi/index_eva/pe_history/"+code+"?day=all";
        String pe_res = DBUtil.sendGet(pe_url);
        JSONObject peobj = JSONObject.parseObject(pe_res);
        JSONObject pe_data = peobj.getJSONObject("data");
        JSONArray pe_index_eva_roe_growths = pe_data.getJSONArray("index_eva_pe_growths");
        for (int i = 0; i <pe_index_eva_roe_growths.size() ; i++) {
            Long ts = pe_index_eva_roe_growths.getJSONObject(i).getLong("ts");
            if(ts<=time){
                continue;
            }
            Double pe = Double.parseDouble(pe_index_eva_roe_growths.getJSONObject(i).getString("pe"));
            peMap.put(ts,pe);
            tsList.add(ts);

        }
        String pb_url = "https://danjuanfunds.com/djapi/index_eva/pb_history/"+code+"?day=all";
        String pb_res = DBUtil.sendGet(pb_url);
        JSONObject pbobj = JSONObject.parseObject(pb_res);
        JSONObject pb_data = pbobj.getJSONObject("data");
        JSONArray pb_index_eva_roe_growths = pb_data.getJSONArray("index_eva_pb_growths");
        for (int i = 0; i <pb_index_eva_roe_growths.size() ; i++) {
            Long ts = pb_index_eva_roe_growths.getJSONObject(i).getLong("ts");
            if(ts<=time){
                continue;
            }
            Double pb = Double.parseDouble(pb_index_eva_roe_growths.getJSONObject(i).getString("pb"));
            pbMap.put(ts,pb);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> sqlList = new ArrayList<>();
        for (int i = 0; i <tsList.size() ; i++) {
            Double peValue = peMap.get(tsList.get(i));
            Double pbValue = pbMap.get(tsList.get(i));
            Double roeValue = roeMap.get(tsList.get(i));


            Date date = new Date(tsList.get(i));
            String format = simpleDateFormat.format(date);
            String sql = "INSERT INTO index_history_value (index_code, date, pe, pb, roe ) VALUES ('"+code+"', '"+format+"', "+peValue+", "+pbValue+", "+roeValue+");";
            sqlList.add(sql);
        }

        if(sqlList.size()==0){
            System.out.print(code+"已是最新");
            return;
        }
        System.out.println(sqlList.get(0));


        String sqlerror = "";
        try {
            System.out.print(code+"共"+sqlList.size()+"条,开始插入");
            for (int j = 0; j <sqlList.size() ; j++) {
                sqlerror  = sqlList.get(j);
                statement.execute(sqlerror);
            }
        } catch (SQLException e) {
            System.out.println(sqlerror);
            e.printStackTrace();
        }



    }
}
