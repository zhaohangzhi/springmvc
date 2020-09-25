package bo;

import util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class checkJiJin {
    public static void main(String[] args) throws SQLException {
//        fenwei25("SH000300",30);
        checkPer("SH000016");
    }


    /**
     * 计算最后pe占当期的百分比位置
     */
    public static  Double checkPer(String code)throws SQLException {
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        String sql = "select pe from index_history_value where index_code ='"+code+"' ORDER BY `date`  ";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Double> doubles = new ArrayList<>();
        Double last = 0.0;
        while(rs.next()){
            Double value = rs.getDouble("pe");
            doubles.add(value);
            last = value;
        }
        System.out.println(last);
        Collections.sort(doubles);
        Double danwei = doubles.size()/ 100.0;//分为100份  xx%
        ArrayList<Double> pe_per = new ArrayList<>();
        for (int i = 0; i <=100 ; i++) {
            if(i==0){
                pe_per.add(doubles.get(0));
                continue;
            }
            if(i==100){
                pe_per.add(doubles.get(doubles.size()-1));
                continue;
            }

            Double  fenwei = i* danwei;//第i份占多少份额
            Double zheng = Math.floor(fenwei);//取整数部分.
            Double  xiaoshu = fenwei - zheng;//取小数
            Double fenweishu = doubles.get(zheng.intValue()) +  ((doubles.get(zheng.intValue()+1)-doubles.get(zheng.intValue()))*xiaoshu);
            pe_per.add(fenweishu);
        }
        for (int i = 0; i <pe_per.size() ; i++) {
//            System.out.println(pe_per.get(i));
            if(last>pe_per.get(i)&&last<pe_per.get(i+1)){
                System.out.println((i+i+1)/2+"%");
                return i+0.0;
            }
        }

        return 0.0;

    }


    /**
     * 计算某百分之xx的分位
     */
    public  static Double fenwei25(String code,int per) throws SQLException {
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        String sql = "select pe from index_history_value where index_code ='"+code+"'";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Double> doubles = new ArrayList<>();
        while(rs.next()){
            Double value = rs.getDouble("pe");
            doubles.add(value);
        }
        Collections.sort(doubles);
//        System.out.println(doubles.size());
        Double fenweishu = 0.0;
        Double danwei = doubles.size()/ 100.0;//243.2
        Double  fenwei = per* danwei;
        Double zheng = Math.floor(fenwei);//取整数部分.
        Double  xiaoshu = fenwei - zheng;
        fenweishu = doubles.get(zheng.intValue()) +  ((doubles.get(zheng.intValue()+1)-doubles.get(zheng.intValue()))*xiaoshu);
//        System.out.println(fenweishu);
        return  fenweishu;
    }

    /**
     * 计算历史中位数
     */
    public  static void zhongwei(String code) throws SQLException {
        Connection connection_postgres = DBUtil.getConnection_mysql();
        Statement statement = connection_postgres.createStatement();
        String sql = "select pe from index_history_value where index_code ='"+code+"'";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Double> doubles = new ArrayList<>();
        while(rs.next()){
            Double value = rs.getDouble("pe");
            doubles.add(value);
        }
        Collections.sort(doubles);
//        System.out.println(doubles.size());
        Double zhong = 0.0;
        if(doubles.size()%2==1){
            //奇数
            int i = (doubles.size()+ 1)/2;
            zhong= doubles.get(i);
        }else {
            //偶数
            zhong = (doubles.get(doubles.size()/2-1) + doubles.get(doubles.size()/2) + 0.0)/2;
        }
        System.out.println(zhong);
    }
}
