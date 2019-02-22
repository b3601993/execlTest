package sign;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


public class GetSign {

	private static final String CONTENT_CHARSET = "UTF-8"; // ���뷽ʽ
    private static final String HMAC_ALGORITHM = "HmacSHA1"; // HMAC�㷨
	
	public static void main(String[] args) {
		
		long timeStamp = Long.valueOf(args[2]);
//		long timeStamp = System.currentTimeMillis()/1000;
		
		
		String sign = getSign(args[0], args[1], timeStamp, new HashMap<String, String>(), args[3], "GET");
		System.out.println(sign);
	}
	
	private static String getSign(String appKey, String appSecret, Long timeStamp, Map<String, String> params, String url, String requestMethod) {
        Map paramsMap = new HashMap();
        if (params != null) {
        	paramsMap.putAll(params);
        }
        paramsMap.put("app_key", appKey);
        paramsMap.put("time_stamp", timeStamp.toString());
        paramsMap.remove("sign");
        codePayValue(paramsMap);
        try {
            return makeSign(requestMethod, url, paramsMap, appSecret);
        } catch (Exception e) {
        }
        return null;
    }
	
	
	/**
     * ����ǩ��
     *
     * @param method HTTP���󷽷� "get" / "post"
     * @param url_path ��Դ, eg: /v1/user/get_info
     * @param params URL�������
     * @param secret ��Կ
     * @return ǩ��ֵ
     */
    public static String makeSign(String method, String urlPath, Map<String, String> params, String secret) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac.getAlgorithm());
        mac.init(secretKey);
        String mk = makeSource(method, urlPath, params);
        byte[] hash = mac.doFinal(mk.getBytes(CONTENT_CHARSET));
        // �����ܺ���ַ�������Base64����
        return new String(Base64Coder.encode(hash));
    }
	
    
    /**
     * ����ǩ������Դ��
     *
     * @param method HTTP���󷽷� "get" / "post"
     * @param urlPath CGI����, eg: /v3/user/get_info
     * @param params URL�������
     * @return ǩ������Դ��
     */
    public static String makeSource(String method, String urlPath, Map<String, String> params) throws Exception {
        /*
          1.�������URI·������URL����
          2.������sign��������в�����key�����ֵ���������
          3.����2���������Ĳ���(key=value)��&ƴ������,����URL����
          4.��HTTP����ʽ��GET����POST���Լ���1���͵�3���е��ַ�����&ƴ��������
          5 ��1���Լ���3���еĵ����ַ�����&ƴ���������õ�Դ��
        */
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(method.toUpperCase()).append("&").append(encodeUrl(urlPath)).append("&");
        StringBuilder buffer2 = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
        	if (params.get(keys[i]) == null) {
        		continue;
        	}
            buffer2.append(keys[i]).append("=").append(params.get(keys[i]));
            if (i != keys.length - 1) {
                buffer2.append("&");
            }
        }
        buffer.append(encodeUrl(buffer2.toString()));
        return buffer.toString();
    }
    
    /**
     * URL���� (����FRC1738�淶)
     *
     * @param input ��������ַ���
     * @return �������ַ���
     */
    public static String encodeUrl(String input) throws Exception {
        return URLEncoder.encode(input, CONTENT_CHARSET).replace("+", "%20").replace("*", "%2A");
    }
	
	/**
     * �Բ���valueֵ�Ƚ���һ�α��뷽����������ǩ
     * (�������Ϊ������ 0~9 a~z A~Z !*() ֮�������ַ�����ASCII���ʮ�����Ƽ�%���б�ʾ�����硰-������Ϊ��%2D��)
     * �ο� <�ص�����URL��Э��˵��_V3>
     *
     * @param params �ص�����Map (key,value);
     */
    public static void codePayValue(Map<String, String> params) {
        Set<String> keySet = params.keySet();
        Iterator<String> itr = keySet.iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = params.get(key);
            value = encodeValue(value);
            params.put(key, value);
        }
    }
    
    /**
     * �������
     *
     * @param s
     * @return
     */
    public static String encodeValue(String s) {
        String rexp = "[0-9a-zA-Z!*\\(\\)]";
        StringBuffer sb = new StringBuffer(s);
        StringBuffer sbRtn = new StringBuffer();
        Pattern p = Pattern.compile(rexp);
        char temp;
        String tempStr;
        for (int i = 0; i < sb.length(); i++) {
            temp = sb.charAt(i);
            tempStr = String.valueOf(temp);
            Matcher m = p.matcher(tempStr);
            boolean result = m.find();
            if (!result) {
                tempStr = hexString(temp);
            }
            sbRtn.append(tempStr);
        }
        return sbRtn.toString();
    }
    
    /**
     * URL��ʮ�����Ʊ���
     *
     * @param s
     * @return
     */
	private static String hexString(char s) {
		String d = Integer.toBinaryString(s);
		if (7 < d.length()) {
			d = "10" + d.substring(d.length() - 6);
		}
		String hex = Integer.toString(Integer.parseInt(d, 2), 16).toUpperCase();
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return "%" + hex;
	}
}
