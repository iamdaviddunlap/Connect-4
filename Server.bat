echo off
title Connect 4 Server
cd src
javac server/Server.java
set /P port=Enter the port on which to run the server: 
java -Xmx100m server.Server %port%
cd ..
call clean.bat
pause