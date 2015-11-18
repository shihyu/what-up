package cn.wehax.whatup.model.status;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by howe on 15/6/13.
 * Email:howejee@gmail.com
 */
@DatabaseTable(tableName = "status")
public class Status implements Serializable {

    @DatabaseField(id = true)
    private String statusId;

    @DatabaseField
    private boolean isWater;

    @DatabaseField
    private String city;

    @DatabaseField
    private String text;

    @DatabaseField
    private String location;

    @DatabaseField
    private String imageId;

    @DatabaseField
    private String imageUrl;

    @DatabaseField
    private String coord;




    public Status(String statusId, String text, String location, String imageId, String imageUrl,String city,boolean isWater,String coord) {
        this.statusId = statusId;
        this.city = city;
        this.text = text;
        this.location = location;
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.isWater = isWater;
        this.coord = coord;
    }

    public Status() {

    }
    public String getCoord() {
        return coord;
    }
    public void setCoord(String coord) {
        this.coord = coord;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public boolean isWater() {
        return isWater;
    }

    public void setWater(boolean isWater) {
        this.isWater = isWater;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
