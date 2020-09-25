package bo;

import util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LoopBackTest {
    /**
     * 回测测试
     *   30%   11.0464
     *   70%   13.22642
     */
    public static void main(String[] args) throws ParseException, SQLException {
//        String index_code = "SH000300";
//        String code = "100038";
//        String start = "2011-01-01";
//        String end = "2020-09-01";
//        int low_line_per =30;
//        int high_line_per =70;
//        Double buy_rate = 0.012;//购买费率
//        Double sale_rate = 0.05;//赎回费率
//        LoopBackTest(index_code,code,start,end,low_line_per,high_line_per,buy_rate,sale_rate);

        String code = "001180";
        Double buy_rate = 0.012;//购买费率
        Double sale_rate = 0.05;//赎回费率
        
        ArrayList<String> buy_days = new ArrayList<>();
        buy_days.add("2016-12-15");
        buy_days.add("2017-01-16");
        buy_days.add("2017-03-31");
        buy_days.add("2017-04-25");
        buy_days.add("2017-04-28");
        buy_days.add("2017-05-23");
        buy_days.add("2017-07-31");
        buy_days.add("2017-03-31");
        buy_days.add("2018-02-06");
        buy_days.add("2018-08-06");
        buy_days.add("2018-10-15");
        ArrayList<String> sale_days = new ArrayList<>();
        sale_days.add("2020-03-25");
        sale_days.add("2020-04-30");
        sale_days.add("2020-07-10");
        sale_days.add("2020-07-16");  
        LoopBackOneJiJin(code,buy_days,sale_days,buy_rate,sale_rate);
    }
    //模拟指定基金
    public static void LoopBackOneJiJin(String code, List<String> buy_days ,List<String> sale_days,Double buy_rate,Double sale_rate)throws ParseException, SQLException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> querydays = new ArrayList<>();
        querydays.addAll(buy_days);
        querydays.addAll(sale_days);
        Collections.sort(querydays);
        String start_day = querydays.get(0);
        String end_day = querydays.get(querydays.size()-1);
//        Date startDate = simpleDateFormat.parse(start_day);
//        Date endDate = simpleDateFormat.parse(end_day);
//        Calendar startCal = Calendar.getInstance();
//        startCal.setTime(startDate);
//        Calendar endCal = Calendar.getInstance();
//        endCal.setTime(endDate);
        String code_sql = "select date,market_value from code_history_value where code ='"+code+"'  and date >= '"+start_day+"'  and date <= '"+end_day+"'  ORDER BY date ;";
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        ResultSet code_rs = statement.executeQuery(code_sql);
        HashMap<String, Double> buy_day_market_value = new HashMap<>();
        HashMap<String, Double> sale_day_market_value = new HashMap<>();
        while(code_rs.next()){
            java.sql.Date date = code_rs.getDate("date");
            String format = simpleDateFormat.format(date);
            if(buy_days.contains(format)){
                double market_value = code_rs.getDouble("market_value");
                buy_day_market_value.put(format,market_value);//购买日,净值
            }
            if(sale_days.contains(format)){
                double market_value = code_rs.getDouble("market_value");
                sale_day_market_value.put(format,market_value);//卖出日,净值
            }
        }
        Double fund_shares = 0.0;//当前份额
        Double inmoney = 0.0;//投入金额
        Double outmoney = 0.0;//此轮取出金额
        int count = 0;
        for (int i = 0; i <querydays.size() ; i++) {
            if(buy_day_market_value.containsKey(querydays.get(i))){
                Double value = buy_day_market_value.get(querydays.get(i));//当天净值
                Double jingshengou = 1000/(1+buy_rate);
                Double shengoufene = jingshengou/ value;
                fund_shares = fund_shares + shengoufene;
                count++;
                System.out.println(querydays.get(i));//+"购买1份,消耗金额1000"
                inmoney +=1000;
            }
            if(sale_day_market_value.containsKey(querydays.get(i))){
                Double value = sale_day_market_value.get(querydays.get(i));//当天净值
                Double shuhuifene = fund_shares/count;//只卖出1份
                Double shuhuizonge =  shuhuifene * value ;
                Double shouxufei =  shuhuizonge * sale_rate ;
                outmoney = shuhuizonge -  shouxufei ;
                count-- ;
                System.out.println(querydays.get(i)+"卖出,得到"+outmoney+"元");
            }
        }
        Double value = sale_day_market_value.get(sale_days.get(sale_days.size()-1));//最后一次卖出的净值
        Double shuhuizonge =  fund_shares * value ;
        Double shouxufei =  shuhuizonge * sale_rate ;
        outmoney = shuhuizonge -  shouxufei ;
        System.out.println("剩余份额"+fund_shares+"价值"+outmoney);
        System.out.println("总消耗金额"+inmoney);
    }






    /**
     *  按分位值定投计算收益
     */
    public static void  LoopBackTest(String index_code,String code,String start,String end,int low_line_per,int high_line_per,Double buy_rate,Double sale_rate)throws ParseException, SQLException {
        Double low_line = checkJiJin.fenwei25(index_code, low_line_per);//这里的分位值是全表的
        Double high_line = checkJiJin.fenwei25(index_code, high_line_per);//这里的分位值是全表的
        System.out.println("开始计算"+start+"至"+end+"日的100038定投回测,pe参照指数"+index_code+"低分位为"+low_line_per+"%,高分位为"+high_line_per+"%");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleDateFormat.parse(start);
        Date endDate = simpleDateFormat.parse(end);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        ArrayList<String> mondayList = new ArrayList<>();//周一的日期合集
        while (true){
            if(startCal.get(Calendar.DAY_OF_WEEK)==2){
                String format = simpleDateFormat.format(startCal.getTime());
                mondayList.add(format);
//                System.out.println(format);
            }
            if(startCal.after(endCal)){
                break;
            }
            startCal.add(Calendar.DAY_OF_MONTH,1);
        }
        //获取指定日期之间的指数pe,来确定该日期是否该买入或卖出
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        String sql = "select date,pe from index_history_value where index_code ='"+index_code+"'  and date >= '"+start+"'  and date <= '"+end+"'  ORDER BY date  ;";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<String> buy_days = new ArrayList<>();
        ArrayList<String> sale_days = new ArrayList<>();
        while(rs.next()){
            Double pe = rs.getDouble("pe");
            if(pe<low_line){
                java.sql.Date date = rs.getDate("date");
                String format = simpleDateFormat.format(date);
                buy_days.add(format);
            }
            if(pe>high_line){
                java.sql.Date date = rs.getDate("date");
                String format = simpleDateFormat.format(date);
                sale_days.add(format);
            }
        }

        String code_sql = "select date,market_value from code_history_value where code ='"+code+"'  and date >= '"+start+"'  and date <= '"+end+"'  ORDER BY date ;";
        ResultSet code_rs = statement.executeQuery(code_sql);
        HashMap<String, Double> buy_day_market_value = new HashMap<>();
        HashMap<String, Double> sale_day_market_value = new HashMap<>();
        while(code_rs.next()){
            java.sql.Date date = code_rs.getDate("date");
            String format = simpleDateFormat.format(date);
            if(buy_days.contains(format)){
                double market_value = code_rs.getDouble("market_value");
                buy_day_market_value.put(format,market_value);//购买日,净值
            }
            if(sale_days.contains(format)){
                double market_value = code_rs.getDouble("market_value");
                sale_day_market_value.put(format,market_value);//卖出日,净值
            }
        }
        //开始遍历所有的周一
        Double fund_shares = 0.0;//当前份额
        Double inmoney = 0.0;//投入金额
        Double outmoney = 0.0;//此轮取出金额
        int count  = 0 ;//投入期数
        for (int i = 0; i <mondayList.size() ; i++) {
            if(buy_day_market_value.containsKey(mondayList.get(i))){
                Double value = buy_day_market_value.get(mondayList.get(i));//当天净值
                System.out.println(mondayList.get(i)+"购买");
                Double jingshengou = 1000/(1+buy_rate);
                Double shengoufene = jingshengou/ value;
                fund_shares = fund_shares + shengoufene;
                inmoney += 1000;
                count += 1;
            }
            if(sale_day_market_value.containsKey(mondayList.get(i)) && fund_shares != 0.0){
                Double value = sale_day_market_value.get(mondayList.get(i));//当天净值
                System.out.println(mondayList.get(i)+"卖出");
                Double shuhuiefu =  fund_shares * value ;
                Double shouxufei =  shuhuiefu * sale_rate ;
                outmoney = shuhuiefu -  shouxufei ;
                System.out.println("此轮投入"+count+"期,投入"+inmoney+"元,得到"+outmoney+"元,收益"+(outmoney-inmoney)+"元");
                fund_shares = 0.0;
                inmoney = 0.0;
                outmoney = 0.0;
                count  = 0 ;
            }
        }
    }
}
