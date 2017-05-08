# transfertcp
Client and server application, file transfer and execution

Transfertcp is an application that allows the connection between one or several clients with a server, established by the TCP protocol, in addition to the transfer of files, transfertcp also allows to execute the different formats of the same, for the windows platform, also adds a Player based on the popular JLayer library, so you must implement this tool in the java transfertcp project

![image transfertcp](https://dl.dropboxusercontent.com/s/6lsoowfaeikiyux/transfertcp.PNG?dl=0)

#### Clone repository

`
 git clone git://github.com/joalcapa/transfertcp.git
`

`
 cd transfertcp
`

### Compilation of transfertcp

Compilation of the project, using the file containing the main method, in order to generate the necessary ".class" files to execute transfertcp without the ".jar", remember to add the jdk of java, to the environment variables, to recognize the command "javac".

### SERVER

`
 javac transfertcp/Server.java
`

### OR CLIENT

`
 javac transfertcp/Cliente.java
`

### Execute transfertcp

### SERVER

`
 java transfertcp/Server.java
`

### OR CLIENT

`
 java transfertcp/Cliente.java
`
