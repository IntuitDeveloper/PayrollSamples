package com.intuit.payroll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 * @author Diana De Rose
 *
 */
public class EmployeeCompensationWithFilter {
	
	public static final String url = "https://v4thirdparty-e2e.api.intuit.com/graphql";
	public static final String accessToken = "eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..WqG3YPq60s6HI80_2SibOA.P_hWUB8qMaoXVglx3Q4g4Sln3B_6B_fGli30CzrRgfhUZlNcapF7LxyQqrP38Fosi-l-7pFCLrRCUiOiDzcpSbwrMX496LGn5KSnC4RfuutFgsAR2IWaq6HTUYwKbx6cHiFmbtEnNhDx2cwUC64hrllxu7RizgYv005sTb64Qr0k-fbU9F_YN-k3xUU8O-WX6FBx4ZHLrjCvjRSAiQuR5PE0B7Lbvwy6rL1KIpOVZp4qshsY6iZpt_vCwH9L_rZG4itktZfPf9yKRzp4veyIEjw83_56H5RxURJIrEdZP8CBNL5JrNijFfW5OmUanuFvABcl6zfYt-xA3vYwLUyj60J6AnT2df-tzszcJZCkpf0Tj6ZaTQu9mfrzFGTiTwdOpO8aJmZ33rVNGthm2S3xQHldTBc2N76mcrwOCifOKsVOHWIhGLA6GPS7DEL3bFiI47bzBVpRZ5t-E-i_JEGsWP_95DXmZuXlkbuDTz3xnsG24ZaJ9zxrRIEodKAa_RjvYvAF01KRvARob3MTcoQFVqwpA0MBHsC6u-SkenfB959nicENckOVkbbzLKKjpVLFdC-j4gqu9pM4OO0Gr7tYNoWL4EedBNBJHJApyOOZ-p39lwkg8LAxiF_bTxWdG3dtaIh7aY9Ie_IIhfIzXnWQ2d2MZESVafihNmaOhuz0yXWycpF3SaDiREy6TjP0Yud2.oVViUF7wPo8aoYp45Hop9g";
	private static final CloseableHttpClient CLIENT = HttpClientBuilder.create().build();
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EmployerCompensation.class);
	
	public static void main (String[] args) {
		
		//Read graphql request from file
		String fileString = readFileToString("employeeCompensationFilter.json");
		LOG.info("input json" + fileString);

		//Call the API
		String result = callAPI(fileString);
		
		//Parse the json and retrive id 
		JSONObject jsonObj = new JSONObject(result);
		//TODO Add null check and error handling
		JSONArray jsonArray = jsonObj.getJSONObject("data")
							  .getJSONObject("company")
							  .getJSONObject("employeeContacts")
							  .getJSONArray("edges");
		
		JSONObject jObj = jsonArray.getJSONObject(0);
		JSONArray jsonArrayCompensation = jObj.getJSONObject("node").getJSONObject("profiles").getJSONObject("employee").getJSONObject("compensations").getJSONArray("edges");
		
		JSONObject compenseation = jsonArrayCompensation.getJSONObject(0);
		String id = compenseation.getJSONObject("node").getString("id");
		String name = compenseation.getJSONObject("node").getJSONObject("employerCompensation").getString("name");
		LOG.info("employer compensation name:" + name);
		LOG.info("employee compensation id :" + id);		
		
	}

	/**
	 * Abstract the V4 API call
	 * @param json
	 * @return
	 */
	private static String callAPI(String json) {
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
	public static StringBuffer getResult(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
		    result.append(line);
		}
		return result;
	}
	
    public static String readFileToString(String filePath){
        return getStringFromInputStream(EmployerCompensation.class.getClassLoader().getResourceAsStream(filePath));
    }
	
	/**
	 * Read contents of the file into a string
	 * @param is
	 * @return
	 */
	public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
