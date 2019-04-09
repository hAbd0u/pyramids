## TODOs ##


### Extend `ETERNITAS-DATA` ###

   - Field `publisher` for the public key
   - Field `date`  for the time stamp 
   - Field`signed-date` for the signed time stamp. Signature 
     with the private key

### Extend `Eternitas`-Object ###

  - New field for title
  - New field for author

One could abuse the text field such that you can enter title and author. At the moment, 
it only works with `CID`s for IPFS, and the `CID` must refer to an
`ETERNITAS-DATA`-Objekt. Syntax:

        title: This is the title
        
or

        author: Peter Sellers
        
So, if you enter this data, the `Eternitas`-Object is updated, and you need to press
the Key-Button again for saving.
        

### New buttons ###

1. Button that creates a new key pair (Representing an identity)
2. Button that creates a new key for symmetric encryptioni (Representing a use case, e.g. an inheritance case)

Both buttons: after pressing them, you need to safe the wallet again by clicking the top left key button. 


## Question to stampd ##

Hi, your service is wonderfully designed!! I have one question: We
will have different sequences of documents. Sequences will be
independent from each other, but in each sequence documents will be
ordered by creation date. So, can we stamp each document in your
service, and then query your service, specifying the sequence, so that
we can find all stamps in a specific sequence?

### Samples for stampd ###

    const qs = require('qs')
    const axios = require('axios')
    
    // const agent = new https.Agent({
    //   rejectUnauthorized: false
    // });
    
    const API_URL = 'https://stampd.io/api/v2';
    // const API_URL = 'http://localhost/stampd-v3/api/v2'
    
    // config
    const client_id = '' // get your API credentials at https://stampd.io
    const secret_key = ''
    const blockchain = ''
    const hash = ''
    
    // Auth call
    axios.get(API_URL + '/init', {
      params: {
        client_id: client_id,
        secret_key: secret_key,
      }
    })
      .then((response) => {
    
        if (response.data && response.data.code === 300) {
          console.log('login success')
          console.log(response.data)
    
          // Post call
          axios.post(API_URL + '/hash', qs.stringify({
            sess_id: response.data.session_id,
            hash: hash,
            blockchain: blockchain,
            // meta_emails: emails, // share the certificate of this stamping with a comma-separated list of emails
            // meta_notes: notes, // metadata notes
            // meta_filename: filename, // the name of the file
            // meta_category: category, // any category name
          }))
            .then((response) => {
    
              if (response.data && response.data.code === 301) {
                console.log('post hash success')
                console.log(response.data)
              } else {
                console.error('error in API call')
                console.error(response.data)
              }
    
            })
            .catch((error) => {
              console.error(error)
            })
        } else {
          console.error('error in API call')
          console.error(response.data)
        }
    
      })
      .catch((error) => {
        console.error(error)
      })
    












