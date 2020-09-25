package bo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.ParseException;

@Component("taskJob")
public class TaskJob {
//    static {
//        try {
//            makeZhiShuPic.makePic();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) throws InterruptedException {
//        task2();
    }
    @Scheduled(cron = "0 0 18 * * ?") // 每晚18点开始自动更新更新指数信息
    public static void task1() throws InterruptedException, SQLException, ParseException {
        insertZhiShu.updateZhiShu();
    }


    @Scheduled(cron = "0 15 18 * * ?") // 每晚18:15点开始自动更新更新指数信息
//    @Scheduled(cron = "0/10 * * * * ?") // 每晚18:15点开始自动更新更新指数信息
    public static void task2() throws InterruptedException, SQLException {
        makeZhiShuPic.makePic();
    }


}
