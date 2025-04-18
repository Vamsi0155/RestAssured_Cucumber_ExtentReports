package cucumberOptions;

import org.junit.runner.RunWith;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features="src/test/resources/features",
		glue="stepDefinitions",
		monochrome=true,
		tags="",
		plugin={"utilities.MyCustomListener",
				"pretty",
				"html:reports/htmlReports/cucumber.html", 
				"json:reports/jsonReports/cucumber.json",
				"rerun:target/failedScenarios.txt",
				"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
		}
		)
public class TestRunner {

}
