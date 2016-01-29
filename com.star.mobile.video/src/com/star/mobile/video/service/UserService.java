package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.Comment;
import com.star.cms.model.CustomerInfo;
import com.star.cms.model.User;
import com.star.cms.model.dto.BindAccountResult;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.sms.Product;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.view.PhoneNumberInputView;
import com.star.util.json.JSONUtil;

public class UserService {

	private static final String TAG = UserService.class.getName();



	/**
	 * 1.2.9 +
	 * 
	 * @param smartCardNo
	 *            智能卡号
	 * @return
	 */
	public Double getMoney(String smartCardNo) {
		String json = null;
		try {
			json = IOUtil.httpGetToJSON(Constant.getMoneyUrl(smartCardNo));
			if (json != null && !json.isEmpty()) {
				return JSONUtil.getFromJSON(json, Double.class);
			}
		} catch (Exception e) {
			Log.e(TAG, "get money", e);
		}
		return null;
	}

	@Deprecated
	public List<Comment> getAppComments() {
		try {
			String json = IOUtil.httpGetToJSON(Constant.getFeedbackUrl());
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Comment>>() {
				}.getType());
			} else {
				return null;
			}
		} catch (Exception e) {

			return null;
		}

	}

	public List<Product> getProduct(String resCode) {
		try {
			String json = IOUtil.httpGetToJSON(Constant.getProductUrl(resCode));
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<Product>>() {
				}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "get product erro ", e);
		}

		return null;
	}


	public User getUser(Context context) {
		try {
			String json = IOUtil.httpGetToJSON(Constant.getMeUrl());
			if (json != null) {
				User u = JSONUtil.getFromJSON(json, new TypeToken<User>() {
				}.getType());
				if (u != null) {
					SharedPreferencesUtil.saveUserInfo(context, json);
					StarApplication.mUser = u;
				}
				return u;
			}
		} catch (Exception e) {
			Log.w("MeService", "", e);
		}
		return null;
	}

	public void getUser(final Context context, final boolean showDialog) {
		if (StarApplication.mUser != null && listener != null) {
			listener.callback(StarApplication.mUser);
			return;
		}
		new LoadingDataTask() {
			User u;

			@Override
			public void onPreExecute() {
				if (showDialog)
					CommonUtil.showProgressDialog(context, null, context.getString(R.string.loading), false);
			}

			@Override
			public void onPostExecute() {
				if (showDialog)
					CommonUtil.closeProgressDialog();
				if (u != null) {
					if (listener != null) {
						listener.callback(u);
					}
				}
			}

			@Override
			public void doInBackground() {
				u = getUser(context);
			}
		}.execute();
	}

	public void initUserInfo(final Context context) {
		StarApplication.mUser = SharedPreferencesUtil.getUserInfo(context);
		new Thread() {
			public void run() {
				new UserService().getUser(context);
			};
		}.start();
	}

	private CallbackListener listener;

	public interface CallbackListener {
		abstract void callback(User user);
	}

	public void setCallbackListener(CallbackListener listener) {
		this.listener = listener;
	}

	public void updateCoins(Context context, int addCoins) {
		if (StarApplication.mUser == null) {
			getUser(context);
		} else {
			StarApplication.mUser.setCoins(StarApplication.mUser.getCoins() + addCoins);
			SharedPreferencesUtil.saveUserInfo(context, JSONUtil.getJSON(StarApplication.mUser));
		}
	}

	@Deprecated
	public Integer getTaskNumCanDo(boolean fromLocal) {
		try {
			String json = null;
			if (fromLocal) {
				json = IOUtil.getCachedJSON(Constant.getTaskNumUrl());
			} else {
				json = IOUtil.httpGetToJSON(Constant.getTaskNumUrl(), true);
			}
			if (json != null) {
				return Integer.parseInt(json);
			}
		} catch (Exception e) {
			Log.i("MeService", "", e);
		}
		return null;
	}

	
	@Deprecated
	public boolean uploadHeadImage(Bitmap bitmap) {
		boolean result = false;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("image", bitmap);
			HttpResponse hr = IOUtil.httpImagePost(params, Constant.getUploadHeadUrl(), CompressFormat.PNG);
			if (hr == null)
				return result;
			int statusCode = hr.getStatusLine().getStatusCode();
			if (!(statusCode > 200 || statusCode < 300)) {
				return result;
			}
			// InputStream is = hr.getEntity().getContent();
			// String json = IOUtil.streamToString(is);
			String json = IOUtil.entityToString(hr);
			if (json != null) {
				result = JSONUtil.getFromJSON(json, Boolean.class);
			}
		} catch (Exception e) {
			Log.e(TAG, "upload head image error.", e);
		}
		return result;
	}

	/**
	 * 删除本地卡号json数据
	 */
	public void delCachedAllSmartCardNo() {
		IOUtil.delCachedJSON(Constant.getSmartCardNoUrl());
	}

	public List<SmartCardInfoVO> getExpectedStopSmartcard() {
		String json = IOUtil.httpGetToJSON(Constant.getExpectedStopSmartcard());
		if (json != null) {
			return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<SmartCardInfoVO>>() {
			}.getType());
		} else {
			return null;
		}
	}

	/**
	 * 查询客户信息
	 * 
	 * @param smartCardNo
	 * @return
	 */
	public List<CustomerInfo> getCustomerInfos(String smartCardNo) {
		try {
			String json = IOUtil.httpGetToJSON(Constant.getCustomers(smartCardNo));
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<CustomerInfo>>() {
				}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "get smardCardInfo error ", e);
		}
		return null;
	}

	public User isExist(String userName) {
		try {
			userName = userName.replaceAll("#", "%23");
			userName = userName.replaceAll(" ", "%20");
			String json = IOUtil.httpGetToJSON(Constant.getIsexist() + "?userName=" + userName);
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<User>() {
				}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "get user isExist error ", e);
		}
		return null;
	}

	public BindAccountResult bindAccount(String account, String pwd, String deviceID, Long parentID, AccountType type, Integer versionCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("parentID", parentID);
		params.put("pwd", pwd);
		params.put("deviceID", deviceID);
		params.put("type", type.getNum());
		params.put("versionCode", versionCode);
		try {
			String json = IOUtil.httpPostToJSON(params, Constant.getBindAccountUrl());
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<BindAccountResult>() {
				}.getType());
			}
		} catch (Exception e) {
			Log.e(TAG, "bind account error!", e);
		}
		return null;
	}

	public List<User> getBindedAccounts(Long parentID) {
		try {
			String json = IOUtil.httpGetToJSON(Constant.getBindedAccountsUrl(parentID));
			if (json != null) {
				return JSONUtil.getFromJSON(json, new TypeToken<ArrayList<User>>() {
				}.getType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void saveExpectedStopSmartcard(final Context context) {
		new LoadingDataTask() {
			List<SmartCardInfoVO> scvs;

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if (scvs != null) {
					AlertManager.getInstance(context).alertExpectedStopSmartcard(scvs, true);
				}
			}

			@Override
			public void doInBackground() {
				Log.d(TAG, "get expectedStop smcartcard");
				scvs = getExpectedStopSmartcard();
			}
		}.execute();
	}

	public void verfyExpectedStopSmartcard(final Context context) {
		new LoadingDataTask() {
			List<SmartCardInfoVO> scvs;

			@Override
			public void onPreExecute() {
			}

			@Override
			public void onPostExecute() {
				if (scvs != null) {
					AlertManager.getInstance(context).alertExpectedStopSmartcard(scvs, false);
				}
			}

			@Override
			public void doInBackground() {
				Log.d(TAG, "verfy expectedStop smcartcard");
				scvs = getExpectedStopSmartcard();
			}
		}.execute();
	}

	public boolean setPhoneOrEmail(PhoneNumberInputView phoneInputView, EditText emailView, String username) {
		boolean isPhone = false;
		if (username != null && CommonUtil.match(Constant.NUMBER_REG, username)) {
			phoneInputView.setPhoneNumber(username);
			isPhone = true;
		} else if (username != null) {
			if (!username.contains(User.PrefixOfUsr3Party)) {
				emailView.setText(username);
				isPhone = false;
			}
		} else if (username == null && !FunctionService.doHideFuncation(FunctionType.RegisterWithPhone)) {
			isPhone = true;
		}
		return isPhone;
	}
}
