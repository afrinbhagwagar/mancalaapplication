'use strict'
var module = angular.module('mancala.controllers', []);
module.controller("mancalacontroller", [ "$scope", "mancalaservice",
		function($scope, mancalaservice) {
			mancalaservice.loadGame().then(function() {
				mancalaservice.getAllPits().then(function(value) {
					$scope.currentPlayer="PlayerA";
					$scope.allPits = value.data;
					var i;
					for(i=7; i<14; i++){
						document.getElementById("button"+i).disabled = true;
					}
				}, function(reason) {
					console.log("Error occured when get service called to fetch all pits.");
				})
			}, function(reason) {
				console.log("Error occured when loading game.");
			});

			$scope.updatePits = function(pitId) {
				mancalaservice.updatePits(pitId).then(function() {
					mancalaservice.getAllPits().then(function(value) {
						$scope.allPits = value.data;
						mancalaservice.getStatus().then(function(value) {
							$scope.status = value.data;
							if(value.data["gameFinished"]==true){
								document.getElementById('winnerid').style.display = "table-cell";
								var i;
								for (i = 0; i < 14; i++) {
									document.getElementById("button"+i).disabled = true;
								}
								
							}else{
								mancalaservice.getCurrentPlayer().then(function(value) {
									//$scope.currentPlayer = value.data;
									console.log("whose turn : "+value.data["name"]);
									var i;
									if(value.data["name"]=="PlayerA"){
										$scope.currentPlayer = "PlayerA";
										for(i=0; i<7; i++){
											document.getElementById("button"+i).disabled = false;
										}
										for(i=7; i<14; i++){
											document.getElementById("button"+i).disabled = true;
										}
									}
									else{
										$scope.currentPlayer = "PlayerB";
										for(i=0; i<7; i++){
											document.getElementById("button"+i).disabled = true;
										}
										for(i=7; i<14; i++){
											document.getElementById("button"+i).disabled = false;
										}
									}
								}, function(reason) {
										console.log("Error occured fetching current player.");
									});
							}
						}, function(reason) {
							console.log("Error occured in get status.");
						})
					}, function(reason) {
						console.log("Error occured when getting all pits values function call.");
					});
				}, function(reason) {
					console.log("Error occured when updating pits called.");
				});
			}
		} ]);