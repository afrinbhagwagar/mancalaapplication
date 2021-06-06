# Mancala

Mancala spring application - Two player.

## Implementation
A) Back-end controller calls :

1. Create new game :
	URL : /mancala
	DESCRIPTION : Creates a new game with 2 players and their pits initialized accordingly.
   
2. Sort Pieces :
	URL : /mancala/pits/<<pitId>>
	DESCRIPTION : Amount of stones from input pitId adjusting to other pits.
	
3. Board Pits :
	URL : /mancala/pits
	DESCRIPTION : Gets value of stones from each pit for both players.
	
4. Board Status :
	URL : /mancala/status
	DESCRIPTION : Gets status whether game finished or not, which player would be the winner.
	
5. Current Player : 
	URL : /mancala/currentplayer
	DESCRIPTION : Gets the player whose turn would be the next turn.

B) Access with UI :
On starting up the application, you can visit /home to get the UI of this application.

Eg : http://localhost:8080/home