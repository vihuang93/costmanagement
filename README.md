# costmanagementserv

# Postman Collections

https://www.getpostman.com/collections/32d3710e1507ccb619b2

# Proposed APIs:

Description: Get a list of aggregated costs of each episode for one show, {id} is the show id. 

 # GET: /costs/{id} 
 This is to get cost report for all episodes of a show, excluding amortized costs. 
 This endpoint is designed to work for both episodic or non-episodic shows
Response:
1) 200 ok
{
    cost_report:[show_episode_cost:{
        show_id:"",
        episode_code:"",
        amount:""
    } ,
    show_episode_cost:{
        show_id:"",
        episode_code:"",
        amount:""
    },
    show_episode_cost:{
        show_id:"",
        episode_code:"",
        amount:""
    }... ]
}
Note: cost_report is a list of show_episode_cost, each amount in show_episode_cost is the aggregated amount here.

2) 404 not found

# POST: /costs/{id}
This endpoint is to create a cost transaction for an episode (an episode of a show, you need to provide which show this episode is for. )
 This endpoint is designed to work for both episodic or non-episodic shows

Request Body:
{
    show_episode_cost:{
        show_id:"",
        episode_code:"",
        amount:""
    }
}

Response:
1) 201 Created
2) 400 Validation error

#  GET: /prodcosts/{id} This endpoint is to get production costs of each episode for a show, including amortized costs.


# Database Table Created:

1) show_episode_amount(table name): this table stores each cost transaction for an episode, this table can have multiple shows, a show can have multiple episodes, a episode of a show can have multiple cost amount. Index on show_id, it can return multiple rows.

show_id | episode_code| amount

# “bad data”
1)  POST creation, unrecognized request body calling POST, return 400 bad request
  'episode_co' is not a valid field
     [{
    "id":"4",
    "episode_co":"2",
    "amount":"200"
    }
    ]
 2)  POST creation, Episode code length != 3, , return 400 bad request
  [{
      "id":"4",
      "episode_code":"1111",
      "amount":"200"
  }
  ]
3)  POST creation, request object missing field 
[{
    
    "episode_code":"1111",
    "amount":"200"
}
]
# corner cases tested
1) POST creation, if no record created, it should return 200 instead of 201.
2) GET operation, if no record found, it should retun 404.
3) POST creation, able to receive empty list, however it returns 200.

