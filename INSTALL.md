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
    
    
# First steps #

Click on the golden arrow at the top left and download the file. Open the file in a text editor, 
and you will find the following section:

    "credentials": {
        "pinataApi": "",
        "pinataApiSecret": "",
        "stampdApi": "",
        "stampdApiSecret": ""
    }
    
Now you visit the pinning service
[Pinata](https://pinata.cloud/)
and register for three. This will give
you 1 GB of space for uploads to IPFS, and
you are not charged for it!

When you finished the registration, you will
have 
`PINATA API KEY`
and
`PINATA SECRET API KEY`
as described in there documentation. Those two values
you add into the section of your credentials listed above.

**Save the file in your text editor!**  Then you drag and
drop the edited file back onto the key. You are now ready to upload files.

Find a small and unimportant file and drag and drop it onto the area
marked *Drop Zone.* See what happens: The file is uploaded into IPFS, and
it is encrypted!


 



    
    









