package pojoClasses;

import java.util.HashMap;
import java.util.Map;

public class ResponseLoader {
	
	private static Map<String, Object> globalValues = new HashMap<>();

    public static Map<String, Object> getGlobalValues() {
        return globalValues;
    }

    public static void addGlobalValue(String key, Object value) {
        globalValues.put(key, value);
    }

}
