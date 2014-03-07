postwall
====================

Example project for using Akka, Spray and MongoDB on Heroku

This application "postwall" allows clients to post messages and to retrieve all posted messages.

The subfolder "postwall" is used as the root for the Heroku deployment. Uploading to Heroku is done using the script "uploadBackendToHeroku".

In order to upload you of course have to create a Heroku app with MongoDB setup (I used MongoHQ Sandbox) and configure the git config accordingly (https://devcenter.heroku.com/articles/git).

When working locally go into the postwall subfolder and run "./runAPI" to start the API process.

There is also a configuration for a background process ("Dyno"), but it is commented in the Procfile. To start the background process locally (that does nothing right now) run "./runProcessing" in the postwall subfolder.

When the API is running you can post message onto the wall by doing a POST request to http://localhost:8080/posts with JSON body of this kind:

{
  "message": "I am looking forward to springtime.",
  "author": "Mister Sunshine"
}

To get all the posted messages to a GET on http://localhost:8080/posts and it will return JSON with all the posts, something like:

[
    {
        "_id": "5319a79b0364eb5d12eed8f6",
        "message": "I am looking forward to springtime.",
        "author": "Mister Sunshine"
    },
    {
        "_id": "5319a7ae0364eb5d12eed8f7",
        "message": "I am looking forward to springtime.",
        "author": "Mister Sunny"
    }
]







