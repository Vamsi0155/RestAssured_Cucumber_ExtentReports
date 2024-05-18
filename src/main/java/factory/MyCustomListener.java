package factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyCustomListener {
	
	private static final Logger logger =LogManager.getLogger(MyCustomListener.class);
	
	
	
	public static void getTestStartedLog(String scenarioName, String path) {
            
		String testStarted =
             "\n _____ _____ ____ _____   ____ _____  _    ____ _____ _____ ____  \n" +
                "|_   _| ____/ ___|_   _| / ___|_   _|/ \\  |  _ \\_   _| ____|  _ \\ \n" +
                "  | | |  _| \\___ \\ | |   \\___ \\ | | / _ \\ | |_) || | |  _| | | | |\n" +
                "  | | | |___ ___) || |    ___) || |/ ___ \\|  _ < | | | |___| |_| |\n" +
                "  |_| |_____|____/ |_|   |____/ |_/_/   \\_\\_| \\_\\|_| |_____|____/ \n" +
                "\n" +
                "TEST STARTED: " + scenarioName + " \n" +
                "----------------------------------------------------------------------("+ path +".feature; " + scenarioName;
		
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
