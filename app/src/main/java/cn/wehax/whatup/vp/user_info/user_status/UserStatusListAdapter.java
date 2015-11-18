package cn.wehax.whatup.vp.user_info.user_status;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

import cn.wehax.util.TimeUtils;
import cn.wehax.whatup.R;
import cn.wehax.whatup.model.leancloud.LC;

public class UserStatusListAdapter extends BaseAdapter {
    Context context;
    List<Map> data;

    final String TIME_PATTERN = "yyyy-MM-dd  HH:mm";
    final int statusImageWidth;
    final int statusImageHeight;

    final int itemWidth;
    final int itemHeight;

    private DisplayImageOptions options;

    public UserStatusListAdapter(Context context, List<Map> data) {
        this.context = context;
        this.data = data;

        statusImageWidth = context.getResources().getDimensionPixelSize(R.dimen.status_image_view_width);
        statusImageHeight = context.getResources().getDimensionPixelSize(R.dimen.status_image_view_height);

        itemWidth = context.getResources().getDimensionPixelSize(R.dimen.user_status_list_item_width);
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.user_status_list_item_height);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.imageloader_loading)
                .showImageOnFail(R.drawable.imageloader_load_fail)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map getItem(int position) {
        // PullToRefreshListView第一项的position = 1
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_user_status_list, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Map item = getItem(position);

        holder.createdTimeTv.setText(TimeUtils.getFormatDate((Long)item.get(LC.method.GetUserStatusList.keyCTime), TIME_PATTERN));

        AVFile imgFile = (AVFile) item.get(LC.method.GetUserStatusList.keyImageData);
        ImageLoader.getInstance().displayImage(
                imgFile.getThumbnailUrl(false, statusImageWidth, statusImageHeight),
                holder.userStatusIv,
                options);

        return convertView;
    }

    class Holder {
        TextView createdTimeTv;
        ImageView userStatusIv;

        public Holder(View view) {
            createdTimeTv = (TextView) view.findViewById(R.id.user_status_created_time_tv);
            userStatusIv = (ImageView) view.findViewById(R.id.user_status_iv);
        }
    }
}
