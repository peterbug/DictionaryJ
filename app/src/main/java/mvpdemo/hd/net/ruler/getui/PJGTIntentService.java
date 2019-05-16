package mvpdemo.hd.net.ruler.getui;

import android.content.Context;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import mvpdemo.hd.net.utils.PJLog;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class PJGTIntentService extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        PJLog.e("onReceiveServicePid:"+pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        // 透传消息的处理，详看SDK demo
        PJLog.e("onReceiveMessageData:" + msg);
        String taskId = msg.getTaskId();
        String messageId = msg.getMessageId();
        byte[] payload = msg.getPayload();
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskId, messageId, 90001);
        PJLog.e("第三方回执接口调用" + (result ? "成功" : "失败"));
        if (payload != null) {
            String data = new String(payload);
//            sendNotification(context, data);
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        PJLog.e("onReceiveClientId:"+clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        PJLog.e("onReceiveOnlineState:"+online);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        PJLog.e("onReceiveCommandResult:"+cmdMessage);

    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        PJLog.e("onNotificationMessageArrived: "+msg);
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        PJLog.e("onNotificationMessageClicked:"+msg);
    }
}
