package utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import factory.Loader;
import factory.ReadConfigFiles;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class BaseClass {

	static ReadConfigFiles config= new ReadConfigFiles();
	
	public static RequestSpecification request;
	public static ResponseSpecification respSpec;
	public static Response response;
	
	private static Logger logger = LogManager.getLogger(BaseClass.class);
	
	public static void initiateBaseURL(PrintStream logs) throws FileNotFoundException {
		
		try {
			request=null;
			if(request==null) {
				
				RestAssured.baseURI=ReadConfigFiles.config.getProperty(ReadConfigFiles.config.getProperty("EnvironmentType"));
				
				request =new RequestSpecBuilder().setContentType(ContentType.JSON).build();
						//.addFilter(RequestLoggingFilter.logRequestTo(logs))
						//.addFilter(ResponseLoggingFilter.logResponseTo(logs))
				
			}
		} catch(Exception e) {
			logger.error("Error while initiating of Base config: ", e.getMessage());
		}
		
		
	}
	
	public void addQueryParameters(Map<String, String> map) {
		
		try {
			if(request != null) {
				request.queryParams(map);
			}
		} catch(Exception e) {
			logger.error("Error while adding of Query param's", e.getMessage());
		}
	}
	
	public void addPathParameters(Map<String, String> map) {
		
		try {
			if(request != null) {
				request.pathParams(map);
			}
		} catch(Exception e) {
			logger.error("Error while adding of Path param's", e.getMessage());
		}
	}
	
	public void addParameters(Map<String, String> map) {
		
		try {
			if(request != null) {
				request.params(map);
			}
		} catch(Exception e) {
			logger.error("Error while adding of Param's", e.getMessage());
		}
	}
	
	public static void addAPIType(String api, String type) {
		
		try {
			if(request != null) {
				
				String endPoint = ReadConfigFiles.apiEndpoints.getProperty(api);
				if(!endPoint.isEmpty()) {
					
					logger.info("RestAPI url: " + ReadConfigFiles.config.getProperty(ReadConfigFiles.config.getProperty("EnvironmentType")) + endPoint );
					
					if(type.equals("GET")) {
						response = request.when().get(endPoint).thenReturn();
					}
					else if(type.equals("POST")) {
						response = request.when().post(endPoint).thenReturn();
					}
					else if(type.equals("PUT")) {
						response = request.when().put(endPoint).thenReturn();
					}
					else if(type.equals("DELETE")) {
						response = request.when().delete(endPoint).thenReturn();
					}
					else
						logger.error("No method type is found");
				}
				else 
					logger.error("Invalid Rest URI found");
			}
		}
		catch(Exception e) { 
			logger.error("Error while adding of API type", e.getMessage());
		}
	}
	
	public static void addPayLoad(String apiName) {
		
		String body =createJsonInput(apiName);
		try {
			if(!body.isEmpty()) {
				request = given().spec(request).body(body);
				logger.info("Rest Request: " + body);
			}
			else
				logger.error("Empty request body found");
		}
		catch(Exception e) {
			logger.error("Error while adding of payload: ", e.getMessage());
		}
	}
	
	public static void payLoad() {
		
		try {
			if(request != null) {
				request = given().spec(request);
			}
			else
				logger.error("Empty request found");
		}
		catch(Exception e) {
			logger.error("Error while adding of payload: ", e.getMessage());
		}
	}
	
	public static void validateStatusCode(int stat) {
		
		if( response != null) {
			logger.info("Response code: " + response.getStatusCode());
			response.then().assertThat().statusCode(stat);
		}
		else 
			logger.error("response object is empty");
	}
	
	public String createInputJsonFile(String service) {
		
		String reqTempFile ="./src/test/resources/requestTemplates/" + service + "_Json.txt";
		
		StringBuilder modifiedContent = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(reqTempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line based on '{$'
                String[] parts = line.split("\\{\\$");

                if (parts.length > 1) {
                    // Process each part
                    modifiedContent.append(parts[0]);  // Append the first part before '{$'

                    for (int i = 1; i < parts.length; i++) {
                        // Find the closing '}' to extract the variable name
                        int endIndex = parts[i].indexOf('}');
                        if (endIndex != -1) {
                            String key = parts[i].substring(0, endIndex);
                            // Replace the keyValue with the corresponding Java value
                            String value=Loader.getInputValue(key, service);
                            if(!value.isEmpty()) {
                            	modifiedContent.append(value);
                            }
                            else {
                            	modifiedContent.append("{$").append(key).append("}");
                            	break;
                            }

                            // Append the rest of the part after '}'
                            modifiedContent.append(parts[i].substring(endIndex + 1));
                        } else {
                            // If no closing '}' is found, append the original "{$" + parts[i]
                            modifiedContent.append("{$").append(parts[i]);
                        }
                    }
                } 
                else {
                    // If no '{$' is found in the line, append the original line
                    modifiedContent.append(line);
                }
                // Move to the next line
                modifiedContent.append(System.lineSeparator());
            }
            
        }
        catch (IOException e) {
            logger.error("Error in formating request for Rest API: ", e.getMessage());
        }

        return modifiedContent.toString();
	}
	
	public static String createJsonInput(String restAPI) {
		
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
            logger.error("Error in formating request for Rest API: ", e.getMessage());
		}
		
		return input;
	}
}
