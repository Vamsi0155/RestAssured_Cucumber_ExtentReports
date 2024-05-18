package stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.path.json.JsonPath;
import pojoClasses.ResponseLoader;
import utilities.BaseClass;

//import static io.restassured.RestAssured.*;

import java.io.FileNotFoundException;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import factory.Loader;


public class CommonSteps extends BaseClass {
	
	private static Logger logger = LogManager.getLogger(CommonSteps.class);
	
	
	@Given("add headres to the API with below details:")
	public void add_headres_to_the_api_with_below_details(DataTable dataTable) throws FileNotFoundException {
	    
		request.headers(Loader.loadTableValues(dataTable));
	}
	
	@Given("add query parameters to the API with below details:")
	public void add_query_parameters_to_the_api_with_below_details(DataTable dataTable) {
	    
		Map<String, String> map =Loader.updateTableWithGlobalValues(dataTable);
		addQueryParameters(map);
	}
	
	@When("the user calls {string} API with {string} request")
	public void the_user_calls_api_with_request(String apiName, String reqType) {
	    
		
		addAPIType(apiName, reqType);
	}
	
	@When("pass with body of {string} API below details:")
	public void pass_the_body_with_below_details(String apiName, DataTable dataTable) {
		
		Loader.loadInputValues(apiName, dataTable);
		addPayLoad(apiName);
		
	}
	
	@When("pass without body of {string} API below details:")
	public void pass_without_body_of_api_below_details(String api) {
		
		payLoad();
	}
	
	@Then("validate the status code {string}")
	public void validate_the_status_code(String status) {
		
	    int stat =Integer.parseInt(status);
	    validateStatusCode(stat);
	    
	}
	
	@Then("validation done with below details:")
	public void validation_done_with_below_details(DataTable dataTable) {
	    
		Map<String, String> map = Loader.loadTableValues(dataTable);
		
		JsonPath js =new JsonPath(response.getBody().asString());
		logger.info("Response: " + response.getBody().asPrettyString());
		
		map.forEach((key, value) -> {
		
		if(value.equals("NotNull") ) {
			
			Assert.assertNotNull((String)js.getJsonObject(key));
		}
		else if(value.equals("$NotNull")) {
			Assert.assertNotNull(js.getString(key));
			ResponseLoader.getGlobalValues().put(key, js.getJsonObject(key));
		}
		else
			Assert.assertEquals(value, js.getString(key));
		
		});
	}

}
