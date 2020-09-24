package voOut;

import util.MessageType;

/**
 * 回复视频消息
 *
 * @author Administrator
 *
 */
public class VideoOutPutMessage extends BaseOutMessage {
    private Video Video;

    public Video getVideo() {
        return Video;
    }

    public void setVideo(Video video) {
        Video = video;
    }

    @Override
    public String getMsgType() {
        return MessageType.RESP_MESSAGE_TYPE_VIDEO.toString();
    }
}