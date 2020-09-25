package rest;

import bo.makeZhiShuPic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class TestController {
    @RequestMapping(value = "/test")
    public  void index(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            makeZhiShuPic.makePic();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().print("error" +e);
        }
        resp.getWriter().print("ok" );
        return;
    }
}
