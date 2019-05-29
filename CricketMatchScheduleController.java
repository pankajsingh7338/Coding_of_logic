package com.actolap.wsegame.cricketAngularController;

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.zip.GZIPInputStream; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import org.apache.http.Header; 
import org.apache.http.HttpEntity; 
import org.apache.http.HttpResponse; 
import org.apache.http.NameValuePair; 
import org.apache.http.client.HttpClient; 
import org.apache.http.client.entity.UrlEncodedFormEntity; 
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.client.methods.HttpUriRequest; 
import org.apache.http.client.utils.URLEncodedUtils; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.message.BasicNameValuePair; 
import org.json.JSONException; 
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.ModelAttribute; 
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RequestMethod; 
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.ResponseBody; 
import org.springframework.web.bind.annotation.SessionAttributes; 
import org.springframework.web.bind.support.SessionStatus; 

import com.actolao.wsegmaes.api.ApiManager; 
import com.actolap.wsegame.common.Constants; 
import com.actolap.wsegame.common.FeUtils; 
import com.actolap.wsegame.interceptor.SessionWrapper; 
import com.actolap.wsegames.response.CricketMatchScheduleResponse; 
import com.actolap.wsegames.response.ObjectListResponse; 
import com.actolap.wsegames.response.SearchResponse; 
import com.google.gson.Gson; 

@SessionAttributes({ "session" }) 
@Controller 
public class CricketMatchScheduleController { 
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CricketMatchScheduleController.class); 
	Gson gson = new Gson(); 
	static String baseUrl = "https://rest.cricketapi.com/rest/"; 
	
	@SuppressWarnings({ "resource" }) 
	@ResponseBody 
	@RequestMapping(value = "ajx/cricketMatch/schedule", method = RequestMethod.GET) 
	public CricketMatchScheduleResponse getCricketMatchSchedule(HttpServletResponse response, 
			@RequestParam("accessToken") String accessToken, HttpServletRequest request) 
			throws IOException, JSONException { 
		CricketMatchScheduleResponse cricketMatchScheduleResponse = new CricketMatchScheduleResponse(); 
		String access_token = accessToken; 
		String data = null; 
		boolean isPost = false; 
		List<NameValuePair> params = new ArrayList<>(4); 
		params.add(new BasicNameValuePair("access_token", access_token)); 
		InputStream inputStream = null; 
		String result = ""; 
		try { 
			String newUrl = baseUrl + "v2/schedule/"; 
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpUriRequest uriRequest; 
			if (!isPost) { 
				String paramString = URLEncodedUtils.format(params, "utf-8");  
				uriRequest = new HttpGet(newUrl + '?' + paramString); 
			} else { 
				HttpPost postRequest = new HttpPost(newUrl); 
				HttpEntity requestParams = new UrlEncodedFormEntity(params);  
				postRequest.setEntity(requestParams); 
				uriRequest = postRequest; 
			} 
			  
			uriRequest.addHeader("Accept-Encoding", "gzip"); 
			HttpResponse httpResponse = httpclient.execute(uriRequest); 
			inputStream = httpResponse.getEntity().getContent(); 
			Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding"); 
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) { 
				inputStream = new GZIPInputStream(inputStream); 
			} 
			if (inputStream != null) { 
				result = convertInputStreamToString(inputStream); 
				cricketMatchScheduleResponse = gson.fromJson(result, CricketMatchScheduleResponse.class); 
			} else 
				result = "{\"status\":false, \"msg\": \"Error Reading\"}"; 
		} catch (Exception e) { 
			logger.info(e.getMessage(), e); 
		} 
		return cricketMatchScheduleResponse; 
	} 
	  
	private static String convertInputStreamToString(InputStream inputStream) throws IOException { 
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); 
		String line = ""; 
		String result = ""; 
		while ((line = bufferedReader.readLine()) != null) 
			result += line; 
		inputStream.close(); 
		return result; 
	} 
	  
	@RequestMapping(value = "ajx/cricketMatch/getContest/search/list", method = RequestMethod.GET) 
	@ResponseBody 
	public ObjectListResponse getMarketingReportSearchList(@RequestParam("query") String query, HttpServletResponse response, HttpServletRequest request,
			SessionStatus status, @ModelAttribute("session") SessionWrapper session) { 
		ObjectListResponse feListResponse = new ObjectListResponse(); 
		try { 
			SearchResponse beResponse = ApiManager.getMatchContestSearchList(query, FeUtils.createAPIMeta(session, request)); 
			if (beResponse != null) { 
				if (FeUtils.handleRepsonse(beResponse, request, status, response)) { 
					if (beResponse.isS()) { 
						for (Object object : beResponse.getSearchList()) 
							feListResponse.getResponse().add(object); 
						feListResponse.setStatus(Constants.SUCCESS); 
						feListResponse.setSuccessMsg(beResponse.getMsg()); 
					} else { 
						feListResponse.setStatus(Constants.FAILED); 
						feListResponse.setErrorDetails(beResponse.getEd()); 
					} 
				} 
			} else { 
				feListResponse.setStatus(Constants.NOT_RESPONEDING); 
				feListResponse.setErrorDetails(Constants.SERVER_IS_NOT_RESPONDING); 
			} 
		} catch (Exception e) { 
			logger.error(e.getMessage(), e); 
			feListResponse.setStatus(Constants.FAILED); 
			feListResponse.setErrorDetails(Constants.SOME_THING_WENT_WRONG); 
		} 
		return feListResponse; 
	} 
	 
}  
  
  
