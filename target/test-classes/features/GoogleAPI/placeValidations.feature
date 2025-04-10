Feature: Validate Place API
	I want validate the Place API's

	@Regression
	Scenario:1 Verify if place being successfully added using AddPlaceAPI
		Given add headers to the API with below details:
			| Content-Type 		| Accept |
			| application/json 	| */* 	 |
		And add query parameters to the API with below details:
			| key 			|
			| qaclick123 	|
		When user calls "AddPlace" API with "POST" request:
			| latitude 	 | longitude | name  | address   			  | side layout  | language  |
			| -31.388192 | 37.589062 | atest | Maratahalli, Bangalore | shop		 | India-IND |
		Then validate the status "200" with below details:
			| status | place_id  | reference | id 		|
			| OK 	 | NotNull#  | NotNull 	 | NotNull  |


	@Regression
	Scenario:2 Verify if place details fetching successfully by GetPlaceAPI
		Given add query parameters to the API with below details:
			| key 			| place_id   |
			| qaclick123 	| place_id#  |
		When user calls "GetPlace" API with "GET" request
		Then validate the status "200" with below details:
			| address 	| name 	  | language |
			| NotNull   | NotNull | NotNull  |


	@Regression
	Scenario:3 Verify if place details fetching successfully by UpdatePlaceAPI
		Given add query parameters to the API with below details:
			| key 			|
			| qaclick123 	|
		When user calls "UpdatePlace" API with "PUT" request:
			|  place_id   | address    | key        |
			|  place_id#  | HSR Layout | qaclick123 |
		Then validate the status "200" with below details:
			| msg 							|
			| Address successfully updated  |


	@Regression
	Scenario:4 Verify if place details fetching successfully by DeletePlaceAPI
		Given add query parameters to the API with below details:
			| key 			|
			| qaclick123 	|
		When user calls "DeletePlace" API with "DELETE" request:
			|  place_id   |
			|  place_id#  |
		Then validate the status "200" with below details:
			| status |
			| OK     |
