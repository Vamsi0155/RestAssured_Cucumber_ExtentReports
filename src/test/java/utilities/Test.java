package utilities;


import java.util.HashMap;
import java.util.Map;

public class Test {
	

    public static Map<Object, Object> processFile(Map<Object, Object> mainMap, Map<Object, Object> globalMap) {
        
          
        for (Map.Entry<Object, Object> entry : mainMap.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();

                // Check if the value contains '#'
                if (value instanceof String && ((String) value).contains("#")) {
                    String temp = String.valueOf(value);
                    Object updatedValue = globalMap.get(temp.replace("#", ""));

                    // Update the value in the main map
                    if (updatedValue != null) {
                        mainMap.put(key, updatedValue);
                    }
                    else
                    	//logger.error("Global value has null");
                    	System.out.println("Global value has null");
                }
        }
        return mainMap;
    }

    public static void main(String[] args) {
    	
    	Map<Object, Object> mainMap = new HashMap<>();
        mainMap.put("key1", "value1#");
        mainMap.put("key2", "value2");
        mainMap.put("key3", "value3#");

        Map<Object, Object> globalMap = new HashMap<>();
        globalMap.put("value1", "updatedValue1");
        globalMap.put("value3", "updatedValue3");

        processFile(mainMap, globalMap);
        System.out.println(processFile(mainMap, globalMap));
    }
}
