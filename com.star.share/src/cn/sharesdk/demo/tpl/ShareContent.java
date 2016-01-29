package cn.sharesdk.demo.tpl;

import java.io.Serializable;

import cn.sharesdk.onekeyshare.EditPage.ColseFriendCallback;

public class ShareContent implements Serializable {
    private String text;    
    private String url;
    private String imagepath;
    private String title;
//    private ColseFriendCallback colseCallback;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
//	public ColseFriendCallback getColseCallback() {
//		return colseCallback;
//	}
//	public void setColseCallback(ColseFriendCallback colseCallback) {
//		this.colseCallback = colseCallback;
//	}
   
  
    
}