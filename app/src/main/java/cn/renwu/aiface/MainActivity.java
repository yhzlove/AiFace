package cn.renwu.aiface;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class MainActivity extends AppCompatActivity {

    StandardGSYVideoPlayer videoPlayer;
    OrientationUtils orientationUtils;

    Context context;
    RecyclerView fileRecyclerView;
    Button addButton;
    Button reduceButton;
    Integer num = 1;
    private FaceInfoAdapter faceInfoAdapter;
    private List<Integer> faceList = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileRecyclerView = (RecyclerView)findViewById(R.id.face_list);
        addButton = (Button)findViewById(R.id.add_buttton);
        reduceButton = (Button)findViewById(R.id.reduce_buttton);
        context = this;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //里面写点击后想要实现的效果
                Toast.makeText(MainActivity.this, "Add", Toast.LENGTH_SHORT).show();
                //这里是弹出一个消息---"按钮被点击"
                add();
            }
        });

        reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //里面写点击后想要实现的效果
                Toast.makeText(MainActivity.this, "Reduce", Toast.LENGTH_SHORT).show();
                //这里是弹出一个消息---"按钮被点击"
                reduce();
            }
        });

        initVideoPlayer();
        initFace();

    }

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

    // video-player  END

    // 识别到的人脸信息展示
    private void initFace() {

        //设置RecyclerView管理器
//        fileRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        //初始化适配器
        faceInfoAdapter = new FaceInfoAdapter(context, faceList);

        //设置添加或删除item时的动画，这里使用默认动画
//        fileRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fileRecyclerView.setItemAnimator(new FadeInDownAnimator());

        fileRecyclerView.getItemAnimator().setAddDuration(100);
        fileRecyclerView.getItemAnimator().setRemoveDuration(10);
        fileRecyclerView.getItemAnimator().setMoveDuration(10);
        fileRecyclerView.getItemAnimator().setChangeDuration(10);

        //设置适配器
        fileRecyclerView.setAdapter(faceInfoAdapter);

        initSocketClient();
    }


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



    // test
    private void add(){
//        faceInfoAdapter.notifyDataSetChanged();
        Log.e("add", "add-start");
        faceList.add(num++);
        Log.e("add", ""+num);
        Log.e("add", ""+faceList.size());
        if(faceList.size() > 3){
            Log.e("add", "大于3个");
            faceList.remove(0);
            Log.e("add", "删除第一个");
            faceInfoAdapter.notifyItemRemoved(0);
            Log.e("add", "删除第一个完成");
        }
        Log.e("add", "添加完成");
        faceInfoAdapter.notifyItemInserted(2);
        Log.e("add", "add-end");
    }

    private void reduce(){
        if(faceList.size()>0) {
            faceList.remove(0);
            faceInfoAdapter.notifyItemRemoved(0);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("Handler", "handleMessage");
            add();
        }
    };


}
