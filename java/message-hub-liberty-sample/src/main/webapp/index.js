/**
 * Copyright 2016 IBM
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
 * (c) Copyright IBM Corp. 2016
 */

//index.js
// request message on server
//Calls KafkaServlet
xhrGet("KafkaServlet", function(responseText){
	// add to document
	var mytitle = document.getElementById('message');
	mytitle.innerHTML = responseText;

}, function(err){
	console.log(err);
});

function postMessage(){
	var responseDiv = document.getElementById('consumeResponse');
	responseDiv.innerHTML = "<p>waiting for consumer...</p>";
	var responseDiv = document.getElementById('produceResponse');
	responseDiv.innerHTML = "<p>waiting for producer...</p>";
	var postMessageBtn = document.getElementById('btnPost');
	postMessageBtn.value='request sent ...';
	xhrPost("KafkaServlet", function(responseText){
        // fill in produced and consume message sections
        // responseText is an array of an produced then consumed message
        var response = responseText.split(",,");

        var produceDiv = document.getElementById('produceResponse');
        produceDiv.innerHTML = response[0];
        var consumeDiv = document.getElementById('consumeResponse');
        consumeDiv.innerHTML = response[1];
        // reset button text
        postMessageBtn.value='Produce a Message';

		xhrGet("KafkaServlet", function(responseText){
			// add message to 'already consumed messages' section
			var mytitle = document.getElementById('message');
			mytitle.innerHTML = responseText;
			postMessageBtn.value='Produce a Message';

		}, function(err){
			console.log(err);
		});
	}, function(err){
		console.log(err);
	});
}

//utilities
function createXHR(){
	if(typeof XMLHttpRequest != 'undefined'){
		return new XMLHttpRequest();
	}else{
		try{
			return new ActiveXObject('Msxml2.XMLHTTP');
		}catch(e){
			try{
				return new ActiveXObject('Microsoft.XMLHTTP');
			}catch(e){}
		}
	}
	return null;
}
function xhrGet(url, callback, errback){
	var xhr = new createXHR();
	xhr.open("GET", url, true);
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4){
			if(xhr.status == 200){
				callback(xhr.responseText);
			}else{
				errback('service not available');
			}
		}
	};
	xhr.timeout = 3000;
	xhr.ontimeout = errback;
	xhr.send();
}

function xhrPost(url, callback, errback){
	var xhr = new createXHR();
	xhr.open("POST", url, true);
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4){
			if(xhr.status == 200){
				callback(xhr.responseText);
			}else{
				errback('XMLHttpRequest ready state: ' + xhr.readyState + '. Service not available');
			}
		}
	};
	xhr.timeout = 10000;
	xhr.ontimeout = errback;
	xhr.send();
}

function parseJson(str){
	return window.JSON ? JSON.parse(str) : eval('(' + str + ')');
}
function prettyJson(str){
	// If browser does not have JSON utilities, just print the raw string value.
	return window.JSON ? JSON.stringify(JSON.parse(str), null, '  ') : str;
}
