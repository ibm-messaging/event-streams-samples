/*eslint-env node*/
/**
 * Copyright 2015 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/
/**
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corp. 2015
*/
var Express = require('express');
var Cfenv = require('cfenv');
var MessageHub = require('message-hub-rest');

/**
 * registerExitHandlers
 * Catch events when application is closing, so a cleanup
 * event can be called. Catches:
 * - Normal process exit ('exit' event)
 * - Ctrl+C (SIGINT)
 * - Uncaught Exceptions ('uncaughtException' event)
*/
var registerExitHandler = function(callback) {
  if(callback) {
    var events = ['exit', 'SIGINT', 'uncaughtException'];

    for(var index in events) {
      process.on(events[index], callback);
    }
  } else if(!callback) {
    throw new ReferenceException('Provided callback parameter is undefined.');
  }
};

/**
 * generateID
 * Generate a unique ID. If an ID cannot be generated within
 * 1000 attepmts, the function returns undefined.
 *
 * @optional idLength     Number of characters the ID will contain.
*/
var previousIDs = [];
var generateID = function(idLength) {
  var ALPHABET = '0123456789abcdefghijklmnopqrstuvwxyz';
  var MAX_ATTEMPTS = 1000;
  idLength = idLength || 8;

  for(var attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
    var result = '';
    for(var i = 0; i < idLength; i++) {
      var index = (Math.random() * ALPHABET.length) | 0;
      result += ALPHABET[index];
    }

    if(previousIDs.indexOf(result) === -1) {
      previousIDs.push(result);
      return result;
    }
  }

  return result;
};

// Set up app and use public directory as
// base file serving directory
var app = Express();
app.use(Express.static(__dirname + '/public'));

// Set up socket.io, to allow message broadcasting.
var http = require('http').Server(app);
var io = require('socket.io').listen(http);

// Retrieve Bluemix environment variables
var appEnv = Cfenv.getAppEnv();
var connectedUsers = 0;
var cleanedUp = false;
var consumerInstance;
var connectedIds = [];
var instance;
var consumerGroupName = 'messagehub-rest-nodejs-chat-' + generateID();
var consumerInstanceName = 'messagehub-rest-nodejs-consumer-instance-' + generateID();
var produceInterval;

console.log('Consumer Group Name: ' + consumerGroupName);
console.log('Consumer Group Instance: ' + consumerInstanceName);

// Register a callback function to run when
// the process exits.
registerExitHandler(function() {
  stop();
});

// Callback triggered when socket.io-client connects
// to the back end service.
io.on('connection', function(socket) {

  var pushMessage = function(id, message) {
    var list = new MessageHub.MessageList();
    var message = {
      user: id,
      message: message,
    }

    list.push(JSON.stringify(message));

    instance.produce('livechat', list.messages)
      .then(function(response) {
          console.log(response);
      })
      .fail(function(error) {
        throw new Error(error);
      });
  };

  /**
   * on('disconnect')
   * On socket disconnect, log a message and decrease
   * the number of active users by 1.
  */
  socket.on('disconnect', function() {
    var message = 'User ' + socket.id + ' disconnected from the service.';
    var deleted = false;
    var index = 0;

    pushMessage('System', message);
    console.log(message);

    while(index < connectedIds.length && deleted === false)
    {
      if(connectedIds[index] === socket.id) {
        connectedUsers--;
        connectedIds.splice(index, 1);
        deleted = true;
      }

      index++;
    }
  });

  /**
   * on('service_connect')
   * Message received when the user clicks the
   * 'Connect' button in the WebUI.
  */
  socket.on('service_connect', function() {
    var message = 'User ' + socket.id + ' connected to the service.';
    var idInList = false;

    for(var index in connectedIds) {
      if(connectedIds[index] === socket.id) {
        idInList = true;
      }
    }

    if(!idInList) {
      connectedIds.push(socket.id);
      connectedUsers++;
      pushMessage('System', message);
      console.log(message);

      socket.emit('service_connected', socket.id);
    }
  });

  /**
   * on('new_message')
   * Message received when the user sends a message
   * via the WebUI. The message is pushed into Message Hub.
  */
  socket.on('new_message', function(data) {
    console.log(socket.id + ' pushing message: ' + data);
    pushMessage(socket.id, data);
  });

});

var start = function(restEndpoint, apiKey, callback) {
  if(!appEnv.services || (appEnv.services && Object.keys(appEnv.services).length === 0)) {
    if(restEndpoint && apiKey) {
      appEnv.services = {
        "messagehub": [
           {
              "label": "messagehub",
              "credentials": {
                 "api_key": apiKey,
                 "kafka_rest_url": restEndpoint,
              }
           }
        ]
      };
    } else {
      console.error('A REST Endpoint and API Key must be provided.');
      process.exit(1);
    }
  } else {
    console.log('Endpoint and API Key provided have been ignored, as there is a valid VCAP_SERVICES.');
  }

  instance = new MessageHub(appEnv.services);

  // Set up an interval which will poll Message Hub for
  // new messages on the 'livechat' topic.
  produceInterval = setInterval(function() {

    // Attempt to consumer messages from the 'livechat' topic_data
    // if at least one user is connected to the service.
    if(consumerInstance && connectedUsers > 0) {
      consumerInstance.get('livechat')
        .then(function(data) {
          if(data.length > 0) {
            console.log('Recieved data: ' + data);

            for(var index in data) {
              data[index] = JSON.parse(data[index]);
            }

            io.emit('topic_data', data);
          }
        })
        .fail(function(error) {
          throw new Error(error);
        });
    }

  }, 250);

  instance.topics.create('livechat')
    .then(function(response) {
      console.log('"livechat" topic created.');
      // Set up a consumer group of the provided name.
      return instance.consume(consumerGroupName, consumerInstanceName, { 'auto.offset.reset': 'largest' });
    })
    .then(function(response) {
      consumerInstance = response[0];
      console.log('Consumer Instance created.');
      // Set offset for current consumer instance.
      return consumerInstance.get('livechat');
    })
    .then(function() {
        // Make web server listen on listed port.
        http.listen(appEnv.port, function() {
          console.log('HTTP server started on port ' + appEnv.port);

          if(callback) {
            callback();
          }
        });
    })
    .fail(function(error) {
      console.log(error);
      stop(1);
    });
};

var stop = function(exitCode) {
  exitCode = exitCode || 0;

  if(!cleanedUp) {
    console.log('Running exit handler.');

    console.log('Clearing produce interval.');
    clearInterval(produceInterval);

    console.log('Closing HTTP server.');
    http.close();

    console.log('Closing sockets.io.');
    io.close();

    if(consumerInstance) {
      console.log('Removing consumer instance: ' + consumerGroupName + '/' + consumerInstanceName);
      consumerInstance.remove()
        .fin(function(response) {
          try {
            console.log(JSON.stringify(response));
          } catch(e) {
            console.log(response);
          }

          cleanedUp = true;
          process.exit(exitCode);
        });
    } else {
      cleanedUp = true;
      process.exit(exitCode);
    }
  }
};

// If this module has been loaded by another module, don't start
// the service automatically. If it's being started from the command license
// (i.e. node app.js), start the service automatically.
if(!module.parent) {
  if(process.argv.length >= 4) {
    start(process.argv[process.argv.length - 2], process.argv[process.argv.length - 1]);
  } else {
    start();
  }
}

module.exports = {
  start: start,
  stop: stop,
  appEnv: appEnv
}
