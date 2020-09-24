package rest;

import bo.WebChatService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import util.MessageType;
import util.WeixinCheckoutUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping(value = "/wxservlet")
public class WeixinController {
    /**
     * token校验,只用1次
     * @param req
     * @param resp
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET)
    public  void get(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String signature = req.getParameter("signature");
        // 时间戳
        String timestamp = req.getParameter("timestamp");
        // 随机数
        String nonce = req.getParameter("nonce");
        // 随机字符串
        String echostr = req.getParameter("echostr");
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        System.out.println(WeixinCheckoutUtil.checkSignature(signature, timestamp, nonce));
        if (signature != null && WeixinCheckoutUtil.checkSignature(signature, timestamp, nonce)) {
            System.out.println(echostr);
            resp.getWriter().print(echostr);
        }
        return ;
    }

    /**
     * 处理微信服务器发来的post消息
     */
    @RequestMapping(method = RequestMethod.POST)
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //用户每次向公众号发送消息、或者产生自定义菜单点击事件时，响应URL将得到推送
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/xml");
            //调用parseXml方法解析请求消息
            Map<String, String> map = MessageType.parseXml(request, response);
            String MsgType = map.get("MsgType");
            String xml = null;//处理输入消息，返回结果的xml
            if(MessageType.REQ_MESSAGE_TYPE_EVENT.equals(MsgType)){
                xml = WebChatService.parseEvent(map);
            }else{
                xml = WebChatService.parseMessage(map);
            }
            //返回封装的xml
            //System.out.println(xml);
            response.getWriter().write(xml);
        } catch (Exception ex) {
            response.getWriter().write("");
        }

    }


}
