# bluemix-chat-sample
This Node.js sample can be used on multiple platforms - [IBM Bluemix](https://console.ng.bluemix.net/) or your own machine!

__Important Note__: This sample creates a topic on your behalf with one partition - this will incur a fee if the topic does not already exist on your account.

## Prerequisites (Bluemix)
To run the sample on Bluemix, you will need the following:

* [Message Hub Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) provisioned in [IBM Bluemix](https://console.ng.bluemix.net/)
* [Cloud Foundry Command Line Interface](https://github.com/cloudfoundry/cli/releases) installed

## Prerequisites (Local)
The run the sample on your local machine, you will need the following:

* [Message Hub Service Instance](https://console.ng.bluemix.net/catalog/services/message-hub/) provisioned in [IBM Bluemix](https://console.ng.bluemix.net/)
* Node.js 0.12.x or above

## Building and Running the Sample (Bluemix)
The build and run the sample on Bluemix, all that needs to be done is the following:

* Open the `manifest.yml` file and rename the `"message-hub-service"` entry to that of your own
Message Hub Service Instance name.
* Run `cf push` where 'project_name' is the name of your project in Bluemix.

Once the files have been pushed to Bluemix, open a web browser and navigate to the endpoint provided by Bluemix.

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

Once the topic and consumer instance have been created, open a web browser and navigate to `http://localhost:port`, where `port` is
the port number described in the output produced when the application starts.
