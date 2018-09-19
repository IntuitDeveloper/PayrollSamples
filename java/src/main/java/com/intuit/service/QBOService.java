package com.intuit.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 * @author Diana De Rose
 *
 */
public final class QBOService {
	
	private QBOService() {}
	
	private static final String url = "https://v4thirdparty.api.intuit.com/graphql";
	private static final String accessToken = "add oauth2 access token";
	private static final CloseableHttpClient CLIENT = HttpClientBuilder.create().build();
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(QBOService.class);

	
	/**
	 * Abstract the V4 API call
	 * @param json
	 * @return
	 */
	public static String callAPI(String json) {
		HttpPost post = new HttpPost(url);
		
		//add header
		post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("provider_override_scheme", "payroll");
        post.setHeader("Authorization", "Bearer " + accessToken);
        
        LOG.info(json);
        HttpEntity entity = new StringEntity(json, "UTF-8");
	    post.setEntity(entity);
	    
	    try {
			HttpResponse response = CLIENT.execute(post);			
			LOG.info("Response status:::" + response.getStatusLine().toString());
			
			if (response.getStatusLine().getStatusCode() == 200){
				String result = getResult(response).toString();
				LOG.info(result);
				return result;
            } else {
            	LOG.info("failed calling API");
                return new JSONObject().put("response","error calling API").toString();
            }
		    
		} catch (Exception e) {
			LOG.error("Error calling API", e.getCause());
			return new JSONObject().put("response","error calling API").toString();
		}
	}
	
	/**
	 * Retrieves the response string
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private static StringBuffer getResult(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
		    result.append(line);
		}
		return result;
	}

}
