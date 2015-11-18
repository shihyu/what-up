package cn.wehax.whatup.support.image.album;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import cn.wehax.whatup.support.image.Config;
import cn.wehax.whatup.support.image.crop.CropActivity;

/**
 * Created by howe on 15/3/27.
 */
public class AlbumActivity extends Activity implements AdapterView.OnItemClickListener{

    private static final String[] PROJECTION_BUCKET = {
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Images.ImageColumns.DATA};

    private final int INDEX_BUCKET_ID     = 0;
    private final int INDEX_BUCKET_NAME   = 1;
    private final int INDEX_BUCKET_URL    = 2;

    GridView albumGrid;

    boolean hasNeedCrop = false;
    int encodingType;
    int returnType;

    String packageName;
    boolean isPreview;
    AlbumAdapter adapter;

    int maxCount = 1;

    List<AlbumBean> albums = new ArrayList<AlbumBean>();

    private Cursor mCursor;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hasNeedCrop = getIntent().getBooleanExtra(Config.INTENT_VALUE_NEED_CROP,false);
        encodingType = getIntent().getIntExtra(Config.INTENT_VALUE_ENCODINGTYPE, Config.ENCODINGTYPE_JPEG);
        returnType = getIntent().getIntExtra(Config.INTENT_VALUE_RETURNTYPE,Config.RETURNTYPE_DATA);
        maxCount = getIntent().getIntExtra(Config.INTENT_VALUE_MAXCOUNT,1);
        isPreview= getIntent().getBooleanExtra(Config.INTENT_VALUE_IS_PREVIEW, true);
        packageName = this.getPackageName();
        LinearLayout root = (LinearLayout) View.inflate(this,getResources().getIdentifier("activity_album","layout",packageName),null);
        setContentView(root);

        albumGrid = (GridView) root.getChildAt(1);
        albumGrid.setOnItemClickListener(this);

        adapter = new AlbumAdapter(albums);

        albumGrid.setAdapter(adapter);
        loadData();
    }
    public void backClick(View view){
        this.finish();
    }

    private void loadData(){
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        mCursor = this.getContentResolver().
            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                PROJECTION_BUCKET,
                null,
                null,
                orderBy + " DESC");
        try {
            while (mCursor.moveToNext()) {
                AlbumBean bean = new AlbumBean(
                    mCursor.getInt(INDEX_BUCKET_ID),
                    mCursor.getString(INDEX_BUCKET_NAME), mCursor.getString(INDEX_BUCKET_URL));

                if (!albums.contains(bean)) {
                    albums.add(bean);
                }
            }

            if (mCursor.getCount() > 0) {
                adapter.notifyDataSetChanged();
            }

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(mCursor!=null) {
                mCursor.close();
                mCursor = null;
            }
        }
    }

    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i<0 || i >= albums.size()){
            return;
        }

        AlbumBean bean = albums.get(i);
        Intent intent = new Intent(this,SelectImageActivity.class);
        intent.putExtra("name",bean.getName());
        intent.putExtra(Config.INTENT_VALUE_MAXCOUNT,maxCount);
        intent.putExtra(Config.INTENT_VALUE_IS_PREVIEW,isPreview);
        this.startActivityForResult(intent,Config.REQUEST_CODE_ALBUM_TO_SELECT_IMG);

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //选择图片成功或裁减成功
            switch (requestCode){
                case Config.REQUEST_CODE_ALBUM_TO_SELECT_IMG:
                    //选择图片返回
                    if(maxCount > 1){
                        setResult(RESULT_OK, getIntent().putExtra(Config.INTENT_VALUE_URI_LIST,data.getStringExtra(Config.INTENT_VALUE_URI_LIST)));
                        finish();
                    }else {
                        doCameraReturn(data.getData());
                    }
                    break;

                case Config.REQUEST_CODE_ALBUM_TO_IMG_CROP:
                    //裁减图片返回
                    doCropReturn(data);
                    break;
            }
        }

    }

    private void doCameraReturn(Uri uri){
        try{

            //判断是否需要裁减图片
            if(hasNeedCrop){
                //调用裁减
                Intent intent = new Intent(AlbumActivity.this,CropActivity.class);
                intent.putExtra(Config.INTENT_VALUE_CROP_WIDTH,getIntent().getIntExtra(Config.INTENT_VALUE_CROP_WIDTH,200));
                intent.putExtra(Config.INTENT_VALUE_CROP_HEIGHT,getIntent().getIntExtra(Config.INTENT_VALUE_CROP_HEIGHT,200));
                intent.putExtra(Config.INTENT_VALUE_CROP_PATH,uri.toString());
                AlbumActivity.this.startActivityForResult(intent,
                    Config.REQUEST_CODE_ALBUM_TO_IMG_CROP);

            }else{
                setResult(RESULT_OK, getIntent().setData(uri));
                finish();
            }
        }catch (Exception e){
            setResult(RESULT_OK, getIntent().setData(uri));
            finish();
        }

    }

    private void doCropReturn(Intent data){
        data.putExtra(Config.INTENT_VALUE_ENCODINGTYPE,encodingType);
        data.putExtra(Config.INTENT_VALUE_RETURNTYPE,returnType);
        setResult(RESULT_OK, data);
        this.finish();
    }




    public class AlbumAdapter extends BaseAdapter{


        List<AlbumBean> albums;

        public AlbumAdapter(List<AlbumBean> list){
            albums = list;
        }

        @Override public int getCount() {
            return albums.size();
        }

        @Override public AlbumBean getItem(int i) {
            return albums.get(i);
        }

        @Override public long getItemId(int i) {
            return i;
        }

        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder = null;
            if(view == null){
                view = View.inflate(AlbumActivity.this,getResources().getIdentifier("item_album_grid","layout",packageName),null);
                holder = new Holder(view);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
            }
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.img.getLayoutParams();
            lp.width = lp.height = viewGroup.getWidth() / 2;
            holder.img.setLayoutParams(lp);

            lp = (FrameLayout.LayoutParams) holder.bg.getLayoutParams();
            lp.width = lp.height = viewGroup.getWidth() / 2;
            holder.bg.setLayoutParams(lp);

            ImageLoadAsync loadAsync = new ImageLoadAsync(AlbumActivity.this,holder.img,200);
            loadAsync.executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR,getItem(i).getUrl());

            holder.name.setText(getItem(i).getName());
            return view;
        }

        private class Holder{
            ImageView img;
            TextView name;
            ImageView bg;

            public Holder(View view){
                img =
                    (ImageView) view.findViewById(getResources().getIdentifier("item_album_img","id",packageName));
                bg =
                    (ImageView) view.findViewById(getResources().getIdentifier("item_album_bg","id",packageName));
                name =
                    (TextView) view.findViewById(getResources().getIdentifier("item_album_name","id",packageName));
            }
        }
    }
}
