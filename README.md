# costmanagementserv

# Postman Collections

https://www.getpostman.com/collections/32d3710e1507ccb619b2

# 3 Proposed APIs(Details can be found in the pr, attached postman request and response for better understanding.)

Description: Get a list of aggregated costs of each episode for one show, {id} is the show id. 

 # 1. GET: /costs/{id} 
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

# 2. POST: /costs/{id}
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

#  3. GET: /prodcosts/{id} This endpoint is to get production costs of each episode for a show, including amortized costs.

Response:
Note: response body is the same pattern as GET /costs/{id}
1) 200 Ok
2) 404 Not found, Scenario when there is no episode cost for a show. Therefore no corresponding record in our database, throw NOT FOUND exception.



# Database Table Created: Used in-memory h2 relational database. Table creation is located at data.sql

1) show_episode_amount(table name): this table stores each cost transaction for an episode, this table can have multiple shows, a show can have multiple episodes, a episode of a show can have multiple cost amount. Index on show_id, it can return multiple rows.
```
show_id | episode_code| amount
```
# “bad data”
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
# corner cases tested
1) POST creation, if no record created, it should return 200 instead of 201.
2) GET operation, if no record found, it should retun 404.
3) POST creation, able to receive empty list, however it returns 200.

