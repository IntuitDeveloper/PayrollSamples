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
public class EmployeeCompensationWithFilter {
		
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EmployeeCompensationWithFilter.class);
	
	public static void main (String[] args) throws Exception {
		
		//Read graphql request from file
		String fileString = FileReader.readFileToString("employeeCompensationFilter.json");
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
					  .getJSONObject("employeeContacts")
					  .getJSONArray("edges");

			//Retrieving data for the first employee (edge)
			JSONObject jObj = jsonArray.getJSONObject(0);
			JSONArray jsonArrayCompensation = jObj.getJSONObject("node")
											.getJSONObject("profiles")
											.getJSONObject("employee")
											.getJSONObject("compensations")
											.getJSONArray("edges");
			
			//Retrieving data for the first employeeCompensation - this can be modified to iterate and access information for all employeeCompensations returned back in the json
			JSONObject compenseation = jsonArrayCompensation.getJSONObject(0);
			String id = compenseation.getJSONObject("node").getString("id");
			String name = compenseation.getJSONObject("node").getJSONObject("employerCompensation").getString("name");
			LOG.info("employer compensation name:" + name);
			LOG.info("employee compensation id :" + id);		

		} else {
			throw new Exception("Error returned in JSON response");
		}
		
		
		
	}
	

}
