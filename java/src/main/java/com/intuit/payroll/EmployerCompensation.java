package com.intuit.payroll;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.intuit.helper.FileReader;
import com.intuit.service.QBOService;

/**
 * @author Diana De Rose
 *
 */
public class EmployerCompensation {
	
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EmployerCompensation.class);
	
	public static void main (String[] args) throws Exception {
		
		//Read graphql request from file
		String fileString = FileReader.readFileToString("employerCompensation.json");
		String graphqlRequest = new JSONObject()
                .put("query",fileString)
               // .put("variables", variables) //TODO add variables if applicable
                .toString();
		LOG.info("input json::" + graphqlRequest);

		//Call the API
		String result = QBOService.callAPI(graphqlRequest);
		
		//Parse the json and retrive id 
		JSONObject jsonObj = new JSONObject(result);
		
		// check if error is returned
		if(jsonObj.has("data") && !jsonObj.has("errors") ) {
			JSONArray jsonArray = jsonObj.getJSONObject("data")
								  .getJSONObject("company")
								  .getJSONObject("companyInfo")
								  .getJSONObject("employerInfo")
								  .getJSONObject("compensations")
								  .getJSONArray("edges");
			
			//Retrieving data for the first employerCompensation - this can be modified to iterate and access information for all employerCompensations returned back in the json
			JSONObject jObj = jsonArray.getJSONObject(0);
			String name = jObj.getJSONObject("node").getString("name");
			String id = jObj.getJSONObject("node").getString("id");
			LOG.info("employer compensation name:" + name);
			LOG.info("employer compensation id :" + id);	
		} else {
			throw new Exception("Error returned in JSON response");
		}
		
	}


}
