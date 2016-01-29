package com.star.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.View;

public class ViewRecycleBin {
	private Map<Object,List<View>> _map = new HashMap<Object,List<View>>();
	
	public void putView(View view){
		Object tag = view.getTag(R.id.recycle_tag);
		if(tag != null){
			if( ! _map.containsKey(tag)){
				_map.put(tag, new ArrayList<View>());
			}
			_map.get(tag).add(view);
			view.forceLayout();
		}else{
			Log.w("ViewRecycleBin", view.getClass() + "  don't has R.id.recycle_tag");
		}
		AsyncUtil.clearViewAsyncRunnable(view);
	}

	public View popView(Object tag){
		if( _map.containsKey(tag)){
			List<View> list = _map.get(tag);
			if(list.size() != 0){
				return list.remove(0);
			}
		}
		return null;
	}
}
