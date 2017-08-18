# ContentMining Nanotoxicity literature

The purpose of this project is to make more nanosafety literature indexed and released as CCZero data.

# Installing dependencies (Debian GNU/Linux)

Get a recent NodeJS version:

    $ curl -s https://deb.nodesource.com/gpgkey/nodesource.gpg.key | sudo apt-key add -
    $ sudo nano /etc/apt/sources.list.d/nodesource.list

The content of that file should look like:

    deb https://deb.nodesource.com/node_6.x stretch  main
    # deb-src https://deb.nodesource.com/node_6.x stretch main

Then run the following command to install some of the components:

    $ sudo npm install --global getpapers
    $ sudo npm install -g ctj

Furthermore, you need to install Norma and AMI.

# Getting Papers

The following should one article (via website), but find 1.6M articles :( Clearly, something is broken...
  
    $ getpapers -q 'JRCNM01000a OR JRCNM01001a OR JRCNM01002a OR JRCNM01003a OR JRCNM01004a OR JRCNM01005a OR JRCNM01100a OR JRCNM01101a OR JRCNM02000a OR JRCNM02001a OR JRCNM02002a OR JRCNM02003a OR JRCNM02004a OR JRCNM02004b OR JRCNM03300a OR JRCNM03301a OR JRCNM04000a OR JRCNM04001a OR JRCNM10201a OR JRCNM10404 OR JRCNM62001a OR JRCNM62002a OR JRCNM62101a' -o nanotox -x

The next command does work, and returns about 470 articles:

    $ getpapers -q '"titanium dioxide" AND toxicology' -o nanotox -x

# Normalizing the XML files into Scholarly HTML

    $ norma --project nanotox -i fulltext.xml -o scholarly.html --transform nlm2html

# Extracting of facts with AMI

The following two ami runs do not seem to give much results, but at least some:

    $ ami2-species --project nanotox -i scholarly.html --sp.species --sp.type genus
    $ ami2-species --project nanotox -i scholarly.html --sp.species --sp.type binomial
  
Counting words is not a problem:
  
    $ ami2-word --project nanotox --w.words wordFrequencies --w.stopwords stopwords.txt

It does not find any use of JRCNM codes, confirmed with a grep search. Therefore, a search on NM-100-like codes is persued:

    $ ami2-regex --project nanotox --context 25 25 -i scholarly.html --r.regex jrccodes.xml
  
The content of jrccodes.xml is:

    <compoundRegex title="jrc">
      <regex fields="jrc" weight="2.0">NM[-]?\d\d\dK</regex>
      <regex fields="jrc" weight="1.0">NM[-]?\d\d\d</regex>
      <regex fields="jrc" weight="2.0">JRCNM\d\d\d\d\d\d?[ab]</regex>
      <regex fields="jrc" weight="1.0">JRCNM\d\d\d\d\d\d?"</regex>
    </compoundRegex>

After extracting all this information, it can be integrated into a single JSON file:

    $ ami2-sequence --project nanotox --filter file\(\*\*/results.xml\) -o sequencesfiles.xml
    $ ctj collect -p nanotox -M -o nanotox -s -g bionomial,genus,jrc,frequencies

# Data Analysis

Then we can start the analysis of the extracted data.

## A Cytoscape graph

I created a Groovy script to extract the bits I am interested in to create a graph
linking the JRC NM codes to articles in species mentioned in those articles:

    $ groovy createNetwork.groovy

This creates a node/edge representation, suitable for Cytoscape.js.
Copy/paste the content into the [network.html](https://egonw.github.io/cmnanotox/network.html) to visualize it with cytoscape.js in a browser.

Similarly, RDF can be generated, close to being compatible with eNanoMapper:

    $ groovy createRDF.groovy

This generates RDF that looks like:

    ex:M2 a obo:CHEBI_59999 ; rdfs:label "NM-100" ;
      dcterms:type enm:ENM_9000201 ; npo:has_part ex:M2_core  ;
      cito:isDiscussedBy pmc:PMC4105399 .
    ex:M2_core a npo:NPO_1888 ; sso:CHEMINF_000200  ex:M2_core_smi .
    ex:M2_core_smi a sso:CHEMINF_000018 ; sso:SIO_000300  "O=[Ti]=O" .
