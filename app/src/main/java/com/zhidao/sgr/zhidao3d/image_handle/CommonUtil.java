package com.zhidao.sgr.zhidao3d.image_handle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CommonUtil {

	public static String NETWORT_2G = "2G";
	public static String NETWORT_3G = "3G";
	public static String NETWORT_4G = "4G";
	public static String NETWORT_WIFI = "WIFI";
	private static final Pattern PATTERN = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:]+");

	public final static String CoorType_GCJ02 = "gcj02";
	public final static String CoorType_BD09LL = "bd09ll";
	public final static String CoorType_BD09MC = "bd09";
	
	public static boolean finished(Activity ctx) {
		if (ctx == null || ctx.isFinishing())
			return true;
		return false;
	}
	
	 

    /**
     * 判断是否有足够的空间供下载
     * 
     * @param downloadSize
     * @return
     */
    public static boolean isEnoughForDownload(long downloadSize)
    {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
            .getAbsolutePath());

        //sd卡分区数
        int blockCounts = statFs.getBlockCount();

        Log.e("ray", "blockCounts" + blockCounts);

        //sd卡可用分区数
        int avCounts = statFs.getAvailableBlocks();

        Log.e("ray", "avCounts" + avCounts);

        //一个分区数的大小
        long blockSize = statFs.getBlockSize();

        Log.e("ray", "blockSize" + blockSize);

        //sd卡可用空间
        long spaceLeft = avCounts * blockSize;

        Log.e("ray", "spaceLeft" + spaceLeft);

        Log.e("ray", "downloadSize" + downloadSize);

        if (spaceLeft < downloadSize)
        {
            return false;
        }

        return true;
    }


	/***
	 * 61 ： GPS定位结果，GPS定位成功。 62 ：
	 * 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。 63 ：
	 * 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。 65 ： 定位缓存的结果。 66 ：
	 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。 67 ：
	 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
	 * 161： 网络定位结果，网络定位定位成功。 162： 请求串密文解析失败。 167：
	 * 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。 502： key参数错误，请按照说明文档重新申请KEY。 505：
	 * key不存在或者非法，请按照说明文档重新申请KEY。 601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。 602： key
	 * mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，
	 * 请按照说明文档重新申请KEY。 501～700：key验证失败，请按照说明文档重新申请KEY。
	 */

	public static float[] EARTH_WEIGHT = { 0.1f, 0.2f, 0.4f, 0.6f, 0.8f }; // 推算计算权重_地球
	// public static float[] MOON_WEIGHT = {0.0167f,0.033f,0.067f,0.1f,0.133f};
	// public static float[] MARS_WEIGHT = {0.034f,0.068f,0.152f,0.228f,0.304f};

	public static int getSaleStatus(int paramInt) {
		if (paramInt == 0)
			;
		while (paramInt == 2)
			return 1;
		if (paramInt == 3)
			return 2;
		return 0;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {

		return dip2px(context, dpValue);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean notNull(String str) {
		return (str != null) && (str.length() > 0);
	}

	/**
	 * 判断字符串是否为空字符串或Null
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		return str == null || str.length() <= 0;

	}

	/**
	 * 判断Json字符串是否为空字符串或Null
	 * 
	 * @param str
	 * @return
	 */
	public static boolean jsonStrIsNull(String str) {
		return str == null || str.length() <= 0 || str.toLowerCase().equals("null");

	}

	/**
	 * 判断Object是否为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean isURL(String paramString) {
		return (paramString.length() > 0) && (PATTERN.matcher(paramString).find());
	}

	public static String getImageUrl(String paramString) {

		return "http://img1.baidu.com/upload/brand/" + paramString;
	}


	/**
	 * 验证邮箱输入是否合法
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		// String strPattern =
		// "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 验证是否是手机号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 返回当前时间,格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getNowDateStr() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}



	/**
	 * 通过结束时间检查距离当前时间的间隔
	 * 
	 * @param endTime
	 * @return
	 */
	public static String getTimeDifference(String endTime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {

			Date d1 = df.parse(endTime);
			Date d2 = new Date();

			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

			if (diff <= 0)
				return "";

			long days = diff / (1000 * 60 * 60 * 24);

			long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
			long seconds = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;

			return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";

		} catch (Exception e) {
			Log.i("getTimeDifference", e.getMessage());
			return "";
		}
	}

	/**
	 * 转换价格为保留两位小数格式
	 * 
	 * @param price
	 * @param includesymbol
	 *            是否包含人民币货币符号
	 * @return
	 */
	public static String formatPrice(double price, boolean includesymbol) {
		DecimalFormat df = new DecimalFormat("0.00");
		String result = "";
		if (Math.round(price) - price == 0) {
			result = String.valueOf((long) price);
		} else {
			result = String.valueOf(price);
		}

		// result = df.format(price);
		result = includesymbol ? "¥" + result : result;
		return result;
	}

	/**
	 * 转换价格为保留两位小数格式
	 * 
	 * @param price
	 * @param includesymbol
	 *            是否包含人民币货币符号
	 * @return
	 */
	public static String formatPrice(double price, boolean includesymbol, boolean decimalPoint) {
		DecimalFormat df = new DecimalFormat("0.00");
		String result = "";
		if (Math.round(price) - price == 0) {
			result = String.valueOf((long) price);
		} else {
			result = String.valueOf(price);
		}

		if (decimalPoint) {
			result = df.format(price);
		}
		result = includesymbol ? "¥" + result : result;
		return result;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!CommonUtil.isNull(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}




	/**
	 * 缩放图片
	 * 
	 * @param source
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap transImage(Bitmap source, int width, int height) {
		try {

			int bitmapWidth = source.getWidth();
			int bitmapHeight = source.getHeight();

			// 缩放图片的尺寸
			float scaleWidth = (float) width / bitmapWidth;
			float scaleHeight = (float) height / bitmapHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 产生缩放后的Bitmap对象
			Bitmap resizeBitmap = Bitmap.createBitmap(source, 0, 0, bitmapWidth, bitmapHeight, matrix, false);

			if (!source.isRecycled()) {
				// 记得释放资源，否则会内存溢出
				source.recycle();
			}
			return resizeBitmap;

		} catch (Exception e) {
			e.printStackTrace();
			return source;
		}

	}

	public static String getImagePath(String key) {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + key + ".png";
	}

	/**
	 * 保存视图到图片
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap saveViewBitmap(View view) {

		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
		// 利用bitmap生成画布
		Canvas canvas = new Canvas(bitmap);

		// 把view中的内容绘制在画布上
		view.draw(canvas);

		return bitmap;
	}

	public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
		if (null == bmpSrc) {
			return null;
		}

		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();

		Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Config.ARGB_8888);
		if (null != bmpDest) {
			Canvas canvas = new Canvas(bmpDest);
			final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);

			canvas.drawBitmap(bmpSrc, rect, rect, null);
		}

		return bmpDest;
	}



	/**
	 * 
	 * @param fileName
	 *            注意不包含扩展名
	 * @return
	 */
	public static Bitmap loadBitmap(String fileName) {
		String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + fileName + ".png";
		File f = new File(filePath);
		if (f.exists()) {
			return BitmapFactory.decodeFile(filePath, null);

		} else
			return null;
	}

	/**
	 * drawableToBitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(

				drawable.getIntrinsicWidth(),

				drawable.getIntrinsicHeight(),

				drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888

						: Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.setBitmap(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}

	/**
	 * 根据包名判断apk是否安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static final boolean isApkInstalled(Context context, String packageName) {
		try {

			context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * desc:保存对象
	 * 
	 * @param context
	 * @param key
	 * @param obj
	 *            要保存的对象，只能保存实现了serializable的对象
	 */
	public static void saveObject(Context context, String key, Object obj) {
		try {
			// 保存对象
			SharedPreferences.Editor sharedata = context.getSharedPreferences(context.getPackageName(), 0).edit();
			// 先将序列化结果写到byte缓存中，其实就分配一个内存空间
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			// 将对象序列化写入byte缓存
			os.writeObject(obj);
			// 将序列化的数据转为16进制保存
			String bytesToHexString = bytesToHexString(bos.toByteArray());
			// 保存该16进制数组
			sharedata.putString(key, bytesToHexString);
			sharedata.commit();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("", "保存obj失败");
		}
	}

	/**
	 * desc:将数组转为16进制
	 * 
	 * @param bArray
	 * @return modified:
	 */
	public static String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			return null;
		}
		if (bArray.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * desc:获取保存的Object对象
	 * 
	 * @param context
	 * @param key
	 * @return modified:
	 */
	public static Object readObject(Context context, String key) {
		try {
			SharedPreferences sharedata = context.getSharedPreferences(context.getPackageName(), 0);
			if (sharedata.contains(key)) {
				String string = sharedata.getString(key, "");
				if (TextUtils.isEmpty(string)) {
					return null;
				} else {
					// 将16进制的数据转为数组，准备反序列化
					byte[] stringToBytes = StringToBytes(string);
					ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
					ObjectInputStream is = new ObjectInputStream(bis);
					// 返回反序列化得到的对象
					Object readObject = is.readObject();
					return readObject;
				}
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 所有异常返回null
		return null;

	}


	/**
	 * desc:将16进制的数据转为数组
	 * 
	 * @param data
	 * @return
	 * 
	 */
	public static byte[] StringToBytes(String data) {
		String hexString = data.toUpperCase().trim();
		if (hexString.length() % 2 != 0) {
			return null;
		}
		byte[] retData = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i++) {
			int int_ch; // 两位16进制数转化后的10进制数
			char hex_char1 = hexString.charAt(i); //// 两位16进制数中的第一位(高位*16)
			int int_ch1;
			if (hex_char1 >= '0' && hex_char1 <= '9')
				int_ch1 = (hex_char1 - 48) * 16; //// 0 的Ascll - 48
			else if (hex_char1 >= 'A' && hex_char1 <= 'F')
				int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
			else
				return null;
			i++;
			char hex_char2 = hexString.charAt(i); /// 两位16进制数中的第二位(低位)
			int int_ch2;
			if (hex_char2 >= '0' && hex_char2 <= '9')
				int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
			else if (hex_char2 >= 'A' && hex_char2 <= 'F')
				int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
			else
				return null;
			int_ch = int_ch1 + int_ch2;
			retData[i / 2] = (byte) int_ch;// 将转化后的数放入Byte里
		}
		return retData;
	}

	static class CheckServerCallable implements Callable {

		@Override
		public Object call() throws Exception {
			return null;
		}
	}



	/**
	 * 从文件获取图片（预防内存溢出）
	 * 
	 * @param filePath
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmapFromFile(String filePath, int width, int height) {
		File dst = new File(filePath);
		Bitmap bmp = null;
		if (null != dst && dst.exists()) {
			BitmapFactory.Options opts = null;
			try {
				if (width > 0 && height > 0) {
					opts = new BitmapFactory.Options();
					// 设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(dst.getPath(), opts);

					opts.inTempStorage = new byte[12 * 1024];
					opts.inPreferredConfig = Config.RGB_565; // 这里用2字节显示图片，一般是4字节（默认
					// 判断如果使用内存大于3M，你可以修改这个参数，一般10M应该OK
					int ratio = opts.outWidth * opts.outHeight * 2 / 3000000;
					opts.inSampleSize = 1; // 不缩放，保持原来的大小
					if (ratio >= 1) {
						opts.inSampleSize = 3; // 宽度和高度将缩小两倍，width/2 and
												// height/2，所以图片会模糊，不过不会消耗很多内存
					}
					Log.i("打开图片路径", filePath);

					if (ratio >= 4) {
						opts.inSampleSize = 6;
					} else {
						opts.inSampleSize = calculateInSampleSize(opts, width, height);
					}
					// opts.inTempStorage = new byte[12 * 1024];

					opts.inScaled = true;

					// 这里一定要将其设置回false，因为之前我们将其设置成了true
					opts.inJustDecodeBounds = false;

				}

				bmp = BitmapFactory.decodeFile(dst.getPath(), opts);
			} catch (Exception e) {
				e.printStackTrace();
				bmp = null;
			}

		}
		return bmp;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		// Raw height and width of image

		final int height = options.outHeight;

		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;

			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both

			// height and width larger than the requested height and width.

			while ((halfHeight / inSampleSize) > reqHeight

					&& (halfWidth / inSampleSize) > reqWidth) {

				inSampleSize *= 2;

			}

		}

		return inSampleSize;

	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,

			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions

		final BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set

		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(res, resId, options);

	}

	/**
	 * 解决小米手机上获取图片路径为null的情况
	 * 
	 * @param intent
	 * @return
	 */
	public Uri getCaptureUri(android.content.Intent intent, Activity parentActivity) {
		Uri uri = intent.getData();
		String type = intent.getType();
		if (uri.getScheme().equals("file") && (type.contains("image/"))) {
			String path = uri.getEncodedPath();
			if (path != null) {
				path = Uri.decode(path);
				ContentResolver cr = parentActivity.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns._ID }, buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					index = cur.getColumnIndex(Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0) {
					// do nothing
				} else {
					Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
					if (uri_temp != null) {
						uri = uri_temp;
					}
				}
			}
		}
		return uri;
	}
	
	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
	    int degree = 0;
	    try {
	        // 从指定路径下读取图片，并获取其EXIF信息
	        ExifInterface exifInterface = new ExifInterface(path);
	        // 获取图片的旋转信息
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
	                ExifInterface.ORIENTATION_NORMAL);
	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            degree = 90;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            degree = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            degree = 270;
	            break;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return degree;
	}
	
	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static  Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
	    Bitmap returnBm = null;
	  
	    // 根据旋转角度，生成旋转矩阵
	    Matrix matrix = new Matrix();
	    matrix.postRotate(degree);
	    try {
	        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
	        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	    } catch (OutOfMemoryError e) {
	    }
	    if (returnBm == null) {
	        returnBm = bm;
	    }
	    if (bm != returnBm) {
	        bm.recycle();
	    }
	    return returnBm;
	}

}
