package cn.wehax.whatup.support.image.album;

import java.io.Serializable;

/**
 * Created by howe on 15/3/27.
 */
public class AlbumImageBean implements Serializable {
    private String url = null;

    private long time = 0;

    private String name = null;

    private boolean isChecked = false;

    public AlbumImageBean(String url,long time){
        this.url = url;
        this.time = time;
    }
    public AlbumImageBean(String url,String name,long time){
        this.url = url;
        this.time = time;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
