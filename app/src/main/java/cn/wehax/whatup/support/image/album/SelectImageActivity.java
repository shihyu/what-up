package cn.wehax.whatup.support.image.album;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.avos.avoscloud.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.wehax.whatup.support.image.Config;

/**
 * Created by howe on 15/3/27.
 */
public class SelectImageActivity extends Activity implements AdapterView.OnItemClickListener {

    GridView albumImgGrid;

    TextView titleText;

    TextView selectText;
    TextView selectNum;

    String albumName;

    boolean isPreview;

    int maxCount = 1;

    SelectImageAdapter adapter;
    List<AlbumImageBean> images = new ArrayList<AlbumImageBean>();
    List<AlbumImageBean> selectImages;

    private Cursor mImageCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumName = getIntent().getStringExtra("name");
        maxCount = getIntent().getIntExtra(Config.INTENT_VALUE_MAXCOUNT, 1);
        isPreview= getIntent().getBooleanExtra(Config.INTENT_VALUE_IS_PREVIEW,true);
        if (TextUtils.isEmpty(albumName)) {
            this.finish();
        }
        if (maxCount > 1) {
            selectImages = new ArrayList<AlbumImageBean>();
        }

        LinearLayout root = (LinearLayout) View.inflate(this,
                getResources().getIdentifier("activity_album_select_img", "layout", getPackageName()),
                null);
        setContentView(root);
        titleText = (TextView) root.findViewById(
                getResources().getIdentifier("album_select_img_title", "id", getPackageName()));
        titleText.setText(albumName);

        selectText = (TextView) root.findViewById(getResources().getIdentifier("album_select_img_select_text", "id", getPackageName()));
        selectNum = (TextView) root.findViewById(getResources().getIdentifier("album_select_img_select_num", "id", getPackageName()));

        albumImgGrid = (GridView) root.getChildAt(1);

        adapter = new SelectImageAdapter(images);

