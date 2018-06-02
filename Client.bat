echo off
title Connect 4 Server
cd src
javac view/PlayerGUI.java
set /P host=Enter the host IP (or localhost): 
set /P port=Enter the port: 
java -Xmx100m view.PlayerGUI %host% %port%
cd ..
call clean.bat
pause