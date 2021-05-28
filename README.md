# costmanagementserv
# 1. How to run this application?
```
1) Clone this git repo
2) Do mvn clean install -U locally
3) Go to CostmanagementservApplication.java, right click to start application
4) Use below Postman Collection link to trigger requests
5) For database use, I used in-memory h2 relational database. Table creation is located at data.sql. 
Once application started, you can visit http://localhost:8080/h2-console to see the table UI. Below are the username and password to login:
username=sa
password=password
Note: This info is stored in application.properties, feel free to change to your own on your local.
```
# 2. Postman Collections

https://www.getpostman.com/collections/32d3710e1507ccb619b2

# 3. 3 Proposed APIs
###### Details can be found in the pr, attached postman request and response for better understanding.

Description: Get a list of aggregated costs of each episode for one show, {id} is the show id. 

 ##  a) GET Operation: 
 ###### url path: /costs/{id} 
 This is to get cost report for all episodes of a show, excluding amortized costs. 
 This endpoint is designed to work for both episodic or non-episodic shows
Response:
1) 200 ok
```
[{
        id:"",
        episode_code:"",
        amount:""
    } ,
    {
        id:"",
        episode_code:"",
        amount:""
    },
    {
        id:"",
        episode_code:"",
        amount:""
    }... 
    ]

```
Note: each amount is the aggregated amount here. episode_code is unique

2) 404 not found

Scenario when there is no episode cost for a show. Therefore no corresponding record in our database, throw NOT FOUND exception.

##  b) POST Operation: 
 ###### url path: /costs/{id}
This endpoint is to create a cost transaction for an episode (an episode of a show, you need to provide which show this episode is for. )
 This endpoint is designed to work for both episodic or non-episodic shows

Response:
1) 201 Created
Request Body:
```
{
    {
        show_id:"",
        episode_code:"",
        amount:""
    }
}
```

2) 400 Validation error can be thrown, See detail in the 'bad data' section
3) 200 Ok, scenario when no data is inserted

##   c) GET Operation: 
 ###### url path: /prodcosts/{id} This endpoint is to get production costs of each episode for a show, including amortized costs.

Response:
Note: response body is the same pattern as GET /costs/{id}
1) 200 Ok
2) 404 Not found, Scenario when there is no episode cost for a show. Therefore no corresponding record in our database, throw NOT FOUND exception.



# 4. Database Table Created: 
## Used in-memory h2 relational database. Table creation is located at data.sql

1) show_episode_amount(table name): this table stores each cost transaction for an episode, this table can have multiple shows, a show can have multiple episodes, a episode of a show can have multiple cost amount. Index on show_id, it can return multiple rows.
```
show_id | episode_code| amount
```
# 5. “bad data”
1)  POST creation, unrecognized request body calling POST, return 400 bad request
  Reason: 'episode_co' is not a valid field
  ```
     [{
    "id":"4",
    "episode_co":"2",
    "amount":"200"
    }
    ]
 ```
 2)  POST creation, Episode code length != 3, , return 400 bad request
 Reason: episode_code length is not 3
```
  [{
      "id":"4",
      "episode_code":"1111",
      "amount":"200"
  }
  ]
```
3)  POST creation, request object missing field 
Reason: missing "id" field.
```
[{
    
    "episode_code":"1111",
    "amount":"200"
}
]
```
# 6. Corner cases tested
```
1) POST creation, if no record created, it should return 200 instead of 201.
2) GET operation, if no record found, it should retun 404.
3) POST creation, able to receive empty list, however it returns 200.
```
# 7. Things can be improved
```
1) Mentioned corner cases/error case scenarios are all tested by postman. Only partial unit tests are covered. Could have better coverage on UT. FT is not added, DAO test is not added here either.
2) For 400 validation errors, could have add detail error msgs to clients, due to limited time, won't add here.
3) If to scale this service, could modulize the code into different modules. This current model is intended only for this code practice.
4) Used jackson JSON library here, would better have submodule for api data model definitions as well as api definitions.
```
# 8. Reach out if you need help
```
Email: miayu79@gmail.com
```
