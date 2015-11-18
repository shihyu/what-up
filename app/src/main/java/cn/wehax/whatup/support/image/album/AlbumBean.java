package cn.wehax.whatup.support.image.album;

import java.io.Serializable;

/**
 * Created by howe on 15/3/27.
 */
public class AlbumBean implements Serializable{
    private String name;
    private int id;
    private String url = null;

    AlbumBean(int id,String name,String url){
        this.id = id;
        this.name = name;
        this.url = url;
    }

    @Override public int hashCode() {
        return id;
    }

    @Override public boolean equals(Object o) {
        if(this == o){
            return true;
        }

        if(!(o instanceof AlbumBean)){
            return false;
        }

        AlbumBean that = (AlbumBean) o;
        return that.getId() == this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
