'use strict'
angular.module('mancala.services', []).factory('mancalaservice', ["$http", "CONSTANTS", function($http, CONSTANTS) {
    var service = {};
    service.getAllPits = function() {
        return $http.get(CONSTANTS.getAllPits);
    }
    
    service.updatePits = function(pitId) {
    	var url = CONSTANTS.updatePits + pitId;
        return $http.put(url);
    }
    
    service.loadGame = function() {
        return $http.post(CONSTANTS.loadGame);
    }
    
    service.getStatus = function() {
        return $http.get(CONSTANTS.status);
    }
    
    service.getCurrentPlayer = function() {
        return $http.get(CONSTANTS.currentPlayer);
    }
    return service;
}]);