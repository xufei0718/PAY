
package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.mybank.pc.kits.unionpay.acp.BaseHttpSSLSocketFactory;

/**
 * @description http or https请求辅助类demo。（请第三方自行设计实现，本代码仅供demo演示用。）
 * @author www
 * @version 1.0
 * @modify
 * @Copyright 中国银联股份有限公司版权拥有
 */
public class HttpClient {
	private URL url;// 请求地址
	private int connectionTimeout;// 连接最大超时（毫秒）
	private int readTimeOut;// 读取数据流最大超时（毫秒）
	private String result;// 应答结果

	public static final String UTF_8_ENCODING = "UTF-8";

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public HttpClient(String url, int connectionTimeout, int readTimeOut) {
		try {
			this.url = new URL(url);
			this.connectionTimeout = connectionTimeout;
			this.readTimeOut = readTimeOut;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送请求报文
	 * 
	 * @param data
	 *            请求报文参数
	 * @param encoding
	 *            编码，默认utf-8
	 * @return 应答结果
	 * @throws Exception
	 */
	public int send(Map<String, String> data, String encoding) throws Exception {
		try {
			HttpURLConnection httpURLConnection = createConnection(encoding);
			if (null == httpURLConnection)
				throw new Exception("创建联接失败");

			requestServer(httpURLConnection, getRequestParamString(data, encoding), encoding);

			this.result = response(httpURLConnection, encoding);
			return httpURLConnection.getResponseCode();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 请求操作
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求报文map
	 * @param encoding
	 *            编码
	 * @param connectionTimeout
	 *            连接超时
	 * @param readTimeout
	 *            应答超时
	 * @return 应答结果
	 */
	public static String send(String url, Map<String, String> data, String encoding, int connectionTimeout,
			int readTimeout) {
		HttpClient hc = new HttpClient(url, connectionTimeout, readTimeout);
		String res = "";
		try {
			int status = hc.send(data, encoding);
			if (200 == status)
				res = hc.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private void requestServer(URLConnection connection, String message, String encoder) throws Exception {
		PrintStream out = null;
		try {
			connection.connect();
			out = new PrintStream(connection.getOutputStream(), false, encoder);
			out.print(message);
			out.flush();
		} catch (Exception e) {
		} finally {
			if (null != out)
				out.close();
		}
	}

	private String response(HttpURLConnection connection, String encoding)
			throws URISyntaxException, IOException, Exception {
		InputStream in = null;
		StringBuilder sb = new StringBuilder(1024);
		BufferedReader br = null;
		String temp = null;
		try {
			if (200 == connection.getResponseCode()) {
				in = connection.getInputStream();
				// 应答报文为UTF-8编码
				br = new BufferedReader(new InputStreamReader(in, UTF_8_ENCODING));
				while (true) {
					if (null == (temp = br.readLine())) {
						return sb.toString();
					}
					sb.append(temp);
				}
			} else {
				in = connection.getErrorStream();
				// 应答报文为UTF-8编码
				br = new BufferedReader(new InputStreamReader(in, UTF_8_ENCODING));
				while (null != (temp = br.readLine())) {
					sb.append(temp);
				}
				return sb.toString();
			}
		} catch (Exception e) {
		} finally {
			if (null != br)
				br.close();

			if (null != in)
				in.close();

			if (null != connection)
				connection.disconnect();
		}
		return sb.toString();
	}

	private HttpURLConnection createConnection(String encoding) throws ProtocolException {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) this.url.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		httpURLConnection.setConnectTimeout(this.connectionTimeout);
		httpURLConnection.setReadTimeout(this.readTimeOut);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestProperty("Content-type",
				new StringBuilder().append("application/x-www-form-urlencoded;charset=").append(encoding).toString());

		httpURLConnection.setRequestMethod("POST");
		if ("https".equalsIgnoreCase(this.url.getProtocol())) {
			HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
			husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
			husn.setHostnameVerifier(new BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier());
			return husn;
		}
		return httpURLConnection;
	}

	private String getRequestParamString(Map<String, String> requestParam, String coder) {
		if ((null == coder) || ("".equals(coder)))
			coder = UTF_8_ENCODING;

		StringBuffer sf = new StringBuffer("");
		String reqstr = "";
		if ((null != requestParam) && (0 != requestParam.size())) {
			for (@SuppressWarnings("rawtypes")
			Map.Entry en : requestParam.entrySet())
				try {
					sf.append(new StringBuilder().append((String) en.getKey()).append("=")
							.append(((null == en.getValue()) || ("".equals(en.getValue()))) ? ""
									: URLEncoder.encode((String) en.getValue(), coder))
							.append("&").toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return "";
				}

			reqstr = sf.substring(0, sf.length() - 1);
		}
		return reqstr;
	}
}