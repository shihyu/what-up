package cn.wehax.whatup.vp.main;

import java.util.ArrayList;
import java.util.Map;

import cn.wehax.whatup.ar.marker.IMarkerOnClickListener;

/**
 * Created by mayuhan on 15/6/10.
 */
public interface IMainView {
    public void refreshStatus(ArrayList<Map> data);

    public void goToChatView(String targeUserId, String statusId);

    public void goToOtherHomePage(String targetUseId, String statusId);

    public void animToStartSendStatus();

    public void animToSendStatusSuccess();

    public void animToSendStatusError();

    public void setStatusThumbnail(String url);

    public void setUserAvatar(String url);

//    public IMarkerOnClickListener getMarkerOnClickListener();

    void showPersonalAndRelationBtn();
}
