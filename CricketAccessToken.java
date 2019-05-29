package com.actolap.wsegame.cricketAngularController;

import java.io.IOException; 
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate; 

import com.actolap.wsegames.response.AuthCricketResponse; 
import com.google.gson.Gson; 

@SessionAttributes({ "session" }) 
@Controller 
public class CricketAccessTokenController { 
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CricketAccessTokenController.class); 
	Gson gson = new Gson(); 
		
	    @ResponseBody 
	    @RequestMapping(value = "access/token", method = RequestMethod.POST) 
	    public AuthCricketResponse getAccessToken(HttpServletResponse response, HttpServletRequest request) throws IOException, JSONException { 
	        AuthCricketResponse authCricketResponse = new AuthCricketResponse(); 
	        String data = null; 
	        try { 
			RestTemplate restTemplate = new RestTemplate(); 
			String access_key = "dad8b9ae2092b4bf117bb07178d25f5d"; 
			String secret_key = "afc6d2a5c92d8ca0dca79421a33fa98f"; 
			String app_id = "abhijit@wsegames.com"; 
			String device_id = getSaltString(); 
			data = restTemplate.getForObject("https://rest.cricketapi.com/rest/v2/auth/" + "?access_key=" + access_key 
					+ "&secret_key=" + secret_key + "&app_id=" + app_id + "&device_id=" + device_id, String.class); 
			authCricketResponse = gson.fromJson(data, AuthCricketResponse.class); 
	        } catch (Exception e) { 
	            logger.info(e.getMessage(), e); 
	        } 
	        return authCricketResponse; 
	    } 
	      
	protected String getSaltString() { 
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; 
		StringBuilder salt = new StringBuilder(); 
		Random rnd = new Random(); 
		while (salt.length() < 18) { 
			int index = (int) (rnd.nextFloat() * SALTCHARS.length()); 
			salt.append(SALTCHARS.charAt(index)); 
		} 
		String saltStr = salt.toString(); 
		return saltStr; 
	} 
  
} 
  
  
