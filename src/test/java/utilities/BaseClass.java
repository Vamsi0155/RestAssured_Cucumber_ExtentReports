package utilities;

import factory.Loader;
import factory.ReadConfigFiles;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BaseClass {

	private RequestSpecification requestSpec;
	private Response response;
	private static final Logger logger = LogManager.getLogger(BaseClass.class);

	public BaseClass(){
		this.initiateBaseURI();
	}

	private void initiateBaseURI() {
		try {
			RestAssured.baseURI=ReadConfigFiles.getConfigValue(ReadConfigFiles.getConfigValue("EnvironmentType"));
			requestSpec =new RequestSpecBuilder().build();
		} catch(Exception e) {
			logger.error("Error while initiating of Base config: {}", e.getMessage());
			throw e;
		}
	}

	public void addHeaders(Map<String, String> headers){
		try{
			if(headers!=null && !headers.isEmpty()){
				requestSpec.headers(headers);
				logger.info("List of header's: \n{}", headers);
			}
			else logger.error("");
		}
		catch (Exception e) {
			logger.error("error while adding of header's: {}", e.getMessage());
			throw e;
		}
	}

	public void addQueryParameters(Map<String, String> queryParams) {
		try{
			if(queryParams!=null && !queryParams.isEmpty()){
				requestSpec.queryParams(queryParams);
				logger.info("List of query param's: \n{}", queryParams);
			}
			else logger.error("");
		}
		catch (Exception e) {
			logger.error("error while adding of query param's: {}", e.getMessage());
			throw e;
		}
	}

	public void addPathParameters(Map<String, String> pathParams) {
		try{
			if(pathParams!=null && !pathParams.isEmpty()){
				requestSpec.pathParams(pathParams);
				logger.info("List of path param's: \n{}", pathParams);
			}
			else logger.error("");
		}
		catch (Exception e) {
			logger.error("error while adding of path param's: {}", e.getMessage());
			throw e;
		}
	}

	public void executeAPI(String apiName, String type, DataTable dataTable) {

		Loader.loadInputValues(apiName, dataTable);
		String reqPayLoad =createJsonInput(apiName);
		String endPoint = ReadConfigFiles.getApiEndpoint(apiName);

		try {
			if(endPoint!=null && !endPoint.isEmpty()) {
				logger.info("RestAPI url: {}{}", ReadConfigFiles.getConfigValue(ReadConfigFiles.getConfigValue("EnvironmentType")), endPoint);

				if(type.equals("GET")) {
					response = given().spec(requestSpec).body(reqPayLoad).when().get(endPoint).thenReturn();
				}
				else if(type.equals("POST")) {
					response = given().spec(requestSpec).body(reqPayLoad).when().post(endPoint).thenReturn();
				}
				else if(type.equals("PUT")) {
					response = given().spec(requestSpec).body(reqPayLoad).when().put(endPoint).thenReturn();
				}
				else if(type.equals("DELETE")) {
					response = given().spec(requestSpec).body(reqPayLoad).when().delete(endPoint).thenReturn();
				}
				else {
					throw new IllegalArgumentException("No method type found: " + type);
				}
			}
			else
				throw new IllegalArgumentException("No end-point found: " + endPoint);
			logger.info("Rest Request: \n{}", reqPayLoad);
		}
		catch(Exception e) {
			logger.error("Error while executing of API: {}", e.getMessage());
			throw e;
		}
	}

	public void executeAPI(String apiName, String type) {

		String endPoint = ReadConfigFiles.getApiEndpoint(apiName);
		try {
			if(endPoint!=null && !endPoint.isEmpty()) {
				logger.info("RestAPI url: {}{}", ReadConfigFiles.getConfigValue(ReadConfigFiles.getConfigValue("EnvironmentType")), endPoint);

				if(type.equals("GET")) {
					response = given().spec(requestSpec).when().get(endPoint).thenReturn();
				}
				else if(type.equals("POST")) {
					response = given().spec(requestSpec).when().post(endPoint).thenReturn();
				}
				else if(type.equals("PUT")) {
					response = given().spec(requestSpec).when().put(endPoint).thenReturn();
				}
				else if(type.equals("DELETE")) {
					response = given().spec(requestSpec).when().delete(endPoint).thenReturn();
				}
				else {
					throw new IllegalArgumentException("No method type found: " + type);
				}
			}
			else
				throw new IllegalArgumentException("No end-point found: " + endPoint);
		}
		catch(Exception e) {
			logger.error("Error while executing of API: {}", e.getMessage());
			throw e;
		}
	}

	public void multi_formData(DataTable dataTable){

		Map<String, String> formData = Loader.updateTableWithGlobalValues(dataTable);
		for (String key : formData.keySet()){
			if(key.contains("-file")){
				String key1 = key.split("-")[0];
				requestSpec.multiPart(key1, new File(formData.get(key)));
			}
			else
				requestSpec.multiPart(key, formData.get(key));
		}
		logger.info("List of form-data: \n{}", formData);
	}

	public String validateResponse(int stat) {

		if( response!= null) {
			logger.info("Response code: {}", response.getStatusCode());
			logger.info("Response: {}", response.getBody().asPrettyString());

			response.then().assertThat().statusCode(stat);
			return response.getBody().asString();
		}
		else
			throw new RuntimeException("No response found!!");
	}

	public String createJsonInput(String restAPI) {

		String reqTempFile ="./src/test/resources/requestTemplates/" + restAPI + "_Json.txt";

		String line, input="";

		try {
			FileReader file= new FileReader(reqTempFile);

			try (BufferedReader buffer = new BufferedReader(file)) {

				while((line = buffer.readLine()) != null) {

					if(!line.contains("{$")) {
						input = input + line + System.getProperty("line.separator");
					}
					else {
						logger.info(line);
						String part1 = line.split("\\{")[0];

						String part2;

						if((line.contains("$")) && (!line.contains(","))) {

							if(line.endsWith("\"")) {
								part2 = line.split("\\}")[1];
							}
							else
								part2 = "Dummy";
						}
						else
							part2 = line.split("\\}")[1];

						String valueName = line.split("\\{")[1].substring(1).split("\\}")[0];
						String value = Loader.getInputValue(valueName, restAPI);

						if(!value.isEmpty()) {

							if((part2.equals("Dummy")) && (line.contains("$"))) {
								input = input + part1 + value + System.getProperty("line.separator");
							}
							else
								input = input + part1 + value + part2 + System.getProperty("line.separator");
						}
					}
				}
			}
		}
		catch(IOException e) {
			logger.error("Error in formating request for Rest API: {}", e.getMessage());
		}

		return input;
	}
}
