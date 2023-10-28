#Posts consumption
This project is a challenge from internship program Compass.uol This server will receive a request to search a post from https://jsonplaceholder.typicode.com/posts and process the response, adding actions history, like created, post_find, comment_find, failed etc.

##Flow
After request on POST /posts/${id} an internal flow will be started, fetching the post and assiging histories to him and at end, will be returned the post processed

Inside resource folder, has an script to fire 100 curl request to this api and measure the total time to fetch all posts
