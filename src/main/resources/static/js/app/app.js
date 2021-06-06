'use strict'
var mancalaApp = angular.module('mancalaindex', ['ui.bootstrap', 'mancala.controllers',
    'mancala.services'
]);
mancalaApp.constant("CONSTANTS", {
	getAllPits: "/mancala/pits",
	updatePits: "/mancala/pits/",
	loadGame: "/mancala",
	status: "/mancala/status",
	currentPlayer: "/mancala/currentplayer"
    
});