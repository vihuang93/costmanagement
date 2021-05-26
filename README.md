# costmanagement

# Proposed APIs:

Description: Get a list of aggregated costs of each episode for one show, {id} is the show id. 

GET: /costs/{id}
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

POST: /costs/{id}
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


# Database Table Created:

1) show_episode_amount(table name): this table stores each cost transaction for an episode, this table can have multiple shows, a show can have multiple episodes, a episode of a show can have multiple cost amount. Index on show_id, it can return multiple rows.

show_id | episode_code| amount
