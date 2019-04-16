# Pyramids! #

![](http://data.lyrx.de/images/PyramideSonnenaufgang2.png)

## Summary ##

This article is inspired by the  proof of concept application  
[Eternitas](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/).
It demonstrates a  new way of storing, encrypting and signing 
documents. Both this article and the 
[application](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/).
present these concepts.

The first part of the article is about general reflections and topics related
to the subject. The second part describes the
[application](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/).
(You may
follow the 
[link](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/),
if you keep in mind that a proof of concept is not a
ready and bullet proof application.)

There are some striking benefits coming with the concept: Your data will never get lost and
can be shared among different parties without today's restrictions and
stupid obstacles.  All sorts of businesses can be based upon
[Eternitas](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/),
also many, many use cases in legal tech.

How does that work? 

- Nothing is ever stored locally. 
- Neither is data stored in the cloud.
- No specific storage provider is used 
 
*-- So how is data stored?*

Data goes into a
distributed file system called
[IPFS](https://ipfs.io/).
IPFS is a peer-to-peer network. It is completely decentralized
and it does not directly provide storage for your data. Instead,
any peer in the network can decide to store
a file by a process called *pinning.* If a node *pins* a file, it
can serve its contents and informs all other peers in the network
about this.

This system inherently provides redundancy, as any file can be pinned
multiple times. In theory a file could be pinned by every peer in the
network. Thus a high degree of data safety can be reached, because any file
will remain available, as long as at least one peer pinning the file
remains online. But if a file is considered important, it will be
pinned many times. The network is global. No single country
or authority can decide to remove the file, because nobody is able to shut
down the complete network.

## Pinata ##

Eternitas uses [Pinata](https://pinata.cloud/) as a pinning service,
and thus as a persistence layer.  This company is a storage provider
specialized on IPFS. Their business concept is quite new: The company
is staking success on a new technology, but their future is shining
bright. Classical storage providers can never reach simplicity and
ubiquity in a way that is possible with a technology like IPFS. And their 
maintenance costs will be lower as well.

For their clients the risk is even smaller. A pinning service can be
easily replaced by another one, and not much of the underlying
technological interfaces will change. Also more than one storage
provider can be used, and this will drastically increase data
redundancy and data security.

The technology might be immature with some respects, but on the other
hand its advantages are so enormous, that its potential cannot be
estimated high enough.

## Blockstack ##

Very often, while doing something in the programming field
that you believe to be new or original, you suddenly find out
that someone else is already doing similar stuff.

Only recently I found out that 
it happened to me: I learned about 
[Blockstack](https://blockstack.org/).
This very ambitious project strives to establish a completely
new ecosystem, going far beyond just data persistence and encryption.
These people cover the persistence topic with their decentralized
storage system *Gaia*.

But with this project -- as with many others -- only future will tell
if it succeeds or fails. Not knowing about the outcomes in the future
makes working in the blockchain and P2P area so fascinating! Every
evening you go to bed and you do not know what will have changed when
you wake up the next morning. 

As I said, Blockstack is ambitious. Personally I have learned the hard
way that modesty should sometimes come before ambition, if you want
to get good results. We should keep an eye on Blockstack!

## Privacy in a peer-to-peer world ##

So how can we be sure to control our data, keep it secure
and make it available only to people we trust? There are basically
two possibilities:

1. Encrypt your data before storing it with IPFS
2. Use a private IPFS network

The second possibility is well suited for large companies or
institutions: Here you control all of the IPFS peers in your own
network, but you will have only as many peers as you manage to setup
yourself. If your company goes bankrupt or your institution fails to
persist for whatever reason, your network will go down. You will loose
most of the resilience that can come with a public and world wide
network, but you may gain confidence and trust, as you shield
yourself against many threats from the outside.

Still I am sure that many of the global players will go
with possibility two -- if they consider such a network at all. 
The first possibility just means that your data is distributed
all over the world, even if it is encrypted and unreadable as long
as you control your encryption keys. So in this case your data
is just as secure as your cryptographic competence is high. 
But many companies are not so self confident in this respect. 
There are so many stories about data leaks. Every month 
new incidents happen. This is why many people will try to close
things up as much as they can -- and they will go with
possibility two.

In the end it will depend on your use case which solution is the
best for you. And it will mainly depend on your specific
security requirements.

## The Pyramids-Metaphor ##

Here we will call the place you use for data storage
**pyramids.**
This alludes to ancient pharaohs who tried to become immortal by getting
buried in huge graves like the Cheops pyramid. Today we state that in our
*Pyramids* your data will be preserved **eternally and securely**, 
just like the pharaohs
hoped would be the case for their dead bodies.

Inside a *Pyramid* data is stored in CHAMBERS, and each
CHAMBER is sealed. With *sealed* we mean that  data
cannot be changed without breaking the seal,
and so a change  can never remain unnoticed.

This application always encrypts data, and the encryption is done on your
machine inside your web browser. So, no unencrypted information about
you and your data will ever leave the browser.

## Signatures ##

You can also *sign* your data by applying a digital signature
to it. Such a signature can be compared to a conventional
signature done with a pen, but it is executed by means of modern
cryptography.

By signing your data you obtain the same effect as with a conventional
signature: You can express your consent with the contents, just as you
do by signing a contract presented to you on a piece of paper.
Or it confirms that you have written a document yourself.

In many cases a document only gets valuable or important, if it is signed.
Also, a document can be signed by multiple parties, just
like in the real world. 


### Is this a DApp? ###


**In a way: yes!** But there is so much hype going on about
[DApps](https://ethereum.stackexchange.com/questions/383/what-is-a-dapp),
that this leads to a lot of confusion, misunderstandings and exaggerated
expectations.

It is not yet true, that any usecase can be completely built on top of
P2P-Networks solely.  Neither smart contracts nor decentralized
storage networks like IPFS can replace today's mainstream
infrastructure completely. DApps are still not ready for prime time. *I
wish they were!*

Various questions remain unanswered, and in some areas there are so
many competing efforts going on, it simply cannot be foreseen
which one of them will prevail. Take blockchains for example: In software development
it is needed -- given a specific requirement -- that there is only a limited
number of blockchain platforms can be considered to be suitable. This number
must not be too large, so that a choice can be made without to much risk. 
It needs to be sure, that in a couple of years the chosen blockchain will
still be maintained and successful. But the current situation is not yet clear
enough to allow such well thought decisions.

Today one can just do best effort. So I personally define a DApp in a
slightly different way: **A DApp is a web application that tries to
avoid centralization as far as possible.** In this modest sense,
Eternitas is really a DApp, because everything possible has been done
to avoid central servers.

There is one huge advantage of DApps many people are not yet aware of:
**It is less work to program them.** Ordinary web applications are vastly
more complex. This is not so obvious, as not many programmers are trained
in programming DApps. Know How, documentation and 
the needed programming infrastructure (support for common IDEs
for example) are not widely available. That's why things seem to be
more complicated than they really are.

For a conventional web application you normally need a backend.  The
web browser running the frontend communicates with this backend.  A
browser does not only receive the web layout through servers, but most
other information as well.  The programming logic tends to be
implement on the server. There usually are several communication
protocols responsible for the data exchange between client and server.

All this needs to be *configured or even programmed*. Namely the
server(s) need(s) to be *configured or even programmed* -- and need(s) to
be maintained during the whole life cycle of an application. Things
are getting **so much easier** if there is **no** backend at all. That 
is why there will be  DApps all over!

In a P2P-Network maintenance is shouldered by a very much larger
number of people. And first of all: You do not have to program or
configure it yourself. Its interfaces and protocols might change over
time, but they will be standardized all over the world. You as an
application developer will definitely not be the one who is
responsible for them. So these protocols will sooner or later be much
better established and supported than any proprietary solution
implement by yourself.

I did a lot of *pet projects* as a developer. This means that I was
responsible for the whole thing myself and I could do whatever I liked
without having to ask anybody. But what you find out quickly when you
do such things, that your capabilities as a programmer are always
limited, and you will most likely **never** reach more ambitious
goals, because a whole team would be needed.

So I learned the hard way that I have to live with my limited
resources and cannot go beyond them. If I had to implement and both
client and server, just to build a small proof of concept, I were more
than happy to save some work. Yet again, P2P-networks come in handy:
Half of your work is already done, if you find a way to use them,
instead of writing a completely new backend from scratch (or even
worse: use one of the existing application servers).

Yet another reason: *DApps will be much easier to merge with legacy
applications*. The reasons are similar to what has already been
described: When merging, there will be no need to merge server side
applications. For web applications, you often have the case that 
the only common environment used by all components is the 
[DOM](https://de.wikipedia.org/wiki/Document_Object_Model)
of the browser. In the best case, it can be good enough to make
some JavaScript modules coexist and make them work together with
the DOM of the web page. Finished! With conventional applications,
merging them can be almost impossible. -- But sometimes inevitable!

If two banks merge, you often have two incompatible IT platforms that
absolutely need to come together.  But merging them is an incredibly
expensive and time consuming task. Software developed during decades
is sometimes not maintainable -- even without the need of a merge. Such
modules should have been decommissioned years ago but they often remain
up and running to save costs. 

Finally the question comes up if such software components can survive
a merge at all.  That is why these mergers can fail even after years
of hard work done by many people.

In the (hopefully better) future, that sort or problems will get less
complex, if the server side is gradually getting replaced by P2P-networks.
It would even help, if such networks were internal to a company, or they
might be shared only between partnering companies or institutions. 
In any such case, merging will be easier, and also many other ways
of cooperation between organizations.

## The Key Is Cryptography ##

Cryptography gives you enormous power -- if you can handle it. That 
might also be the reason why the basics of this art is rarely
taught to young people. If the majority of the people knew about its
possibilities, they were much more difficult to control or rule. 
There is just not much motivation for governments to teach their people
such stuff. It could make citizens more autonomous and
less transparent.

A good example is encrypting e-mails. This is possible today, and
there are plugins for all major e-mail clients enabling this
feature. But it is rarely used. It always comes with a loss of
usability that many people evade. Or they might just not know
about the possibilities. It is just so easy to write a simple
e-mail. Why spoil that process and make it more complicated?
Simplicity is the main reason why e-mails have become so
successful. If you take away simplicity, people just turn away.

If you add encryption to e-mails, you mess things up.  You suddenly
need to deal with incompatibilities.  There might even be the need to
exchange public cryptographic keys. To do that, you must understand
the basics. Here we are: **It will never happen!** And for the
powerful it is always better to leave others unaware of certain things
...

However, as cryptography is still needed in many situations, it is
available almost anywhere. Best of all, it is available in browser. 

During my investigations I found out that most popular browsers
support
[SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto),
as long as the browser visits a page that is considered *secure*.
Any page  not providing a private connection and a valid
security certificate, is considered to be *insecure*. 
In this sense, all IPFS gateways that I have checked are *secure*,
and so they can serve a page providing encryption by using SubtleCrypto.

Most likely due to valid security considerations pages served by
the older `HTTP`-Protocol or any servers without security certificate
are excluded from SubtleCrypto. This implies
that content providers providing encrypted content or the possibility for
users to encrypt content cannot remain anonymous themselves. Someone always
needs to register and give away his address, if he wants to serve such content.
That might make sense in countries respecting human rights and sticking to
a somehow ethical legal system. But what about other countries?

## Encryption Once Again! ##

So how does encryption work in 
[Eternitas](https://gateway.pinata.cloud/ipfs/Qmf2Cnz214u5vQgaMqaE7BE93KKuWr6HZ162Y9YyjowWmG/)?

Saving the credentials is the tricky part, as usual. Every time the page is loaded, three
things happen:

1. A new key pair for asymmetric encryption is created.
2. A new key  for symmetric encryption is created.
3. A key pair for signatures and signature verification is created.

These are your credentials. If you click 
on the golden key in the top left corner you can
download your credentials and save them somewhere. **You
are the only person responsible for this sensitive data. You must
keep them secret and never loose them.** 

There is no conventional login process for Eternitas, as your credentials are
completely unknown to anybody but you. You can load credentials back into
the page just by dragging and dropping the file (previously created
by clicking on the golden key) onto the golden key again. Nothing will
happen, as this application is somehow minimalist.

Actually, you need to be clever and edit your credentials to make the 
a little bit more useful. I am telling you how ...


## Edit your credentials ##


The file you have created by clicking on the golden
key represents your pyramid. It is also the key to this pyramid. So let
us give it a name.

Open the file in a text editor and look for a section
like this one: 

    "title": {
       "text": "THIS IS YOUR PYRAMID"
    }

Just change that: 

    "title": {
       "text": "PYRAMID OF ALICE AND BOB"
    }

And enter your name for the signature: 

       "sign": {
         "name" : "Alice and Bob Carter",
         "private": {
                     "crv": "P-384",
         [...]
         
(You will  find that in the section containing the signature keys)

*(OK: Using a editing JSON-File is not an easy task for most
people. There will be the need to provide a graphical user interface
in the future. We are working on it!)*

The most important part is still missing. You will need to add more credentials
to the file, if you want to make some of your data persistent. Persistence
always means that somebody has to provide disk space for you, and this person
will need to charge you for that. 

As mentioned before, we only support 
[Pinata](https://pinata.cloud/pinexplorer)
as our storage provider. So you go to their page and register.
After that you will have your *api key* and your *secret*, just as
they explain on their website, and you enter them here. 

          "credentials": {
               "pinataApi"      : "XXXXXXXXXXXXXXXXXXXX",
               "pinataApiSecret": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
 
 
## Uploading files ##

Now that you have successfully "pimped" your credentials, you  can drag and drop the file back
onto the golden key. This should change the page a little bit, and you can now
see the name of your pyramid written in the sky, and at the bottom you can find your name
as the SIGNER.

You can now drag and drop a (not too large) file onto the area marked **Drop Zone**. The file will
be encrypted and saved to IPFS. But more than that happened: You have just created and
sealed your first CHAMBER inside your pyramid. The seal of your CHAMBER is shown to you
on the left bottom of the page. The file you just uploaded is also named in *IPFS-style* and
written below the name of your pyramid.

You can add more files the same way. With each iteration, a new CHAMBER will be created containing
the new item plus all the items that you  previously added.

Each time you find a new seal on the bottom, and the list below your pyramid name is growing.

You will never find your data again, if you do not click on the link next to "CHAMBER:" and
save that file. If you want to get back what you saved, you click on one of the links 
below your pyramid name, and you can download your file again. It is automatically decrypted
for you.

Never reload the page and never click on the pyramid name, if you have not saved your CHAMBER! 
Now let us assume, that you **did save** your CHAMBER. If you click on the pyramid name now, 
the CHAMBER will be gone. If you drag and drop the file associated with your CHAMBER back
onto the **Drop Zone**, you will get everything back. But do not reload the page, because in
this case your credentials will be lost and recreated. OK -- you have already saved your
credentials. So, just reload the page now! Here we are, back at the very beginning. Now 
you drag and drop your credentials back onto the golden key, and you will be "logged in"
again. Now drag and drop your CHAMBER back onto the **Drop Zone**, and you will get all
your data back. 

## Signing ##

Your CHAMBERS are always sealed, but they are not signed yet. That
just means, that anybody could have sealed them. You cannot yet prove
that you are the one who did it. So let us sign the CHAMBER now. Click
on the feather for that. This is a complicated process. I wish you
well! It will take a while, and when it is finished and went well, you
will find your signature written above the **Drop Zone**. There is
also a new entry in the bottom line. Click on the link next to
"SIGNATURE:" to download and save your signature. It will prove that
you have sealed the CHAMBER and that you agree with its contents. It
wil also suffice to recover the contents of your CHAMBER.  The CHAMBER
can never change. Nobody can get into it, unless he has your
credentials.

## The Blue Mauritius ##

The Blue Mauritius is just the third icon on the top of the page. To be 
honest: There is no action triggered by this icon. It is unimplemented.
Later on it is supposed to help you share your CHAMBERS with others. For example,
such a CHAMBER could contain your complete inheritance. By clicking on the
stamp, you can transfer everything to your heir. 


## Hashes Or CIDs ##

It still needs to be explained what the two text fields below the **Drop Zone**
are meant for.

Both the CHAMBER and the SIGNATURE are associated with exactly one
[Hash or CID](https://docs.ipfs.io/guides/concepts/cid/) that uniquely
identifies your content. If you have that identifier, you can always
regain your data from the IPFS network. In the textfield marked *UNSIGNED* 
you can enter the CID of your CHAMBER. In the textfield marked *SIGNED*
you can enter the CID of your SIGNATURE. After you press enter, you always
get your content back. Always? Not really! You need to have your credentials
ready, just as described above.

## Conclusion ##

I could only describe the functionality in a very concise way. It will not
be obvious immediately, what the meaning of a CHAMBER really is, and what 
you can do with the SIGNATURES. Start thinking about it! You will begin 
to realize that a whole spectrum of use cases can be realized with that concept.

Do not take the pyramids-metaphor too serious. Other metaphors can fit in 
as well, and they will help you find even more implications and possibilities
of this. Maybe some imagination is needed to go further. Also, a lot of things
still need to be done. Let us go for it!


















         
           
 







        
        

    
























