package factory;

import io.cucumber.datatable.DataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojoClasses.ResponseLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader {

    private static final Logger logger = LogManager.getLogger("Loader.class");
    private static final Map<String, Map<String, String>> loadInputValues = new HashMap<>();


    public static void loadInputValues(String apiName, DataTable table) {
        loadInputValues.put(apiName, updateTableWithGlobalValues(table));
    }

    public static String getInputValue(String keyValue, String apiName) {
        return loadInputValues.get(apiName).getOrDefault(keyValue, "");
    }

    public static void clearLoadInputValues(){
        loadInputValues.clear();
    }

    public static Map<String, String> loadTableValues(List<Map<String, String>> map) {

        if (!map.isEmpty()) {
            return map.getFirst();
        }
        else
            logger.error("Data table is empty!!");
        return new HashMap<>();
    }

    public static Map<String, String> loadTableValues(DataTable table) {

        List<Map<String, String>> dataList = table.asMaps(String.class, String.class);
        if (!dataList.isEmpty()) {
            return dataList.getFirst();
        }
        else
            logger.error("Data table is empty");
        return new HashMap<>();
    }

    public static Map<String, String> updateTableWithGlobalValues(DataTable table) {

        Map<String, Object> globalMap = ResponseLoader.getGlobalValues();
        Map<String, String> updatedInputValues = new HashMap<>();

        List<Map<String, String>> data = table.asMaps(String.class, String.class);
        for (Map<String, String> mainMap : data) {
            Map<String, String> tmpTable = new HashMap<>(mainMap);

            for (String key:tmpTable.keySet()){
                String value = tmpTable.get(key);

                if(value.equals("Image#")){
                    String imagePath = "./src/test/resources/images/"+key.split("-")[0]+".jpg";
                    tmpTable.put(key,imagePath);
                }

                if (value.endsWith("#")) {
                    Object updatedValue = globalMap.get(value.replace("#", ""));
                    System.out.println(updatedValue);
                    if (updatedValue != null) {
                        tmpTable.put(key, updatedValue.toString());
                    }
                    else
                        logger.error("Global value for key '{}' is null", key);
                }
            }
            updatedInputValues.putAll(tmpTable);
        }
        return updatedInputValues;
    }
}
