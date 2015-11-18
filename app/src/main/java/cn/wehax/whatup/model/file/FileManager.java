package cn.wehax.whatup.model.file;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by howe on 15/6/11.
 * Email:howejee@gmail.com
 */
@Singleton
public class FileManager {

    Application app;

    @Inject
    FileManager(Provider<Application> application) {
        app = application.get();
    }


//        avFile.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                callback.done(avFile, e);
//            }
//        }, new ProgressCallback() {
//            @Override
//            public void done(Integer integer) {
//                callback.progress(integer.intValue());
//            }
//        });
//    }


}
