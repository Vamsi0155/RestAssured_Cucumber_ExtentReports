package utilities;

import java.io.FileInputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import factory.ReadConfigFiles;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;

public class MyCustomListener implements EventListener {
	
	private static final Logger logger =LogManager.getLogger(MyCustomListener.class);
	
	String exampleCount;
	static int count;
	
	private static String startTime;
	private static String failedStepLine;
	private static int failedStepNum;
	
	
	
	@Override
	public void setEventPublisher(EventPublisher publisher) {
		
		publisher.registerHandlerFor(TestCaseStarted.class, this::onScenarioStarted);
		publisher.registerHandlerFor(TestStepStarted.class, this::onStepStarted);
		publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
		publisher.registerHandlerFor(TestCaseFinished.class, this::onScenarioFinished);
	}
	
	public void onScenarioStarted(TestCaseStarted test) {
		
		startTime = null;
		test.getInstant();
		Instant.now().atZone(ZoneId.systemDefault());
		startTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'"));
	}
	
	public void onStepStarted(TestStepStarted step) {
		
	}
	
	public void onStepFinished(TestStepFinished step) {
		
		if(step.getResult().getStatus().isOk() == false) {
			failedStepLine = null;
			PickleStepTestStep pickle = (PickleStepTestStep)step.getTestStep();
			Step st = pickle.getStep();
			failedStepLine = st.getText();
			failedStepNum = st.getLine();
			System.out.println("Failed at line: " + failedStepNum + ", And Line: " + failedStepLine);
		}
	}
	
	
	public void onScenarioFinished(TestCaseFinished test) {
		
		
		this.writeRunDetailsToDB(test, test.getTestCase().getUri().getPath());
	}
	
	
	private void writeRunDetailsToDB(TestCaseFinished test, String fullPath) {
		
		String scenarioPath = fullPath.substring(fullPath.indexOf("features"));
		test.getInstant();
		String runDateTime = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println("Time: " + runDateTime);
		logger.info("Test finished outcome:: Duration:" + test.getResult().getDuration().getSeconds() + ", Result:" + test.getResult().getStatus().toString() + ", Feature:" + scenarioPath + ", StartTime:" +startTime + ", TestFailedStep:" + failedStepNum + ", TestFailedStepLine:" + failedStepLine + ", FailureCause:" + test.getResult().getError());
		
		String analyticsReq = ReadConfigFiles.config.getProperty("AnalyticsRequired");
		if(analyticsReq != null && analyticsReq.equals("Y")) {
			
			try {
				// Disabled below code becz not configured DB details.
				/*
				Properties analyticsData = new Properties();
				FileInputStream input = new FileInputStream("AutoAnalytics.properties");
				analyticsData.load(input);
				
				String dbAlias = analyticsData.getProperty("DatabaseAlias");
				String dbType = analyticsData.getProperty("DataBaseType");
				String url = analyticsData.getProperty("DatabaseURL");
				String table = analyticsData.getProperty("tableName");
				
				DriverManager.registerDriver((Driver)getClass().forName(analyticsData.getProperty("JDBCDriver")).getDeclaredConstructor().newInstance());
				//new Properties();
				
				String query = "";
				PreparedStatement pstmt = null;
				Connection dbConnection = DriverManager.getConnection(url, analyticsData.getProperty("DatabaseUserName"), analyticsData.getProperty("DatabasePassword"));
				String failureReason = "";
				
				if(failedStepLine != null) {
					failureReason = failedStepLine;
				}
				if(test.getResult().getError().toString() != null) {
					failureReason = "Failed at " + failedStepNum + "step line of " + failureReason + " : " + test.getResult().getError().toString();
				}
				
				if(dbType.equalsIgnoreCase("Oracle") || dbType.equalsIgnoreCase("PostgreSQL")) {
					
					try {
						
						if(failureReason.equals("")) {
							failureReason = "null";
						}
						else {
							failureReason = "'" + failureReason + "'";
						}
						
						query = "INSERT INTO " + dbAlias + "." + table + " (RUN_NUMBER, RUN_PROFILE, RUN_FEATURE_PATH, RUN_DATETIME, RUN_DURATION, RUN_SCENARIO, RUN_FEATURE, RUN_MODULE, RUN_STEPS, RUN_STAT, RUN_FAILURE_REASON) VALUES ( 001, '" + analyticsData.getProperty("Run_Region") + "' , '" + scenarioPath + "', TO_DATE('" + runDateTime + "', 'YYYY-MM-DD HH24:MI:SS'), " + test.getResult().getDuration().getSeconds() + ", '" + test.getTestCase().getName() + "', '" + scenarioPath.split("/")[2] + "', '" + scenarioPath.split("/")[1] + "', " + test.getTestCase().getTestSteps().size() + ", '" + test.getResult().getStatus().toString() + "', '" + failureReason + "')";
						logger.info(table + "Insert query: " + query);
						pstmt = dbConnection != null ? dbConnection.prepareStatement(query) : null;
						pstmt.executeQuery();
						
					}
					catch(Throwable var2) {
						var2.printStackTrace();
					}
					finally {
						pstmt.close();
						dbConnection.close();
					}
				}
				
				if(dbType.equalsIgnoreCase("DB2")) {
					
					// write code on same way.
				}
				*/
			}
			catch(Exception var3) {
				logger.error("Error in executing DB query method ", var3);
				logger.error("Error in connecting to DB in execute query method");
			}
		}
		
	}
	
	
	public static void getTestStartedLog(String scenarioName, String path) {
            
		String testStarted =
             "\n _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                "|_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                "  | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                "  | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                "  |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n" +
                "\n" +
                "TEST STARTED: " + scenarioName + " \n" +
                "----------------------------------------------------------------------("+ path + "; " + scenarioName;
		
		logger.info(testStarted);
	}
	
	
	public static void getTestStatusLog(String scenarioName, String status) {
		
		String testPassed=
            "\n        __    _____ _____ ____ _____   ____   _    ____  ____  _____ ____  \n" +
                    "  _     \\ \\  |_   _| ____/ ___|_   _| |  _ \\ / \\  / ___|/ ___|| ____|  _ \\ \n" +
                    " (_)_____| |   | | |  _| \\___ \\ | |   | |_) / _ \\ \\___ \\\\___ \\|  _| | | | |\n" +
                    "  _|_____| |   | | | |___ ___) || |   |  __/ ___ \\ ___) |___) | |___| |_| |\n" +
                    " (_)     | |   |_| |_____|____/ |_|   |_| /_/   \\_\\____/|____/|_____|____/ \n" +
                    "        /_/                                                                \n" +
                    "\n" +
                    "TEST PASSED: " + scenarioName + " \n" +
                    "----------------------------------------------------------------------";
		
		
		
		String testFailed =
	            "\n           __  _____ _____ ____ _____   _____ _    ___ _     _____ ____  \n" +
	                    "  _       / / |_   _| ____/ ___|_   _| |  ___/ \\  |_ _| |   | ____|  _ \\ \n" +
	                    " (_)_____| |    | | |  _| \\___ \\ | |   | |_ / _ \\  | || |   |  _| | | | |\n" +
	                    "  _|_____| |    | | | |___ ___) || |   |  _/ ___ \\ | || |___| |___| |_| |\n" +
	                    " (_)     | |    |_| |_____|____/ |_|   |_|/_/   \\_\\___|_____|_____|____/ \n" +
	                    "          \\_\\                                                            \n" +
	                    "\n"+
	                    "TEST FAILED: " + scenarioName + " \n" +
	                    "----------------------------------------------------------------------";

	    
	                    
	    String testError =
	            "\n         __  _____ _____ ____ _____   _____ ____  ____   ___  ____  \n" +
	                    " _      / / |_   _| ____/ ___|_   _| | ____|  _ \\|  _ \\ / _ \\|  _ \\ \n" +
	                    "(_)____| |    | | |  _| \\___ \\ | |   |  _| | |_) | |_) | | | | |_) |\n" +
	                    " |_____| |    | | | |___ ___) || |   | |___|  _ <|  _ <| |_| |  _ < \n" +
	                    "(_)    | |    |_| |_____|____/ |_|   |_____|_| \\_\\_| \\_\\\\___/|_| \\_\\\n" +
	                    "        \\_\\                                                         \n" +
	                    "\n" +
	                    "TEST ERROR: " + scenarioName + " \n" +
	                    "----------------------------------------------------------------------";
		
	    if(status.equals("PASSED")) {
	    	logger.info(testPassed);
	    }
	    else if(status.equals("FAILED")) {
	    	logger.info(testFailed);
	    }
	    else if(status.equals("ERROR")) {
	    	logger.info(testError);
	    }
	}


}
