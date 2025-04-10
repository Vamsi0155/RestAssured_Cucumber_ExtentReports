package utilities;

import factory.DBConnection;
import factory.Loader;
import factory.ReadConfigFiles;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojoClasses.ResponseLoader;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MyCustomListener implements EventListener {

	private static final Logger logger =LogManager.getLogger(MyCustomListener.class);

	private static String startTime;
	private static String failedStepLine;
	private static int failedStepNum;



	@Override
	public void setEventPublisher(EventPublisher publisher) {

		publisher.registerHandlerFor(TestRunStarted.class, this::onTestRunStarted);
		publisher.registerHandlerFor(TestSourceRead.class, this::onFeatureStarted);
		publisher.registerHandlerFor(TestSourceParsed.class, this::onFeatureFinished);
		publisher.registerHandlerFor(TestCaseStarted.class, this::onScenarioStarted);
		publisher.registerHandlerFor(TestStepStarted.class, this::onStepStarted);
		publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
		publisher.registerHandlerFor(TestCaseFinished.class, this::onScenarioFinished);
		publisher.registerHandlerFor(TestRunFinished.class, this::onTestRunFinished);
	}

	public void onTestRunStarted(TestRunStarted run){
		logger.info(MyCustomListener.setAutomationSuiteLog());
		String formattedFinishTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(run.getInstant());
		logger.info("Automation Suite Started at: {}", formattedFinishTime);
	}

	public void onFeatureStarted(TestSourceRead read){
		Loader.clearLoadInputValues();
		ResponseLoader.clearGlobalValues();
		logger.info("Global & Input values are cleared before feature started: {}", Paths.get(read.getUri()).getFileName().toString());
	}

	public void onFeatureFinished(TestSourceParsed read){

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

		if(!step.getResult().getStatus().isOk()) {
			failedStepLine = null;
			PickleStepTestStep pickle = (PickleStepTestStep)step.getTestStep();
			Step st = pickle.getStep();
			failedStepLine = st.getText();
			failedStepNum = st.getLine();
			System.out.println("Failed at line: " + failedStepNum + ", And Line: " + failedStepLine);
		}
	}

	public void onScenarioFinished(TestCaseFinished test) {
		this.writeRunDetailsToDB(test);
	}

	public void onTestRunFinished(TestRunFinished run){
		Result result = run.getResult();
		String formattedFinishTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(run.getInstant());
		logger.info("Automation Suite Finished at: {}", formattedFinishTime);
		logger.info("Run Duration: {}h {}m {}s", result.getDuration().toHours(), result.getDuration().toMinutes(), result.getDuration().toSeconds());
		logger.info("Run status: {}", result.getStatus().toString());
	}


	private void writeRunDetailsToDB(TestCaseFinished test) {

		String featureFile = Paths.get(test.getTestCase().getUri()).getFileName().toString();
		String featurePath = test.getTestCase().getUri().getPath();
		String moduleName = featurePath.substring(featurePath.indexOf("features")).split("/")[1];
		String runDate = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		test.getInstant();
		String runDateTime = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println("Time: " + runDateTime);
		logger.info("Test finished outcome:: Duration:{}, Result:{}, Feature:{}, StartTime:{}, TestFailedStep:{}, TestFailedStepLine:{}, FailureCause:{}", test.getResult().getDuration().getSeconds(), test.getResult().getStatus().toString(), featureFile, startTime, failedStepNum, failedStepLine, test.getResult().getError());

		String analyticsReq = ReadConfigFiles.getConfigValue("AnalyticsRequired");
		if(analyticsReq != null && analyticsReq.equals("Y")) {

			try {
				String dbType = ReadConfigFiles.getConfigValue("DataBaseType");
				String failureReason = "";
				String query = "";

				if(failedStepLine != null) {
					failureReason = failedStepLine;
				}
				if(test.getResult().getError() != null) {
					failureReason = "Failed at " + failedStepNum + "step line of " + failureReason + " : " + test.getResult().getError();
				}

				if(dbType.equalsIgnoreCase("MySQL")){

					query = "INSERT INTO Auto_Run_Dtls (Run_Id, Run_Region, Scenario, Feature, Module, Run_Date, Duration, Status, Failure_Cause)  \n" +
							"VALUES ((Select MAX(Run_id) from Auto_Run_Stat), '"+ReadConfigFiles.getConfigValue("EnvironmentType")+"', '"+test.getTestCase().getName()+"', '"+featureFile+"', '"+moduleName+"', '"+runDate+"', "+test.getResult().getDuration().getSeconds()+", '"+test.getResult().getStatus().toString()+"', '"+failureReason+"')";

				}
				else if (dbType.equalsIgnoreCase("Oracle") || dbType.equalsIgnoreCase("PostgresSQL")) {

					query = "Write insert query for Oracle/PostgresSQL";
				}
				else if(dbType.equalsIgnoreCase("DB2")) {

					query = "Write insert query for DB2";
				}

				DBConnection.executeInsertQuery(query);
			}
			catch(Exception var3) {
				logger.error("Error in executing DB query method ", var3);
				logger.error("Error in connecting to DB in execute query method");
			}
		}

	}

	public static String setAutomationSuiteLog() {
		return
				"\n    _     __  __  _______   ___   ____   ____     _   _______  _   ___   ____    _                 \n" +
						"   / \\   | |  | ||__   __| / _ \\ | |\\ \\ / /| |   / \\ |__   __|| | / - \\ | |\\ \\  | |    \n" +
						"  / _ \\  | |  | |   | |   | / \\ || |  \\_/  | |  / _ \\   | |   | || / \\ || | \\ \\ | |     \n" +
						" / ___ \\ | \\__/ |   | |   | \\_/ || |       | | / --- \\  | |   | || \\_/ || |  \\ \\| |     \n" +
						"/_/   \\_\\ \\____/    |_|    \\___/ |_|       |_|/_/   \\_\\ |_|   |_| \\___/ |_|   \\__/     \n" +
						"--------------------------------------------------------------------------------------------";
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
						"----------------------------------------------------------------------("+ path + "; " + scenarioName +")";

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
