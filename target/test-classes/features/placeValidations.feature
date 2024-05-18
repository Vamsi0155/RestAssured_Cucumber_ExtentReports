Feature: Validate Place API
	I want validate the Place API's


@Regression
Scenario:1 Verify if place being succesfully added using AddPlaceAPI
	Given add headres to the API with below details:
	| Content-Type 			| Accept |
	| application/json 	| */* 	 |
	And add query parameters to the API with below details:
	| key 				|
	| qaclick123 	|
	When pass with body of "AddPlace" API below details:
	| latitude 	 | longitude | name  | address   							 | side layout  | language  |
	| -37.388492 | 34.589162 | atest | Marathahalli, Bangalore | shop				  | India-IND |
	And the user calls "AddPlace" API with "POST" request
	Then validate the status code "200"
	And validation done with below details:
	| status | place_id  | reference | id 			|
	| OK 		 | $NotNull  | NotNull 	 | NotNull  |
	

@Regression
Scenario:2 Verify if place details fecthing succesfully by GetPlaceAPI
	Given add headres to the API with below details:
	| Content-Type 			| Accept |
	| application/json 	| */* 	|
	And add query parameters to the API with below details:
	| key 				| place_id   |
	| qaclick123 	| $place_id  |
	When pass without body of "GetPlace" API below details:
	And the user calls "GetPlace" API with "GET" request
	Then validate the status code "200"
	And validation done with below details:
	| address 		| name 		| language |
	| NotNull     | NotNull | NotNull  |
	

@Regression
Scenario:3 Verify if place details fecthing succesfully by UpdatePlaceAPI
	Given add headres to the API with below details:
	| Content-Type 			| Accept |
	| application/json 	| */* 	|
	And add query parameters to the API with below details:
	| key 				|
	| qaclick123 	|
	When pass with body of "UpdatePlace" API below details:
	|  place_id   | address    | key        |
	|  $place_id  | HSR Layout | qaclick123 |
	And the user calls "UpdatePlace" API with "PUT" request
	Then validate the status code "200"
	And validation done with below details:
	| msg 													|
	| Address successfully updated  |
	

@Regression	
Scenario:4 Verify if place details fecthing succesfully by DeletePlaceAPI
	Given add headres to the API with below details:
	| Content-Type 			| Accept |
	| application/json 	| */* 	|
	And add query parameters to the API with below details:
	| key 				|
	| qaclick123 	|
	When pass with body of "DeletePlace" API below details:
	|  place_id   |
	|  $place_id  |
	And the user calls "DeletePlace" API with "DELETE" request
	Then validate the status code "200"
	And validation done with below details:
	| status |
	| OK     |
