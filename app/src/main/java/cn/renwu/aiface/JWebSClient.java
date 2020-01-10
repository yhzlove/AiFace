package cn.renwu.aiface;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import timber.log.Timber;

public class JWebSClient extends WebSocketClient {


    public JWebSClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Timber.e("连接打开onOpen");
    }

    @Override
    public void onMessage(String message) {
        Timber.e(message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Timber.e("关闭 断开连接onClose");
    }

    @Override
    public void onError(Exception ex) {
        Timber.e("错误 onError");
    }

}