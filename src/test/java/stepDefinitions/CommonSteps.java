package stepDefinitions;

import factory.Loader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import pojoClasses.ResponseLoader;
import utilities.BaseClass;

import java.util.Map;


public class CommonSteps {

	private static final Logger logger = LogManager.getLogger(CommonSteps.class);
	private final BaseClass restAPI;

	public CommonSteps(){
		restAPI = new BaseClass();
	}

	@Given("add headers to the API with below details:")
	public void add_headers_to_the_api_with_below_details(DataTable dataTable) {
		restAPI.addHeaders(Loader.updateTableWithGlobalValues(dataTable));
	}

	@Given("add query parameters to the API with below details:")
	public void add_query_parameters_to_the_api_with_below_details(DataTable dataTable) {
		restAPI.addQueryParameters(Loader.updateTableWithGlobalValues(dataTable));
	}

	@Given("add path parameters to the API with below details:")
	public void add_path_parameters_to_the_api_with_below_details(DataTable dataTable) {
		restAPI.addPathParameters(Loader.updateTableWithGlobalValues(dataTable));
	}

	@When("user calls {string} API with {string} request:")
	public void the_user_calls_api_with_request(String apiName, String apiMethod, DataTable dataTable) {
		restAPI.executeAPI(apiName, apiMethod, dataTable);
	}

	@When("user calls {string} API with {string} request")
	public void the_user_calls_api_with_request(String apiName, String apiMethod) {
		restAPI.executeAPI(apiName, apiMethod);
	}

	@When("user calls {string} API with {string} request using form-data:")
	public void the_user_calls_api_with_request_using_formData(String apiName, String apiMethod, DataTable dataTable) {
		restAPI.multi_formData(dataTable);
		restAPI.executeAPI(apiName, apiMethod);
	}

	@Then("validate the status {string} with below details:")
	public void validation_done_with_below_details(String status, DataTable dataTable) {

		try{
			Map<String, String> map = Loader.loadTableValues(dataTable);
			JsonPath js =new JsonPath(restAPI.validateResponse(Integer.parseInt(status)));

			for (String key: map.keySet()){
				if(map.get(key).equals("NotNull")){
					Assert.assertNotNull("Value for key '" + key + "' is null!", js.getJsonObject(key));
				}
				else if(map.get(key).equals("NotNull#")) {
					Assert.assertNotNull("Value for key '" + key + "' is null!", js.getJsonObject(key));
					ResponseLoader.getGlobalValues().put(key, js.getJsonObject(key));
				}
				else
					Assert.assertEquals(map.get(key), js.get(key));
			}
		}
		catch (Exception e){
			logger.error("Error while validating of response: {}", e.getMessage());
			throw e;
		}
	}

}
