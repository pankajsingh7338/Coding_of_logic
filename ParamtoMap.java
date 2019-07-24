package com.actolap.wse.payment.service;

import java.util.HashMap;
import java.util.Map;

public class ParamToMap {
	// sample
	// mihpayid=403993715516227319&mode=&status=success&unmappedstatus=captured&key=UFu3ed&txnid=594a1e9ddd2ef11708a791f9&amount=3999.0&addedon=2017-06-21+12%3A52%3A42&productinfo=TRKAP-01&firstname=test&lastname=&address1=&address2=&city=&state=&country=&zipcode=&email=test%40kaptune.com&phone=9876543213&udf1=&udf2=&udf3=&udf4=&udf5=&udf6=&udf7=&udf8=&udf9=&udf10=&hash=a688c06faf7fdd51b1425df4f5e3e416b480b08350cc2f5b6df447546c427bf622c6c1e8f9f9010746e4e5913f971466bbe3e84a1dff87f30aeecaeddd55221a&field1=71726966821&field2=999999&field3=5233515521271721&field4=-1&field5=&field6=&field7=&field8=&field9=SUCCESS&PG_TYPE=HDFCPG&encryptedPaymentId=526E1633D6CB334FEDCEEA87AEBE5565&bank_ref_num=5233515521271721&bankcode=CC&error=E000&error_Message=No+Error&name_on_card=payu&cardnum=512345XXXXXX2346&cardhash=This+field+is+no+longer+supported+in+postback+params.&amount_split=%7B%22PAYU%22%3A%223999.0%22%7D&payuMoneyId=1111257054&discount=0.00&net_amount_debit=3999

	static final char AND = '&';
	static final char EQUAL = '=';

	public static Map<String, String> convert(String params) {
		return render(new HashMap<String, String>(), 0, params);
	}

	private static Map<String, String> render(Map<String, String> result, int index, String param) {

		int andIndex = param.indexOf(AND, index);
		int equalIndex = param.indexOf(EQUAL, index);
		if (equalIndex < 0 || andIndex < 0)
			return result;
		else {
			result.put(param.substring(index, equalIndex), param.substring(equalIndex + 1, andIndex));
			return render(result, andIndex + 1, param);
		}
	}
}

