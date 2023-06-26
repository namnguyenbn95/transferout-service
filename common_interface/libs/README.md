# ECDH(Elliptic-curve Diffie–Hellman) for linux

# Install Instruction

1. Download Download the binary distribution and extract to any directory. The download contains the following files:
   ecdh.jar libECDH-{bankCode}.so README.md test/

2. Extract to any Directory

3. Run test to Verify it Works:
   cd test sh run.sh

4. Using System.load and System.loadLibrary("ECDH-{bankCode}") to load a Native Shared Library try {
   System.loadLibrary("ECDH-sme"); System.out.println("load ECDH successfully!"); } catch (UnsatisfiedLinkError e) {
   e.printStackTrace(); }

5. classpath and using JAR archives See: Java JAR Archives and classpath on Linux

# Function List:

	(*) Creat MAC (SHA-265)
    public static String mc(String d, String mk) throws SecurityException, Exception
		- Param        :
			+ d : (String)  data to MAC
			+ mk: (String)  MAC key

    	- Data response: (String) mac

		- Old function: public static byte[] createMac(byte[] bData, byte[] keybyte) throws NoSuchAlgorithmException, InvalidKeyException

		- Example: 
			+ Old:
				String mac = new String(Base64.encode(HMAC256.createMac(dataToSign.getBytes(), macKey.getBytes())))
			+ New:
				String mac = ECDH.mc(dataToSign, macKey);

	(*) Get access key
    public static String gAcK(String p, String mk) throws SecurityException, Exception
		- Param        :
			+ p : (String)  phone number
			+ mk: (String)  access token key

    	- Data response: (String) access key

		- Old function: public String getAccessKey(String sThread, String phone, String AccessTokenkey)

		- Example: 
			+ Old:
				String accessKey = new Security().getAccessKey(sThread, phone, accessTokenkey);
			+ New:
				String accessKey = ECDH.gAcK(phone, accessTokenkey);

	(*) Encrypt (AES/CTR/NoPadding)
    public static String en(String d, String ek) throws SecurityException, Exception
		- Param        :
			+ d : (String)  data to encrypt
			+ ek: (String)  encryption key

    	- Data response: (String) cipher text

		- Old function: public static String encrypt(String strSecretKey, String text) throws Exception

		- Example: 
			+ Old:
				String cipherText = AES256Service.encrypt(key, rawText);
			+ New:
				String cipherText = ECDH.en(rawText, key, 16);

	(*) Decrypt (AES/CTR/NoPadding)
    public static String de(String d, String ek) throws SecurityException, Exception
		- Param        :
			+ d : (String)  data to decrypt
			+ ek: (String)  decryption key

    	- Data response: (String) decrypt text

		- Old function: public static String decrypt(String strSecretKey, String decrypt) throws Exception

		- Example: 
			+ Old:
				String decryptText = AES256Service.decrypt(key, cipherText);
			+ New:
				String decryptText = ECDH.en(cipherText, key, 16);

	(*) PreDecryption request data: handling message from client
    public static PreData prD(String d) throws SecurityException, Exception
		- Param        :
			+ d : (String)  request body text

    	- Data response: (com.vnpay.ECDH.entity.PreData) base object contains the key info and data

		- Old function: public keyidresponse getBaseData(String sEncrypt)

		- Example: 
			+ Old:
				keyidresponse k = new Security().getBaseData(sEncrypt);
			+ New:
				PreData preData = ECDH.prD(sEncrypt);

	(*) Parser request data: decrypt message from client
    public static String pR(String e, String k, String t, String n, String s, String puk, String prk) throws SecurityException, Exception 
		- Param        :
			+ e  : (String)  cipherText
			+ k  : (String)  key id
			+ t  : (String)  timstamp
			+ n  : (String)  nonce
			+ s  : (String)  signature
			+ puk: (String)  client public key
			+ prk: (String)  server private key

    	- Data response: The raw data in response

		- Old function: public response decrypt(String sEncrypt, String sSignData, String sClientPublicKey, String sServerPrivateKey) 

		- Example: 
			+ Old:
				response rp = new Security().decrypt(sEncrypt, sSignData, sClientPublicKey, sServerPrivateKey);
			+ New:
				String rawData = ECDH.pR(preData.getE(), preData.getK(), preData.getT(), preData.getN(), preData.getS(), sClientPublicKey, sServerPrivateKey);

	(*) Format response data: encapsulates the response message to the client
    public static String fR(String d, String kId, String puk, String prk) throws SecurityException, Exception
		- Param        :
			+ d  : (String)  raw data
			+ kId: (String)  key id
			+ puk: (String)  client public key
			+ prk: (String)  server private key

    	- Data response: (String) The cipher data.
		- Old function: public response encrypt(String sClearData, String sKeyID, String sClientPublicKey, String sServerPrivateKey)

		- Example: 
			+ Old:
				response rp = new Security().encrypt(sClearData, sKeyID, sClientPublicKey, sServerPrivateKey);
			+ New:
				response rp = ECDH.fR(sClearData, sKeyID, sClientPublicKey, sServerPrivateKey);

	(*) ECC keypair generate: Generate a ECC key pair
    public static KeyPair kpG() throws SecurityException, Exception
		- Param        :

    	- Data response: (com.vnpay.ECDH.entity.KeyPair) object contains key pair

		- Old function: 

		- Example: 
			+ Old:
				
			+ New:
				KeyPair keyPair = ECDH.kpG();

	(*) Make common key: Make a common key to send client
    public static String mkCmk(String kId, String puk) throws SecurityException, Exception
		- Param        :
			+ kId: (String)  key id
			+ puk: (String)  server public key

    	- Data response: (String) Cipher text of common key.

		- Old function: 

		- Example: 
			+ Old:
				
			+ New:
				String cmkCipher = ECDH.mkCmk(kId, puk);

	(*) Decrypt common key: Decrypt common key
    public static String cmkDe(String cmk) throws SecurityException, Exception
		- Param        :
			+ cmk: (String)  Cipher text of common key.

    	- Data response: (String) raw text of common key.

		- Old function: 

		- Example: 
			+ Old:
				
			+ New:
				String commonKey = ECDH.cmkDe(cmkCipher);

# Error Code:

		- SUCCESS = "00";

		- ERROR = "01";

		- NprD_COMMON_ERROR_101 = "101";
		- NprD_INVALID_INPUT_102 = "102";
		- NprD_INVALID_INPUT_103 = "103";

		- NpR_COMMON_ERROR_201 = "201";
		- NpR_INVALID_SIGNALTURE_202 = "202";

		- NfR_COMMON_ERROR_301 = "301";
		- NfR_SIGN_ERROR_302 = "302";
		- NfR_ENCRYPT_ERROR_303 = "303";

		- NrG_COMMON_ERROR_401 = "401";

		- NgAcK_COMMON_ERROR_501 = "501";
