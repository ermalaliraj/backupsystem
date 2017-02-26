# backupsystem

RU: Listen on queue/remoteCMD for commands from Server. 
Commands can be: startSending, stopSending, getStatus, testSending.
Message sent from SERVER to RU are synchronous. For each request the server waits the reply from the RU which can be. ACK, ERROR or a Serialized object.

SERVER: Listen on queue/scontent for files sent by the Remote Unit.
Message sent from RU to SERVER  are Asynchronous. RU sends the files and forget about it.





COMMANDS (SERVER to RU)
1. Start Sending. After this command the RU start sending files to the server with a configured frequency.  Starts a Sending service.

2. Stop Sending. With this command the Server says to RU to stop sending files. It stops the service created in step 1.

3. Get Status. Ask the actual status from the RU. The reply from RU can be AVAILABLE (no sending service running, or BUSY (a sending service is running)

4. Test sending. Without running a sending service SERVER asks from RU a block of files. Useful to test the connection before starting a service. 

SENDING SERVICE (RU to SERVER)
After a sending service is started, the RU starts sending files to the server with a ENV configured frequency SEND_FILES_TIMEOUT.
ENV value PATH_FILES_TO_BE_SEND is used from RU to know the path with files ready to be send ex: D:\backupsystem\ru\FILES_TO_SEND
