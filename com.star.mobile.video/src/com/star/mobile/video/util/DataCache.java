package com.star.mobile.video.util;

import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.star.cms.model.vo.ProgramVO;

public class DataCache {

	private static final int HARD_CACHE_CAPACITY = 50;
	private static BitmapFactory.Options options = new BitmapFactory.Options();
	private static final char HEX_DIGITS[] = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	
	static {
		options.inSampleSize = 2;
		options.inPurgeable = true;
	}

	private static DataCache cache;

	private DataCache() {
	}

	public static synchronized DataCache getInstance() {
		if (cache != null) {
			return cache;
		} else {
			cache = new DataCache();
			return cache;
		}

	}

	public HashMap<Long, List<ProgramVO>> getOneLevelProgramCache(){
		return mOneLevelProgramCache;
	}
	/**
	 * 一级缓存
	 */
	private final HashMap<Long, List<ProgramVO>> mOneLevelProgramCache = new LinkedHashMap<Long, List<ProgramVO>>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, List<ProgramVO>> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				mTwoLevelProgramCache.put(eldest.getKey(), new SoftReference<List<ProgramVO>>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	/**
	 * 二级缓存
	 */
	private final static ConcurrentHashMap<Long, SoftReference<List<ProgramVO>>> mTwoLevelProgramCache = new ConcurrentHashMap<Long, SoftReference<List<ProgramVO>>>();

	public List<ProgramVO> getProgramFromCache(long channelId) {
		synchronized (mOneLevelProgramCache) {
			final List<ProgramVO> programs = mOneLevelProgramCache.get(channelId);
			if (programs != null) {
				// 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
				mOneLevelProgramCache.remove(channelId);
				mOneLevelProgramCache.put(channelId, programs);
				return programs;
			}
		}

		// 如果一级缓存中找不到，到二级缓存中找
		SoftReference<List<ProgramVO>> programReference = mTwoLevelProgramCache.get(channelId);
		if (programReference != null) {
			final List<ProgramVO> programs = programReference.get();
			if (programs != null) {
				mOneLevelProgramCache.put(channelId, programs);
				mTwoLevelProgramCache.remove(channelId);
				return programs;
			} else {
				mTwoLevelProgramCache.remove(channelId);
			}
		}
		return null;
	}
	
	public HashMap<String, Drawable> getOneLevelChannelLogoCache(){
		return mOneLevelChannelLogoCache;
	}
	
	/**
	 * 一级缓存
	 */
	private final HashMap<String, Drawable> mOneLevelChannelLogoCache = new LinkedHashMap<String, Drawable>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Drawable> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				mTwoLevelChannelLogoCache.put(eldest.getKey(), new SoftReference<Drawable>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	/**
	 * 二级缓存
	 */
	private final static ConcurrentHashMap<String, SoftReference<Drawable>> mTwoLevelChannelLogoCache = new ConcurrentHashMap<String, SoftReference<Drawable>>();

	public Drawable getDrawableFromCache(String url) {
		String key = MD5encode(url);
		synchronized (mOneLevelChannelLogoCache) {
			final Drawable drawable = mOneLevelChannelLogoCache.get(key);
			if (drawable != null) {
				// 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
				mOneLevelChannelLogoCache.remove(key);
				mOneLevelChannelLogoCache.put(key, drawable);
				return drawable;
			}
		}

		// 如果一级缓存中找不到，到二级缓存中找
		SoftReference<Drawable> bitmapReference = mTwoLevelChannelLogoCache.get(key);
		if (bitmapReference != null) {
			final Drawable drawable = bitmapReference.get();
			if (drawable != null) {
				mOneLevelChannelLogoCache.put(key, drawable);
				mTwoLevelChannelLogoCache.remove(key);
				return drawable;
			} else {
				mTwoLevelChannelLogoCache.remove(key);
			}
		}
		return null;
	}
	
    /**
     * MD5 加密
     *
     * @param s
     * @return
     */
    private static String MD5encode(String s) {
        if (s == null) {
            s = "null";
        }

        byte[] strTemp = s.getBytes();
        MessageDigest mdTemp;
        try {
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }
    
    public HashMap<Long, ProgramVO> getOneLevelEPGCache(){
		return mOneLevelEPGCache;
	}
    
    /**
	 * 一级缓存
	 */
	private final HashMap<Long, ProgramVO> mOneLevelEPGCache = new LinkedHashMap<Long, ProgramVO>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, ProgramVO> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				mTwoLevelEPGCache.put(eldest.getKey(), new SoftReference<ProgramVO>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};

	/**
	 * 二级缓存
	 */
	private final ConcurrentHashMap<Long, SoftReference<ProgramVO>> mTwoLevelEPGCache = new ConcurrentHashMap<Long, SoftReference<ProgramVO>>();

	public ProgramVO getEPGFromCache(Long key) {
		synchronized (mOneLevelEPGCache) {
			final ProgramVO value = mOneLevelEPGCache.get(key);
			if (value != null) {
				// 如果找到的话，把元素移到linkedhashmap的最前面，从而保证在LRU算法中是最后被删除
				mOneLevelEPGCache.remove(key);
				mOneLevelEPGCache.put(key, value);
				return value;
			}
		}

		// 如果一级缓存中找不到，到二级缓存中找
		SoftReference<ProgramVO> vReference = mTwoLevelEPGCache.get(key);
		if (vReference != null) {
			final ProgramVO value = vReference.get();
			if (value != null) {
				mOneLevelEPGCache.put(key, value);
				mTwoLevelEPGCache.remove(key);
				return value;
			} else {
				mTwoLevelEPGCache.remove(key);
			}
		}
		return null;
	}
}
