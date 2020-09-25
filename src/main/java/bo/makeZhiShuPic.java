package bo;


import org.apache.log4j.Logger;
import util.DBUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 读取指数信息,生成图片
 */
public class makeZhiShuPic {
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static String update_time_format = simpleDateFormat.format(new Date());
    
    private static Logger logger = Logger.getLogger(makeZhiShuPic.class);
    public static void main(String[] args) throws SQLException {
//        makePic();
        logger.info("23423");
        logger.error("sdfsdf");

    }

    public static void makePic() throws SQLException {
            String[] title ={"指数代码","指数类型","指数名称","PE","PE百分比","PB","PB百分比","股息率","ROE","PEG"};
            ArrayList<String> dataList = new ArrayList<>();
            Connection connection_postgres = DBUtil.getConnection_mysql();
            Statement statement = connection_postgres.createStatement();
            String Sql = "SELECT * from index_details ";
            ResultSet rs = statement.executeQuery(Sql);
            while(rs.next()) {
                if(rs.getString("index_code").equals("000992")){//全指金融不更新
                    continue;
                }
                StringBuffer data = new StringBuffer();
                data.append(rs.getString("index_code")).append(",");
                String index_type = rs.getString("index_type");
                if(index_type.equals("1")){
                    data.append("宽基指数").append(",");
                }else if(index_type.equals("2")) {
                    data.append("策略指数").append(",");
                }else if(index_type.equals("3")) {
                    data.append("行业指数").append(",");
                }
                data.append(rs.getString("index_name")).append(",");
                data.append(rs.getDouble("now_pe")).append(",");
                data.append(rs.getDouble("now_pe_per")+"%").append(",");
                data.append(rs.getDouble("now_pb")).append(",");
                data.append(rs.getDouble("now_pb_per")+"%").append(",");
                data.append(rs.getDouble("dyr")+"%").append(",");
                data.append(rs.getDouble("roe")+"%").append(",");
                if(rs.getDouble("peg")==0.0){
                    data.append("- -");
                }else{
                    data.append(rs.getDouble("peg"));
                }
//                if(rs.getDate("update_time")!=null){
//                    Date update_time = rs.getDate("update_time");
//                    update_time_format= simpleDateFormat.format(update_time);
//                }
                dataList.add(data.toString());
            }
            myGraphicsGeneration(title,dataList,"/opt/pic/a.jpg");// /opt/pic/a.jpg C:/IDEAproject/a.jpg



    }

    public static void myGraphicsGeneration(String[] title ,ArrayList<String> dataList,String path){
        // 字体大小
        int fontTitileSize =15;
        // 横线的数量
        int totalrow = dataList.size()+2;
        // 竖线的数量
        int totalcol = 0;
        if (title[0]  != null){
            totalcol = title.length;;
        }
        // 图片宽度
        int imageWidth =900;
        // 行高
        int rowheight= 40;
        // 图片高度
        int imageHeight = totalrow*rowheight+50;
        // 起始高度
        int startHeight = 10;
        // 起始宽度
        int startWidth = 10;
        // 单元格宽度
        int colwidth = (int)((imageWidth-20)/totalcol);
        BufferedImage image = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0,imageWidth,imageHeight);
        graphics.setColor(new Color(220,240,240));//背景

        graphics.setColor(Color.black);
        graphics.drawString("更新时间:"+update_time_format,10,35);
        //画横线
        for(int j=0;j<totalrow;j++){
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth,startHeight+(j+1)*rowheight,startWidth+colwidth*totalcol,startHeight+(j+1)*rowheight);
        }
        //画竖线
        for(int k=0;k<totalcol+1;k++){
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth+k*colwidth,startHeight+rowheight,startWidth+k*colwidth,startHeight+rowheight*totalrow);
        }
        //设置字体
        Font font  = new Font("微软雅黑",Font.BOLD,fontTitileSize);
        graphics.setFont(font);
        //写标题
        for(int x=0;x<title.length;x++){
            graphics.drawString(title[x].toString(),startWidth+colwidth*x+5,startHeight+rowheight*(0+2)-10);
        }
        for (int y = 0; y < dataList.size() ; y++) {
            String data = dataList.get(y);
            String[] split = data.split(",");
            for (int z = 0; z < split.length; z++) {
                if (z == 0) {
                    font = new Font("微软雅黑", Font.PLAIN, fontTitileSize);
                    graphics.setFont(font);
                    graphics.setColor(Color.RED);
                } else {
                    font = new Font("微软雅黑", Font.PLAIN, fontTitileSize);
                    graphics.setFont(font);
                    double pe_per = Double.parseDouble(split[4].replace("%",""));
                    if(pe_per<30 && z ==4){
                        Color green  = new Color(30, 155, 67);
                        graphics.setColor(green);
                        graphics.fillRect(startWidth + colwidth * z+ 1,startHeight + rowheight * (y + 2)+1,colwidth-1,rowheight-1); // 画矩形着色块
                        graphics.setColor(Color.white);
                    }else if(pe_per>70 && z ==4){
                        Color red  = new Color(231, 116, 67);
                        graphics.setColor(red);
                        graphics.fillRect(startWidth + colwidth * z+ 1,startHeight + rowheight * (y + 2)+1,colwidth-1,rowheight-1); // 画矩形着色块
                        graphics.setColor(Color.white);
                    }else {
                        graphics.setColor(Color.BLACK);
                    }
                }
                graphics.drawString(split[z].toString(), startWidth + colwidth * z + 5, rowheight+startHeight + rowheight * (y + 2) - 10);
            }
        }
        createImage(image, path);
    }


    /**
      * 将图片保存到指定位置
      * @param image 缓冲文件类
      * @param fileLocation 文件位置
      */
    public static void createImage(BufferedImage image, String fileLocation){
        try {
            FileOutputStream fos = new FileOutputStream(fileLocation);
            ImageIO.write(image, "jpg", fos);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
