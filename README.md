# AiFace
RTSP播放摄像头 websocket推送后台数据  在大屏上展示


## 播放器
```xml
    //完整版引入
//    implementation 'com.shuyu:GSYVideoPlayer:7.1.2'

    implementation 'com.shuyu:gsyVideoPlayer-java:7.1.2'

    //是否需要ExoPlayer模式
    implementation 'com.shuyu:GSYVideoPlayer-exo2:7.1.2'

    //更多ijk的编码支持
    implementation 'com.shuyu:gsyVideoPlayer-ex_so:7.1.2'
```

代码：
```java
// video-player  Start
    private void initVideoPlayer() {
        videoPlayer =  (StandardGSYVideoPlayer)findViewById(R.id.video_player);
        // https://blog.csdn.net/mei_jia12/article/details/84573234
        /**此中内容：优化加载速度，降低延迟*/
        VideoOptionModel videoOptionModel;
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video"); //根据媒体类型来配置
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 20000);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1316);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240);
        list.add(videoOptionModel);
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        list.add(videoOptionModel);
        //  关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
        videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        list.add(videoOptionModel);
        GSYVideoManager.instance().setOptionModelList(list);
        String source1 = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";//"rtsp://admin:123456@192.168.2.135:554/video1";
        videoPlayer.setUp(source1, true, "");
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.startPlayLogic();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }
```

## websocket
```xml
 //Java-WebSocket
 implementation "org.java-websocket:Java-WebSocket:1.4.0"
```

代码 client：
```java
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
```
```java
JWebSClient client;
    //websocket  start
    //打开socket连接
    private void initSocketClient() {
        URI uri = URI.create("ws://192.168.2.178:8887");
        client = new JWebSClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("onMessage", message);

                if("1".equals(message)){
                    Message msg = new Message();
                    handler.sendMessage(msg);
                }
                if("2".equals(message)){
                    Message msg = new Message();
                    handler.sendMessage(msg);
                }

            }

        };
        connect();



    }

    //连接
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    client.connectBlocking();
                    Log.e("connectBlocking", "连接成功");
                    Log.e("connectBlocking", "" + client.isOpen());
                    if(client.isOpen()){
                        sendMsg("你好");
                        Log.e("connectBlocking", "isOpen");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    //断开连接
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Socket", "断开连接异常");
        } finally {
            client = null;
        }
    }

    //发送消息
    private void sendMsg(String msg) {
        if (null != client) {
            client.send(msg);
            Log.e("发送的消息", msg);
        }
    }

```