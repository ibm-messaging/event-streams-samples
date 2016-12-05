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
// Load Angular modules and set Angular Material theme.
var app = angular.module('StarterApp', ['ngMaterial'])
  .config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
      .primaryPalette('blue-grey')
      .backgroundPalette('blue-grey')
      .accentPalette('blue');
  });

app.controller('AppCtrl', ['$scope', '$mdSidenav', function($scope, $mdSidenav) {

}]);

app.controller('chatCardController', ['$scope', function($scope) {
  // socket.io variable for connecting to
  // back-end service.
  var socket;
  $scope.feedMessages = [];
  // Set default data for chat-card directive.
  $scope.data = {
    username: "A user",
    connectionDate: null,
    connecting: false,
  };

  var pushMessageToInterface = function(message, user) {
    $scope.feedMessages.push({
      message: message,
      user: user,
      time: new Date().toLocaleString(),
    });
  };

  $scope.disconnect = function() {
    socket.disconnect();
    $scope.feedMessages = [];
    $scope.data.connectionDate = null;
  };

  /**
   * $scope.connect
   * Sets up socket.io instances and connects to
   * back-end service. Called when the user clicks
   * the 'Connect' button.
  */
  $scope.connect = function() {
    $scope.data.connecting = true;

    // Show button again after 5 seconds of not connecting.
    var connectTimeout = setTimeout(function() {
      $scope.data.connecting = false;
      $scope.$apply();
    }, 5000);

    if(!socket) {
      socket = io.connect();
    } else {
      socket.connect();
    }

    /**
     * on('service_connected')
     * Callback triggered when the server acknowledges
     * the WebUI's connection request. The server responds
     * with the username the client should use in communication.
    */
    socket.on('service_connected', function(username) {
      clearTimeout(connectTimeout);
      $scope.data.connecting = false;
      $scope.data.username = username;
      $scope.data.connectionDate = new Date().toLocaleString();
      $scope.$apply();
    });

    /**
     * on('topic_data')
     * Callback triggered when the server acknowledges
     * the WebUI's connection request. The server responds
     * with the username the client should use in communication.
    */
    socket.on('topic_data', function(data) {
      for(var index in data) {
        pushMessageToInterface(data[index].message, data[index].user);
      }

      $scope.$apply();
    });

    socket.on('disconnect', function() {
      $scope.disconnect();
      $scope.$apply();
    });

    // Sends a message to the server, asking to
    // be connected.
    socket.emit('service_connect', undefined);
  };

  /**
   * $scope.sendMessage
   * Triggered when the user clicks the 'Send' button,
   * once connected. If the message is valid, the message
   * is sent via socket.io with the topic name 'new_message'.
  */
  $scope.sendMessage = function() {
    if($scope.data.message && $scope.data.message.length > 0 && $scope.data.message.length <= 140) {
      socket.emit('new_message', $scope.data.message);
      $scope.data.message = "";
    }
  };

}]);

/**
 * chatCard directive
 * Directive set up for 'chatCard' directive. Only
 * to be used on as an attribute. Example usage:
 *
 * <div chat-card></div>
*/
app.directive('chatCard', function() {
  return {
    templateUrl: 'templates/chatCard.tpl.html',
    restrict: 'A',
    controller: 'chatCardController',
  };
});
