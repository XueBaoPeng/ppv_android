package com.star.mobile.video.util.adapterutils;



import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;

/**
 * ViewHolder公共类
 * 
 * @author Lee
 * @version 1.0
 * @date 2015/10/26
 *
 */
public class ViewHolder {

	private SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;
	private Context mContext;
	private int mSelectPosition;

	public void setmSelectPosition(int mSelectPosition) {
		this.mSelectPosition = mSelectPosition;
	}

	public View getConvertView() {
		return mConvertView;
	}

	public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		this.mContext = context;
		this.mViews = new SparseArray<View>();
		this.mPosition = position;
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		this.mConvertView.setTag(this);
	}

	/**
	 * 获得ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		if (null == convertView) {
			return new ViewHolder(context, parent, layoutId, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}

	/**
	 * 通过viewId获取控件
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (null == view) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 给ID为viewId的TextView设置文字text，并返回this
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}

	/**
	 * 设置图片
	 * 
	 * @param viewId
	 * @param imageResource
	 * @return
	 */
	public ViewHolder setImageView(int viewId, int imageResource) {
		ImageView imageView = getView(viewId);
		imageView.setImageResource(imageResource);
		return this;
	}
	 
	/**
	 * 设置fresco的图片
	 * @param viewId
	 * @param url
	 * @return
	 */
	 
	/*public ViewHolder setFrescoImageView(int viewId,String url){
		final SimpleDraweeView imageView=getView(viewId);
		imageView.setBackgroundResource(R.drawable.channel_detail_bg);
		ProgressiveJpegConfig jpegConfig = new ProgressiveJpegConfig() {
            @Override
            public int getNextScanNumberToDecode(int scanNumber) {
                return scanNumber + 2;
            }

            @Override
            public QualityInfo getQualityInfo(int scanNumber) {
                boolean isGoodEnough = (scanNumber >= 5);
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
            }
        };
        Uri uri=Uri.parse(url);
        ImagePipelineConfig.newBuilder(mContext).setProgressiveJpegConfig(jpegConfig).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())//使用oldController可以节省不必要的内存分配
                .build();
        imageView.setController(draweeController);
		
		return this;
	} */
 
	/**
	 * 设置图片url
	 * 
	 * @param viewId
	 * @param url
	 * @return
	 */
	public ViewHolder setImageView(int viewId, String url) {
		final com.star.ui.ImageView imageView = getView(viewId);
		imageView.setImageResource(R.drawable.channel_detail_bg);
//		imageView.setFinisher(new Finisher() {
//
//			@Override
//			public void run() {
//				// 4dp的圆角
//				Bitmap bitmap = BitmapUtil.getRoundedCornerBitmap(imageView.getImage(),
//						DensityUtil.dip2px(mContext, 4));
//				imageView.setImageBitmap(bitmap);
//			}
//		});
		imageView.setUrl(url);
		return this;
	}

}
