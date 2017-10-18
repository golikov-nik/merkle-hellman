# Index
This repository is the implementation of Merkle-Hellman knapsack cryptosystem in Java. 

# Getting started
To run this program you need to have JRE  1.8 or higher installed.
Download jar file from [here](https://github.com/golikov-nik/merkle-hellman/releases) and run it via command `java -jar merkle-hellman.jar`.

# Usage
Use the following syntax:
`java -jar merkle-hellman.jar <Mode> [<Input parameters>]`
List of modes available:
- Key generator (kg, keygen)
- Encryptor (en, encrypt)
- Decryptor (de, decrypt)
To view help for input parameters use parameter `-h` or `--help`.

# Example
1. To generate keys, run command:
`java -jar merkle-hellman.jar kg`
It will generate public and private keys for encoding messages of length up to 16 and put them in current directory named `mh.privatekey.txt` and `mh.pubkey.txt`.
2. Let `hello` be the sequence we want to encrypt. Run command:
`java -jar merkle-hellman.jar en -f mh.pubkey.txt -s hello`

This will print the encrypted string in your terminal.
3. To decrypt the message, run command:
`java -jar merkle-hellman.jar de -f mh.privatekey.txt -s <encrypted string>`
`hello` will be printed to your terminal.

# License
The MIT License (MIT)
