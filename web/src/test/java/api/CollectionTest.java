package api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mybank.pc.collection.api.CollAPIRSAKey;
import com.mybank.pc.kits.RSAKit;

public class CollectionTest {

	public static void establishTest() throws Exception {
		Map<String, String> data = new HashMap<String, String>();
		data.put("merCode", "4");
		data.put("merchantID", "20");

//		data.put("accNo", "6216261000000000018");
//		data.put("certifTp", "01");
//		data.put("certifId", "341126197709218366");
//		data.put("customerNm", "全渠道");
//		data.put("phoneNo", "13552535506");
//		data.put("cvn2", "");
//		data.put("expired", "");
		
		data.put("accNo", "6221558812340000");
		data.put("certifTp", "01");
		data.put("certifId", "341126197709218366");
		data.put("customerNm", "互联网");
		data.put("phoneNo", "13552535506");
		data.put("cvn2", "123");
		data.put("expired", "1711");
		
//		data.put("accNo", "6227000736050041914");
//		data.put("certifTp", "01");
//		data.put("certifId", "21010219901114691X");
//		data.put("customerNm", "鲁梓楠");
//		data.put("phoneNo", "13889305840");
//		data.put("cvn2", "");
//		data.put("expired", "");

		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put("req", RSAKit.encrypt(JSON.toJSONString(data), CollAPIRSAKey.COLL_API.getPublicKey()));
		// reqData.put("req", "123456");

		String host = "http://localhost:8082";
		//String host = "https://pac.mybank.cc";
		String result = HttpClient.send(host + "/coll/api/entrust/establish", reqData, HttpClient.UTF_8_ENCODING, 50000,
				50000);
		System.out.println(result);
		String decryptResult = RSAKit.decrypt(result, CollAPIRSAKey.COLL_CLIENT.getPrivateKey());

		JSONObject o = JSON.parseObject(decryptResult);
		System.out.println(o);
		String unionpayEntrust = o.getString("unionpayEntrust");
		System.out.println(unionpayEntrust);
	}

	public static void tradeTest() throws Exception {
		String orderId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		Map<String, String> data = new HashMap<String, String>();
		data.put("merCode", "3");// 系统商户号代码 4:测试 1:春城批量
		data.put("merchantID", "20");// 商户ID
		data.put("bussType", "1");// 1加急 2批量
		data.put("orderId", orderId);
		data.put("txnAmt", "1000");

//		data.put("accNo", "6216261000000000018");
//		data.put("certifTp", "01");
//		data.put("certifId", "341126197709218366");
//		data.put("customerNm", "全渠道");
//		data.put("phoneNo", "13552535506");
//		data.put("cvn2", "");
//		data.put("expired", "");
		
		data.put("accNo", "6227000736050041914");
		data.put("certifTp", "01");
		data.put("certifId", "21010219901114691X");
		data.put("customerNm", "鲁梓楠");
		data.put("phoneNo", "13889305840");
		data.put("cvn2", "");
		data.put("expired", "");

		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put("req", RSAKit.encrypt(JSON.toJSONString(data), CollAPIRSAKey.COLL_API.getPublicKey()));

		String host = "http://localhost:8082";
		//String host = "https://pac.mybank.cc";
		String result = HttpClient.send(host + "/coll/api/trade/initiate", reqData, HttpClient.UTF_8_ENCODING, 50000,
				50000);

		System.out.println(result);
		String decryptResult = RSAKit.decrypt(result, CollAPIRSAKey.COLL_CLIENT.getPrivateKey());

		JSONObject o = JSON.parseObject(decryptResult);
		System.out.println(o);
		String unionpayCollection = o.getString("unionpayCollection");
		System.out.println(unionpayCollection);
	}

	public static void queryTest() throws Exception {
		Map<String, String> data = new HashMap<String, String>();
		data.put("orderId", "20180525112741838");
		data.put("merchantID", "20");

		Map<String, String> reqData = new HashMap<String, String>();
		reqData.put("req", RSAKit.encrypt(JSON.toJSONString(data), CollAPIRSAKey.COLL_API.getPublicKey()));

		String host = "http://localhost:8082";
		// String host = "https://pac.mybank.cc";
		String result = HttpClient.send(host + "/coll/api/trade/q", reqData, HttpClient.UTF_8_ENCODING, 50000, 50000);

		System.out.println(result);
		String decryptResult = RSAKit.decrypt(result, CollAPIRSAKey.COLL_CLIENT.getPrivateKey());

		JSONObject o = JSON.parseObject(decryptResult);
		System.out.println(o);
		String unionpayCollection = o.getString("unionpayCollection");
		System.out.println(unionpayCollection);
	}

	public static void main(String[] args) throws Exception {
		//establishTest();
		tradeTest();
		 //queryTest();
	}
}
