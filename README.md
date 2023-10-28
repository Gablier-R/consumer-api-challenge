# Consume post api
This project is a challenge from the Compass.uol internship program This server will receive a request to search for a post from https://jsonplaceholder.typicode.com/posts and process the response, adding action history, such as: ${StatusPost}

## Flow
After request on POST an internal flow will be started, fetching the post and assiging histories to him and at end, will be returned the post processed.

## StatusPost
- CREATED: Initial state of a new post.
- POST_FIND: Indicates that the app is searching for basic post data.
- POST_OK: Indicates that the basic post data is already available.
- COMMENTS_FIND: Indicates that the app is searching for post comments.
- COMMENTS_OK: Indicates that the post comments are already available.
- ENABLED: Indicates that the post has been successfully processed and is enabled.
- DISABLED: Indicates that the post is disabled, either due to a processing failure or by user decision.
- UPDATING: Indicates that the post needs to be reprocessed.
- FAILED: Indicates a processing error.

## Settings
the api is not yet deployed, if you want to check the documentation and all the implementation details

1. Clone the repository:
   git clone https://github.com/Gablier-R/api-collections-movies.git

2. Access the swagger documentation http://localhost:8080/swagger-ui.html
