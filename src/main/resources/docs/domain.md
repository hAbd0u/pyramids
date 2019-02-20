---
title: Your Last Will As A Smart Contract
author: Alexander Weinmann
created: 2019-02-19T09:35:53+01:00
---

![](http://data.lyrx.de/images/Eternitas.png)


## The Blockchain Part Of the Eternitas Domain Modell ##

### Description ###

It is necessary to reduce the Eternitas use case in such a way, that only
the parts related to the *delegation of trust* will be implemented
by blockchain technology, and nothing else. 

Any blockchain -- still at the time of
writing -- can be considered as a *distributed public ledger*, and nothing
else. 

This implies that a blockchain cannot be considered to be a database or 
"full blown" application platform.
On the contrary, it should **only** be used for the "trust related" parts of any asset transfer
and the applications related to it
(Of course an inheritance case can be seen as a special case of an asset
transfer).

We see the following actors:

1. *Testator* 
   - Can identify towards the smart contract
2. *Heir* (One or many)
   - Can identify towards the smart contract
3. *Eternitas Smart Contract*
   - Registers last wills
   - Registers heirs
   - Is the death switch
   - Registers the password manager
4. *Password Manager*: 
   - Maintains access 
   - Can identify towards the smart contract
5. *Parish Register*: 
   - Can identify towards the smart contract
   - Pulls the death switch
   
In the following scenario, we use the (somewhat generic) concept
of any public key infrastructure. You should therefore already 
know what a cryptographic 
key pair is -- or somehow understand the concept of asymmetric encryption and
message transfers.

It is also essential to understand, that a smart contract by itself
should not store a testament or last will or any other type of
conventional data. 

To identify an actor, a smart
contract will store this actor's  *public key* or *blockchain address*.

To reference a document, the smart contract will only store its hash code, 
and not the document itself, not even its storage location
In the following, if we say *"register"*, we mean that an actor's *blockchain address*
is stored inside a smart contract corresponding to his or hers role.


We will then end up with the following work flow representing an inheritance
use case:

1. The *Testator is registered in the smart contract* 
   - *blockchain address of the testator*
   - For each heir: Hash of the inheritance document
2. *Heirs*, *Password Manager* and *Parish Register* register to the smart contract
3. Death: The *Parish Register* pulls the death switch inside the smart contract, confirming
   the death of the *Testator*
4. *Password manager* in action
    - Pulls the information about the lethal in incident from the *smart contract* 
    - Can verify any heir any time by their *blockchain addresses* registered to the
    smart contract
    - As for  each heir an individual inheritance document is registered inside the
    smart contract by its hash code, the *password manager* will send an encrypted
    document to each heir containing the inheritance information (may include passwords
    or any other sensitive data
    ).

### Implementation ###

Every actor ...

1. *Testator*
2. *Heir*
3. *Smart Contract*
4. *Password Manager*
5. *Parish Register*

... can be seen as a software module and can be
implemented independently of the other modules.


| Actor      | Software Component |
| ----------- | ----------- |
| *Smart Contract*| **Only** blockchain component. Every other component depends on it (No other dependendies between components in this project)|
| *Testator*   | Corresponds to the registry for last wills. The testator just testifies his will in a tamper proof way |
| *Password Manager*| This needs to be an **external component** connected to the blockchain interface|
| *Parish Register*| Most simple, if not trivial module. Can  be implemented such that any real institution or Eternitas would press the death trigger |
| *Heir*   | Heirs need to be registered and identifiable. One document must be mapped to exactly one heir. (Hashing)|


There will be one more software component, not visible
in the diagram: **The interface to the smart contract.**
It will be very easy to design that interface, if some
important prerequisites are taken into account. The interface
...

1. Needs to be "functional" in the sense of the functional
   programming paradigm
2. Interaction with the smart contract needs to be asynchronous 
   ("event driven")
3. There need not be any complex data structures or large amounts
   of data exchanged between the *smart contract*
   and the other components. It is always sufficient to communicate
   via basic data times (booleans, numbers or string representing 
   blockchain addresses or public keys)
4. Because of this argument, the interface can easily be designed
   
### Security ###

The level of security  in this application will mainly be determined 
by the security inside the blockchain and the smart contract, plus
the level of security of the password manager. 

As it is very difficult to write secure software, it seems to be 
necessary to rely on specialized tools as much as possible. This
is one of the reasons, why the password manager cannot be self written.
It needs to be the adoption of an already existing software tools. 

The smart contract on the other hand can only be a realiable piece
of software, if it remains very simple. As outlined in this concept,
this would be the case.







