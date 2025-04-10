# Cucumber BDD Framework with Extent Reports for Rest-API Automation

## Overview
  This framework is developed using Behavior-Driven Development (BDD) principles with Cucumber and integrated with Extent Reports. It is designed specifically for automating REST APIs, providing robust reporting and flexibility for handling API responses and requests.
### Key Features
#### Cucumber BDD:
- Enables writing human-readable test scenarios that bridge the gap between non-technical stakeholders and developers.
#### Extent Reports:
- Generates visually appealing and detailed test reports with screenshots, logs, and results.
#### Response Value Reuse:
- The framework allows passing response values from one API request to another. Simply reference the value using the $ symbol as "$key" to inject it into subsequent API requests dynamically.
#### Comprehensive Logging:
  Logs are configured at a high level, printing detailed information for each test scenario, including:
  - Scenario name
  - Duration
  - Execution status (pass/fail)
  - Failure cause or error (if any)
#### Database Integration:
- The framework can push scenario execution details (status, duration, errors) into a database for further analysis and tracking.
#### Multi-Database Support:
  The framework connects with different SQL databases such as:
  - Oracle
  - DB2
  - MySQL This allows for executing queries across various database environments and validating data integrity.
#### Spring Batch Integration:
- The framework includes a Batches class, which executes Spring Batch jobs for batch processing use cases.

## Prerequisites
- Java 17 or higher
- Maven 3.6v or higher
- Junit 5.0v or higher
- RestAssured 5.4.0v or higher
- Cucumber 7.16v or higher
- Extent-reports 5.0v or higher

## How to run from cmd line
In the pom.xml, we have configured Extent reports and run the features by following different commands.

### To build the Project
To build the project and download all dependencies, run the whole suite following Maven command:
```bash
mvn clean install
```

### To run specific feature, use the following command:
```bash
mvn clean install -Dcucumber.options="--features src/test/resources/features/GoogleAPI/placeValidations.feature"
```

### To run multiple features, use the following command:
```bash
mvn clean install -Dcucumber.options="--features src/test/resources/features/GoogleAPI/placeValidations.feature,src/test/resources/features/EcommerceAPI/EndtoEnd_E_commerce.feature"
```

### To run scenarios with Tag's, use the following command:
```bash
mvn clean install -Dcucumber.options="--tags @Regression"
```

### To run features with Tag's, use the following command:
```bash
mvn clean install -Dcucumber.options="--features src/test/resources/features/GoogleAPI/placeValidations.feature --tags @Regression"
```

## Jenkins CI/CD
Choose the Maven project and configure by following detail,
#### General Section:
 Select "This project is parameterized" and set below parameters.
1. For 1st parameter, select String parameter and set below details:
 - Name: Features
 - Default value: src/test/resources/features
 - Description: -- Pass the features path here by separated coma (,). -- By default, it will pick up all features.
2. For 2nd parameter, select choice parameter and set below details:
 - Name: Tags
 - Choices: Regression, Smoke, Sanity
 - Description: -- Choose the tags. By default, Regression
#### Build Section:
1. For Root POM, give as "pom.xml"
2. For Goals and Options, use the following command:
```bash
clean verify -Dcucumber.options="--features ${Features} --tags @${Tags}"
```
3. Go to "Advanced" and check the "use custom workspace". Add project directory path.

Note: Add project path in the custom workspace field and Also remaining as per requirements.
