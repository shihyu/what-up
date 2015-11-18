package cn.wehax.whatup.model.file;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;

/**
 * Created by howe on 15/6/11.
 * Email:howejee@gmail.com
 */
public interface UploadCallback {

    public void done(AVFile avFile,AVException e);

    public void progress(int progress);
}
