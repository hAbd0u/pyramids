# Will-Registration  #

**Problem to solve:** Last wills, which can ́t be found are non-existant
and won ́t be respected. We want to build a solution, where a last will
can be stored safely, updated and finally be accessible to persons
pre-defined by the testator.

## Intro: ##

We need a register, which can store text files (wills) and later also
video files of our users in a way, which assures, that the files were
actually produced by the person owning the account (Identification),
that the given document is immutable, can ́t be forged or destroyed by
third parties (safety) and holds as a proof that the document was
registered at a given moment in time (timestamp).

The user should have the possibility to access his document at any
time - as can every person who is granted access (for example the will
executor, a lawyer, notary etc). The user should also have the
possibility to upload a new file at any time and be assured, that the
newest file is actually the newest. Legally, every new will replaces
the old will as far as validity is concerned.

Our register should have different features/layers, like:
Identification, a personal account, notarization of the document,
notification, payment.

Accessing the document for others than the user goes similar through:
identification, payment, access

1. **Website, Identification, creation of a personal account**
   Identification could happen in different ways, either one of the following or a cumulative 
   solution Log-In through social media, upload of ID, ID-now connection through video; 
   face recognition through AI. For the MVP, a basic solution is absolutely sufficient.
   The personal account should have a namespace, accessible by a password
2. **Personal Account**
   The account could work like an asset container (mosaic in nem terms), where the document is stored. 
   New uploads of documents could be done by creating a sub-account of the mosaic.
3. **Upload of files into the asset container**
   - Drag & Drop solution. User writes a will by hand, takes a picture of it or scans it, and uploads it in his account.
   - The document (or later videos) themselves will be stored on a decentralized database (e.g. IPFS)
   - The document will be hashed; the hash will be stored on our private nem blockchain and in addition on the public nem blockchain.
   - On the private nem blockchain we will issue a token, with characteristics like: payback, 
     dividend payment, earning more tokens with referrals. For the tokens we will need a secure wallet built-in.
4. **Payment**
   User pays in fiat (paypal, sepa, credit card etc. or in our token (if he has any, for example through referrals)
   When payed, he gets a notification to his email-account that he has registered the document.
5. **Notification/Access to the document

User can decide if he wants to notify others about the existence of
the will or not. In any (normal) case, the heirs or anyone else
normally only should have access to the file after the death of the
testator. In the first case, the user will name the persons identity
(„beneficiairies“) to whom access should be granted after death. Those
beneficiaires could get a general message at this point saying: „I
uploaded my last will in the register xy and you can access it after
my death“

The beneficiaires could then produce proof of identity to get access
  to the passwords after „proof of death“ (through a death
  ceritificate for instance);

In the second case, we could imagine, that we inform the beneficiaires when death occurrs.



