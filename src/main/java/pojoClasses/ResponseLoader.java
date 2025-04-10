package pojoClasses;

import java.util.HashMap;
import java.util.Map;

public class ResponseLoader {

    private static final Map<String, Object> globalValues = new HashMap<>();

    public static Map<String, Object> getGlobalValues() {
        return globalValues;
    }

    public static void addGlobalValue(String key, Object value) {
        globalValues.put(key, value);
    }

    public static void clearGlobalValues(){
        globalValues.clear();
    }

}
