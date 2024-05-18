package factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ReadConfigFiles {

	
	private static FileInputStream fis;
	private static BufferedReader commonDefaults;
	
	public static Properties config = new Properties();
	public static Properties apiEndpoints = new Properties();
	
	private static Logger logger = LogManager.getLogger(ReadConfigFiles.class);
	
	
	static {
		try {
			fis = new FileInputStream(new File (System.getProperty("user.dir") + "//configure.properties"));
			config.load(fis);
			
		} catch (Exception e) {
			logger.error("Error while loading of confiure properties ", e);
		}
		finally {
			try {
				if(fis !=null) {
					fis.close();
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	static {
		try {
			fis = new FileInputStream(new File (System.getProperty("user.dir") + "//src//test//resources//properties//restAPIDetails.properties"));
			apiEndpoints.load(fis);
			
		} catch (Exception e) {
			logger.error("Error while loading of API endpoints properties ", e);
		}
		finally {
			try {
				if(fis !=null) {
					fis.close();
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getCommonDefault(String name) {
		
		String value=null;
		try {
			commonDefaults = new BufferedReader(new FileReader(System.getProperty("user.dir")+"//src//test//resources//properties//commonDefaultValues.txt"));
			String line;
            while ((line = commonDefaults.readLine()) != null) {
            	
                String[] values=line.split("=");
                if(values[0].equalsIgnoreCase(name)) {
                	value = values[1];
                	break;
                }
            }
		} catch (Exception e) {
			logger.error("Error while loading of common default values.");
        }
		finally {
			try {
				if(fis !=null) {
					commonDefaults.close();
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
