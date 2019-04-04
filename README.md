[![Build Status](https://travis-ci.org/dogecoin/libdohj.svg?branch=master)](https://travis-ci.org/dogecoin/libdohj)

### Welcome to libdohj 

The libdohj library is a lightweight wrapper library around the bitcoinj Java library,
enabling support for Dogecoin (pull requests for support for other altcoins would
be welcomed). 

Sexcoin's android wallet is now based on this project. There were a couple 
of additions to bitcoinj in order for sexcoin's main client and sexcore-sexcoin to be able
to interract with bitcoinj. These changes are in https://github.com/lavajumper/bitcoinj 
repository. We are making every effort to be able NOT to touch bitcoinj with the hope that 
it will be one less dependency that needs to be customized.

### Getting started

To get started, it is best to have the latest JDK and Maven installed. The HEAD of the `sexcoin` branch 
contains the latest development code.
You should be familiar with bitcoinj first, as this library simply adds minor
changes to extend bitcoinj. Generally using libdohj is equivalent to using
bitcoinj, except with different network parameters (reflecting Sexcoin consensus
in place of Bitcoin) and also including code to make working with Age Verification hopefully seamless.

Be aware however that altcoin blocks have their own class, AltcoinBlock, which
adds support for features such as AuxPoW.

Sexcoin also has folded in support for native KGW. THIS IS TRICKY TO WORK WITH. There are precision issues which
crop up when verifiying POW. As of right now these have not been completely dealt with. Hopefully in the near future
we will be able to make KGW calculations reliable.

#### Building from the command line

To perform a full build use
```
mvn clean package
```
You can also run
```
mvn site:site
```
to generate a website with useful information like JavaDocs.

The outputs are under the `target` directory.

#### Building from an IDE

Alternatively, just import the project using your IDE. [IntelliJ](http://www.jetbrains.com/idea/download/) has Maven integration built-in and has a free Community Edition. Simply use `File | Import Project` and locate the `pom.xml` in the root of the cloned project source tree. 

