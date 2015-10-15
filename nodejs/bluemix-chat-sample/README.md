# bluemix-chat-sample
This Node.js sample can be used on multiple platforms - [IBM Bluemix](https://console.ng.bluemix.net/) or your own machine!

## Prerequisites (Bluemix)
To run the sample on Bluemix, you will need the following:

* A provisioned Bluemix application running the __SDK for Node.js__
* A Message Hub service instance created
* ```cf``` tool installed
* Application files downloaded to your local machine

If you have not done this, you can follow the [linked instructions](https://www.ng.bluemix.net/docs/services/MessageHub/index.html) to get the
prerequisites set up.

## Prerequisites (Local)
The run the sample on your local machine, you will need the following:

* A Message Hub service instance created
* ```cf``` tool installed
* Application files downloaded to your local machine
* Node.js 0.12.x or above

## Building and Running the Sample (Bluemix)
The build and run the sample on Bluemix, all that needs to be done is the following:

* Copy the sample `bluemix-chat-sample` files into your Bluemix application file directory.
* Run `cf push <project_name>` where 'project_name' is the name of your project in Bluemix.

Once the files have been pushed to Bluemix and your application has been restarted, open a web browser and navigate to the endpoint provided by Bluemix.

## Building and Running the Sample (Local)
To build and run the sample, you will first need to install its dependencies. To do this, run
the following command:

```shell
npm install
```

Once the dependencies have been installed, run the following command to get the sample running:

```shell
node app.js <rest_endpoint> <api_key>
```

The application will then produce the following output on start-up:
```
Consumer Group Name: messagehub-rest-nodejs-chat-87da0e40
Consumer Group Instance: messagehub-rest-nodejs-consumer-instance-u659ayw5
HTTP server started on port 6003
"livechat" topic created.
Consumer Instance created.
```

Once the topic and consumer instance have been created, open a web browser and navigate to `http://localhost:port`.
