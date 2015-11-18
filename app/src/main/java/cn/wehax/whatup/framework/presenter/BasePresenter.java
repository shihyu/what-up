package cn.wehax.whatup.framework.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.wehax.whatup.framework.view.IBaseView;

public abstract class BasePresenter<T>{
    protected T mView;
    private Bundle arguments;

    public void setView(T view){
        mView =  view;
    }

    public void init(T view){
        init(view, null);
    }

    public void init(T view, Bundle arguments){
        setView(view);
        this.arguments = arguments;
    }

    /**
     * 获取 Activity Context
     */
    public Activity getActivity(){
        if(mView instanceof Activity){
            return (Activity) mView;
        }else if(mView instanceof Fragment){
            return ((Fragment) mView).getActivity();
        }else if(mView instanceof IBaseView){
            return ((IBaseView) mView).getActivity();
        }else{
            return null;
        }
    }

    public Bundle getArguments() {
        return arguments;
    }
}
