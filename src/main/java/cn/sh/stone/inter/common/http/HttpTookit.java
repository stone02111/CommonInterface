/**
 * 
 */
package cn.sh.stone.inter.common.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @作者 Stone
 * @创建日期 2013-10-22 下午03:48:01
 * @版本 V1.0
 * @类说明	Http工具箱
 */
public final class HttpTookit {
	private static Log log = LogFactory.getLog(HttpTookit.class);

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * @param url 请求的URL地址
	 * @param queryString 请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String sendGet(String url, String queryString) {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			if (StringUtils.isNotBlank(queryString))
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (URIException e) {
			log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e);
		} catch (IOException e) {
			log.error("执行HTTP Get请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * @param url 请求的URL地址
	 * @param params 请求的查询参数,可以为null
	 * @return 返回请求响应的HTML
	 */
	public static String sendPost(String url, Map<String, String> params) {
		String response = null;
		HttpClient client = new HttpClient();
		HttpMethod method = new PostMethod(url);
//		for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
//
//		}
		// 设置Http Post数据
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (IOException e) {
			log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}

		return response;
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * @param url 请求的URL地址
	 * @param xml 请求的查询参数
	 * @return
	 */
	public static String sendPostXml(String url, String xml) {
		String response = null;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);

		try {
			// 设置请求的内容
			RequestEntity requestEntity = new StringRequestEntity(xml, "text/xml", "UTF-8");
			method.setRequestEntity(requestEntity);

			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			}
		} catch (IOException e) {
			log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	public static String sendPostMap(String url, Map<String, String> params) {
		String response = null;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		try {
			// 设置请求的内容
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					NameValuePair nvp = new NameValuePair();
					nvp.setName(entry.getKey());
					nvp.setValue(URIUtil.encode(entry.getValue(), null));
					list.add(nvp);
				}
				method.setRequestBody(list.toArray(new NameValuePair[list.size()]));
			}
			client.executeMethod(method);

			InputStream stream = null;

			switch (method.getStatusCode()) {
			case HttpStatus.SC_OK:
				// 处理中文等
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				stream = method.getResponseBodyAsStream();
				
				int bytesRead = 0;
				int buffLen = 1024;
				byte[] buffer = new byte[1024];
				while ((bytesRead = stream.read(buffer, 0, buffLen)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				response = outStream.toString("UTF-8");
				stream.close();
				break;
			default:
				response = method.getResponseBodyAsString();
				break;
			}
		} catch (IOException e) {
			log.error("执行HTTP Post请求" + url + "时，发生异常！", e);
		} finally {
			method.releaseConnection();
		}
		return response;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		// 测试GET请求
		String s = sendGet("http://qicheng.sinaapp.com/?", "key=你好");
		System.out.println(s);

		// 测试post MAP参数
		Map<String, String> map = new HashMap<String, String>();
		String xml = "<xml>" + " <ToUserName><![CDATA[toUser]]></ToUserName>"
				+ " <FromUserName><![CDATA[fromUser]]></FromUserName> "
				+ " <CreateTime>1348831860</CreateTime>"
				+ " <MsgType><![CDATA[text]]></MsgType>"
				+ " <Content><![CDATA[3]]></Content>"
				+ " <MsgId>1234567890123456</MsgId>" + " </xml>";
		map.put("para", "白毛浮绿水");
		String x = sendPostMap("http://www.xiaohuangji.com/ajax.php", map);
		System.out.println(x);

		// 测试post XML
		String y = sendPostXml(
				"http://114chahao.duapp.com/wechat/2lm1Jxd8zMJr", xml);
		System.out.println(y);
	}
}
