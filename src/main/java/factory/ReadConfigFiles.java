package factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class ReadConfigFiles {

	private static final Logger logger = LogManager.getLogger(ReadConfigFiles.class);

	private static final Properties config = new Properties();
	private static final Properties apiEndpoints = new Properties();

	public static void loadProperties(){
		loadProperties(config, System.getProperty("user.dir") + "//configure.properties", "configuration");
		loadProperties(apiEndpoints, System.getProperty("user.dir") + "//src//test//resources//properties//restAPIDetails.properties", "API endpoints");
	}

	/**
	 * Loads properties from a file into a Properties object.
	 */
	private static void loadProperties(Properties properties, String filePath, String fileType) {
		try (FileInputStream fis = new FileInputStream(filePath)) {
			properties.load(fis);
			logger.info("{} properties loaded successfully from: {}", fileType, filePath);
		} catch (FileNotFoundException e) {
			logger.error("{} file not found: {}", fileType, filePath, e);
		} catch (IOException e) {
			logger.error("Error while loading {} properties from: {}", fileType, filePath, e);
		}
	}

	/**
	 * Retrieves a configuration value from the loaded properties.
	 */
	public static String getConfigValue(String key) {
		return config.getProperty(key);
	}

	/**
	 * Retrieves an API endpoint value from the loaded API properties.
	 */
	public static String getApiEndpoint(String key) {
		return apiEndpoints.getProperty(key);
	}

	/**
	 * Retrieves a common default value from a text file.
	 */
	public static String getCommonDefault(String name) {
		String filePath = System.getProperty("user.dir") + "/src/test/resources/properties/commonDefaultValues.txt";

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split("=", 2);
				if (values.length == 2 && values[0].trim().equalsIgnoreCase(name)) {
					return values[1].trim();
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Common default values file not found: {}", filePath, e);
		} catch (IOException e) {
			logger.error("Error while reading common default values file: {}", filePath, e);
		}
		return null;
	}
}
