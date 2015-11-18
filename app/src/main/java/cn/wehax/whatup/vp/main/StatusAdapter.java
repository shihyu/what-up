package cn.wehax.whatup.vp.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import cn.wehax.whatup.R;
import cn.wehax.whatup.config.Constant;
import cn.wehax.whatup.model.leancloud.LC;
import cn.wehax.whatup.support.helper.MoveToHelper;

/**
 * Created by mayuhan on 15/6/10.
 */
public class StatusAdapter extends BaseAdapter {

    public Activity context;
    private DisplayImageOptions options;
    private ArrayList<Map> statusList = new ArrayList<>();

    private Map itemObj;
    MainPresenter mPresenter;

    public StatusAdapter(Activity context, MainPresenter presenter) {
        this.context = context;
        this.mPresenter=presenter;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void sycNewData(ArrayList<Map> data) {
        statusList.clear();
        statusList.addAll(data);
        this.notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int position) {
        return statusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (statusList != null)
            itemObj = statusList.get(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
            }
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item_main, parent, false);
            viewHolder.statusThumb = (ImageView) convertView.findViewById(R.id.iv_status_main);
            viewHolder.statusAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar_main);
            viewHolder.distanceContent = (TextView) convertView.findViewById(R.id.tv_distance_main);
            viewHolder.avatarRl = (RelativeLayout) convertView.findViewById(R.id.rl_avatar_main);
            viewHolder.statusRl = (RelativeLayout) convertView.findViewById(R.id.rl_status_main);
            viewHolder.sexAvatar= (ImageView) convertView.findViewById(R.id.iv_avatar_sex);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (itemObj != null) {
            AVFile imageData = (AVFile) itemObj.get("imageData");
            final Map userInfo = (Map) itemObj.get("source");
            AVObject avObject = (AVObject) itemObj.get("statusReference");
            final String statusId = avObject.getObjectId();
            Integer userSex= 0;
            if (imageData != null) {
                ImageLoader.getInstance().displayImage(imageData.getThumbnailUrl(false, 80, 80), viewHolder.statusThumb,options);
            }
            if (userInfo != null) {
                AVFile avatarData = (AVFile) userInfo.get("avatar");
                userSex= (Integer) userInfo.get(LC.table.User.sex);
                if (avatarData != null) {
                    ImageLoader.getInstance().displayImage(avatarData.getThumbnailUrl(false, 55, 55), viewHolder.statusAvatar,options);
                }
            }
            if(userSex== Constant.SEX_MALE){
                viewHolder.sexAvatar.setImageResource(R.drawable.avatar_border_selector);
            }else {
                viewHolder.sexAvatar.setImageResource(R.drawable.avatar_female_border_selector);
            }
            viewHolder.distanceContent.setText("100m");
           final int selectPosition = position;
            viewHolder.avatarRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String objectId;
                    if(userInfo !=null) {
                        objectId = (String) userInfo.get("objectId");
                        if (objectId != null)
                            MoveToHelper.moveToOtherHomepage(context, objectId, statusId);
                    }

                }
            });
            viewHolder.statusRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onClickStatus(selectPosition);

                }
            });

        }

        return convertView;
    }

    public class ViewHolder {
        ImageView statusThumb;
        ImageView statusAvatar;
        ImageView sexAvatar;
        TextView distanceContent;
        RelativeLayout statusRl;
        RelativeLayout avatarRl;

    }
}
