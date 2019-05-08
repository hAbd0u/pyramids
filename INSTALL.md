# Some basic installation instructions #

This is a webapp meant to run
**serverless**. So it should
not be necessary to maintain or set up
any backend infrastructure.

The application can then be served by 
[public IFS gateways](https://ipfs.github.io/public-gateway-checker/),
as demonstrated 
[here](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/).

However, during development it does make sense to set up a local webserver for testing. Here
is how you can do that.

You need
[Scala SBT](https://www.scala-sbt.org/download.html)
on your machine, which in turn requires 
[Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html).

If you have both of them, you should run on the command line

    sbt fullOptJS

in the top level directory. That should provide the webapp in the directory
`src/main/webapp`

To get an easy to use local webserver for static content, install 
[NodeJS](https://nodejs.org/en/download/)
first.

And then, in the top level directory, execute on the command line

    npm install http-server -g
    
and then ...

    http-server src/main/webapp/
    
Navigate to `http://localhost:8080` and test the application!
    
    
    










