package com.mybank.pc.kits;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mybank.pc.Consts;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.UrlSafeBase64;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 简介
 * <p>
 * 项目名称: [cms] 包: [com.dbd.cms.kits] 类名称: [QiNiuKit] 类描述: [七牛云储存工具 ] 创建人: [于海慧]
 * 创建时间: [2016/12/4] 修改人: [] 修改时间: [] 修改备注: [] 版本: [v1.0]
 */
public class QiNiuKit {

	private static UploadManager uploadManager;
	private static Auth auth;
	private static Zone z;
	private static BucketManager bucketManager;

	private QiNiuKit() {

	}

	private static UploadManager getUploadManager() {
		if (uploadManager == null) {
			uploadManager = new UploadManager(configuration());
		}
		return uploadManager;
	}

	private static BucketManager getBucketManage() {
		if (bucketManager == null) {
			// 实例化一个BucketManager对象
			Configuration configuration = configuration();
			bucketManager = new BucketManager(auth, configuration);
		}
		return bucketManager;
	}

	private static Configuration configuration() {
		String AK = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "AK");
		String SK = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "SK");
		// 密钥配置
		auth = Auth.create(AK, SK);
		z = Zone.zone1();
		return new Configuration(z);
	}

	// 简单上传，使用默认策略，只需要设置上传的空间名就可以了
	public static String getUpToken() {
		if (auth == null)
			configuration();
		return auth.uploadToken((String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_bucket"));
	}

	public static void upload(File file, String qn_filename) throws QiniuException {

		// 调用put方法上传
		Response res = getUploadManager().put(file, qn_filename, getUpToken());
		// 打印返回的信息
		LogKit.info(res.bodyString());

	}

	public static void del(String qn_filename) throws QiniuException {
		String bucket = (String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_bucket");
		// 调用put方法上传
		getBucketManage().delete(bucket, qn_filename);

	}

	public static String put64image(String base64Str, String imgKey) throws IOException {
		LogKit.info(base64Str);
		String[] array = base64Str.split(",");
		base64Str = array[1];
		String url = "http://up-z1.qiniu.com/putb64/-1/key/" + UrlSafeBase64.encodeToString(imgKey);
		// 非华东空间需要根据注意事项 1 修改上传域名
		RequestBody rb = RequestBody.create(null, base64Str);
		Request request = new Request.Builder().url(url).addHeader("Content-Type", "application/octet-stream")
				.addHeader("Authorization", "UpToken " + getUpToken()).post(rb).build();

		OkHttpClient client = new OkHttpClient();
		okhttp3.Response response = null;
		response = client.newCall(request).execute();

		String rs = response.body().string();
		LogKit.info("以base64字符串形式上传至七牛的图片返回结果：" + rs);
		JSONObject jsonObject = JSON.parseObject(rs);

		return jsonObject.containsKey("hash") ? Consts.YORN_STR.yes.name() : Consts.YORN_STR.no.name();

	}

	public static void main(String[] args) throws IOException {

		FileInputStream fis = null;
		int l = (int) (new File("/Users/yuhaihui8913/Pictures/dft-avatar.jpg").length());
		byte[] src = new byte[l];
		fis = new FileInputStream(new File("/Users/yuhaihui8913/Pictures/dft-avatar.jpg"));
		fis.read(src);
		String file64 = Base64.encodeToString(src, 0);
		System.out.println(file64);

		upload(new File("/Users/yuhaihui8913/Pictures/dft-avatar.jpg"), "image/avart/test.jpg");
	}

}
