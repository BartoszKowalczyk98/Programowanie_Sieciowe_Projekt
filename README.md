# Programowanie_Sieciowe_Projekt
Final project for network programming in university 3rd year software engineer

2 main folders 
Clientside consists of java files responsible for client side of project. 
ClientGui.java and ClientGui.form contain code responsible for visual aspect of this part of program, of client part.
Clien.java is the main class, it is responsible for maintaining order in classes, and starts most of the threads used in this part.
ClientThread.java is thread class used to exchange information with server.
UDPDiscoverClient.java is used to get list of available servers when client is not connected to any.

Serverside consists of java files responsible for server side of project.
ServerGUI.java and ServerGUI.form contain code responsible for visual aspect of this part of program, of the server part at least.
Server.java is the main class for server side of program, it starts other classes as threads, and is the class used to start a server.
ServerClientHandler.java is the class that when started as a thread exchanges information with client and informs main class when client disconnected.
UDPDiscoverHandler.java is the class that uses multicast to send out server offers to clients.

Links that were useful to me and were used to some extend in my code:
https://stackoverflow.com/questions/21330682/confirmation-before-press-yes-to-exit-program-in-java
https://stackoverflow.com/questions/6446881/java-pop-up-window-to-ask-for-data
https://www.baeldung.com/java-broadcast-multicast
