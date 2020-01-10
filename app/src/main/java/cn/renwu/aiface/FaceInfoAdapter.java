package cn.renwu.aiface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.List;



/**
 * FaceInfoAdapter
 * @author yanhaizhen
 */
public class FaceInfoAdapter extends RecyclerView.Adapter<FaceInfoAdapter.FaceInfoVH> {

    private Context context;
    private List<Integer> fileDatas;

    public FaceInfoAdapter(Context context, List<Integer> list) {
        this.fileDatas = list;
        this.context = context;

    }

    @Override
    public FaceInfoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_face_info, parent, false);
        FaceInfoVH viewHolder = new FaceInfoAdapter.FaceInfoVH(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final FaceInfoVH holder, final int position) {

        Integer num = fileDatas.get(position);
        holder.stuName.setText("学生"+num);


    }

    @Override
    public int getItemCount() {
        return null != fileDatas ? fileDatas.size() : 0;
    }


    class FaceInfoVH extends RecyclerView.ViewHolder {

        ImageView faceImage;
        TextView stuName;

        FaceInfoVH(View itemView) {
            super(itemView);

            stuName = (TextView)itemView.findViewById(R.id.stu_name);
            faceImage = (ImageView) itemView.findViewById(R.id.face_image);
        }
    }


    /**
     * 和Activity通信的接口
     */
    public interface onSwipeListener {
        void onClickItem(View v, int pos);
    }

    private FaceInfoAdapter.onSwipeListener mOnSwipeListener;

    public FaceInfoAdapter.onSwipeListener getmOnSwipeListener() {
        return mOnSwipeListener;
    }

    public void setmOnSwipeListener(FaceInfoAdapter.onSwipeListener mOnSwipeListener) {
        this.mOnSwipeListener = mOnSwipeListener;
    }


}