        albumImgGrid.setOnItemClickListener(this);
        albumImgGrid.setAdapter(adapter);
        loadData();
    }

    public void backClick(View view) {
        this.finish();
    }

    public void finishClick(View view) {
        StringBuffer sb = new StringBuffer("[");
        int size = selectImages.size();
        for (int i = 0; i < size; i++) {
            AlbumImageBean bean = images.get(i);

            sb.append("\"file://" + bean.getUrl() + "\"");
            if (i < size - 1) {
                sb.append(",");
            }
        }

        sb.append("]");

        setResult(RESULT_OK, this.getIntent().putExtra(Config.INTENT_VALUE_URI_LIST, sb.toString()));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            setResult(RESULT_OK, this.getIntent().setData(data.getData()));
            this.finish();
        }

    }

    private void loadData() {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = null;
            String bucket = albumName;
            searchParams = "bucket_display_name = \"" + bucket + "\"";
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            mImageCursor = this.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            columns,
                            searchParams,
                            null,
                            orderBy + " DESC");
            if (mImageCursor.getCount() > 0) {
                for (int i = 0; i < mImageCursor.getCount(); i++) {
                    mImageCursor.moveToPosition(i);

                    AlbumImageBean bean = new AlbumImageBean(
                            mImageCursor
                                    .getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                            0);

                    images.add(bean);
                }
                adapter.notifyDataSetChanged();
            } else {
                //                mView.showErrorMessage(R.string.tips_no_image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mImageCursor != null) {
                mImageCursor.close();
                mImageCursor = null;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0 || i >= images.size()) {
            return;
        }
        AlbumImageBean bean = images.get(i);
        if (maxCount > 1) {
            selectImage(bean, !bean.isChecked());
            adapter.notifyDataSetChanged();
        } else {

            if(isPreview){
                Intent intent = new Intent(this, ImagePreviewActivity.class);
                intent.setData(Uri.parse("file://" + bean.getUrl()));
                startActivityForResult(intent, Config.REQUEST_CODE_CALL_PREVIEW);
            }else {
                Intent intent = new Intent(this, ImagePreviewActivity.class);
                intent.setData(Uri.parse("file://" + bean.getUrl()));
                setResult(RESULT_OK,intent);
                this.finish();
            }

        }
    }

    boolean selectImage(AlbumImageBean bean, boolean isChecked) {
        if (isChecked && selectImages.size() >= maxCount) {
            Toast.makeText(this, "最多只能选择" + maxCount + "张图片", Toast.LENGTH_SHORT).show();
            return false;
        }
        bean.setChecked(isChecked);
        if (bean.isChecked() && !selectImages.contains(bean)) {
            selectImages.add(bean);
        } else if (!bean.isChecked() && selectImages.contains(bean)) {
            selectImages.remove(bean);
        }
        updateSelectText();
        return true;
    }

    void updateSelectText() {
        int count = selectImages.size();
        if (count == 0) {
            selectText.setVisibility(View.GONE);
            selectNum.setVisibility(View.GONE);
        } else {
            selectText.setVisibility(View.VISIBLE);
            selectNum.setVisibility(View.VISIBLE);
        }
        selectNum.setText("" + count);
    }


    private class SelectImageAdapter extends BaseAdapter {

        List<AlbumImageBean> images;

        public SelectImageAdapter(List<AlbumImageBean> list) {
            images = list;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public AlbumImageBean getItem(int i) {
            return images.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final AlbumImageBean data = getItem(i);
            final Holder holder;
            if (view == null) {
                view = View.inflate(SelectImageActivity.this,
                        getResources().getIdentifier("item_album_img_grid", "layout", getPackageName()),
                        null);
                holder = new Holder(view);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            FrameLayout.LayoutParams lp =
                    (FrameLayout.LayoutParams) holder.img.getLayoutParams();

            lp.width = lp.height = viewGroup.getWidth() / 4;
            holder.img.setLayoutParams(lp);

            lp = (FrameLayout.LayoutParams) holder.bg.getLayoutParams();

            lp.width = lp.height = viewGroup.getWidth() / 4;
            holder.bg.setLayoutParams(lp);

            if (data.getTime() != 0) {
                String time = formatTime(data.getTime() / 1000);
                holder.size.setText(time);
            } else {
                holder.size.setVisibility(View.GONE);
            }
            if (maxCount == 1) {
                holder.checkBox.setVisibility(View.GONE);
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
                if (data.isChecked()) {
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                }
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!selectImage(data, holder.checkBox.isChecked())) {
                            holder.checkBox.setChecked(false);
                        }
                    }
                });

            }

            ImageLoadAsync loadAsync =
                    new ImageLoadAsync(SelectImageActivity.this, holder.img, 200);
            loadAsync.executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, data.getUrl());

            return view;
        }

        public String formatTime(long seconds) {

            long hh = seconds / 60 / 60;
            long mm = (seconds - hh * 60 * 60) > 0 ? (seconds - hh
                    * 60 * 60)
                    / 60
                    : 0;
            long ss = seconds < 60 ? seconds : seconds % 60;

            return (hh == 0 ? "" : (hh < 10 ? "0" + hh : hh) + "小时")
                    + (mm == 0 ? "" : (mm < 10 ? "0" + mm : mm) + "分")
                    + (ss == 0 ? "" : (ss < 10 ? "0" + ss : ss) + "秒");
        }

        private class Holder {
            ImageView img;
            ImageView bg;
            TextView size;
            CheckBox checkBox;

            public Holder(View view) {
                img = (ImageView) view.findViewById(
                        getResources().getIdentifier("item_album_select_img", "id", getPackageName()));

                bg = (ImageView) view.findViewById(
                        getResources().getIdentifier("item_album_select_bg", "id", getPackageName()));

                size = (TextView) view.findViewById(
                        getResources().getIdentifier("item_album_select_size", "id", getPackageName()));

                checkBox = (CheckBox) view.findViewById(
                        getResources()
                                .getIdentifier("item_album_select_checkbox", "id", getPackageName()));
            }
        }
    }
}
