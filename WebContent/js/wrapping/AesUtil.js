var AesUtil = function() { 
   this.keySize = 128 / 32; 
   this.iterationCount = 1000; 
 }; 
 

 AesUtil.prototype.generateKey = function(salt, passPhrase) { 
   var key = CryptoJS.PBKDF2( 
       passPhrase,  
       CryptoJS.enc.Hex.parse(salt), 
        { keySize: this.keySize, iterations: this.iterationCount }); 
    return key; 
  } 
  

  AesUtil.prototype.encrypt = function(salt, iv, passPhrase, plainText) { 
    var key = this.generateKey(salt, passPhrase); 
    var encrypted = CryptoJS.AES.encrypt( 
        plainText, 
        key, 
        { iv: CryptoJS.enc.Hex.parse(iv) }); 
    return encrypted.ciphertext.toString(CryptoJS.enc.Base64); 
  } 
  

  AesUtil.prototype.decrypt = function(salt, iv, passPhrase, cipherText) { 
    var key = this.generateKey(salt, passPhrase); 
    var cipherParams = CryptoJS.lib.CipherParams.create({ 
      ciphertext: CryptoJS.enc.Base64.parse(cipherText) 
    }); 
    var decrypted = CryptoJS.AES.decrypt( 
        cipherParams, 
        key, 
        { iv: CryptoJS.enc.Hex.parse(iv) }); 
    return decrypted.toString(CryptoJS.enc.Utf8); 
  } 
  
  
  AesUtil.prototype.createpassPharse = function(){
	  
	  var chars = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
	  
	  var passPhrase='';
	  for(i in [1,2,3,4,5]){
		  passPhrase += chars[ Math.floor(Math.random()*23) ] ;
	  }
	  
	  return passPhrase;
  }
