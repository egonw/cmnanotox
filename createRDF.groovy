import groovy.json.JsonSlurper

println "@prefix void:  <http://rdfs.org/ns/void#> ."
println "@prefix owl:   <http://www.w3.org/2002/07/owl#> ."
println "@prefix enm:   <http://purl.enanomapper.org/onto/> ."
println "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> ."
println "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ."
println "@prefix npo:   <http://purl.bioontology.org/ontology/npo#> ."
println "@prefix sso:   <http://semanticscience.org/resource/> ."
println "@prefix cito:  <http://purl.org/net/cito/> ."
println "@prefix bao:   <http://www.bioassayontology.org/bao#> ."
println "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
println "@prefix dcterms: <http://purl.org/dc/terms/> ."
println "@prefix obo:   <http://purl.obolibrary.org/obo/> ."
println "@prefix pmc:   <http://identifiers.org/pmc:> ."

def correction = [
  "NM300": "NM-300"
]
def nmDetails = [
  "NM-100": [ uri: "ENM_9000201", core: [ smiles: "O=[Ti]=O" ] ],
  "NM-101": [ uri: "ENM_9000202", core: [ smiles: "O=[Ti]=O" ] ],
  "NM-102": [ uri: "ENM_9000203", core: [ smiles: "O=[Ti]=O" ] ],
  "NM-103": [ uri: "ENM_9000208", core: [ smiles: "O=[Ti]=O" ] ],
  "NM-104": [ uri: "ENM_9000209", core: [ smiles: "O=[Ti]=O" ] ],
  "NM-105": [ uri: "ENM_9000204", core: [ smiles: "O=[Ti]=O" ] ],
  "NM-110": [ uri: "ENM_9000205", core: [ smiles: "[Zn]=O" ] ],
  "NM-111": [ uri: "ENM_9000210", core: [ smiles: "[Zn]=O" ], coating: [] ],
  "NM-200": [ uri: "ENM_9000211", core: [ smiles: "O=[Si]=O" ] ],
  "NM-203": [ uri: "ENM_9000214", core: [ smiles: "O=[Si]=O" ] ],
  "NM-211": [ uri: "ENM_9000226", core: [ smiles: "O=[Ce]=O" ] ],
  "NM-212": [ uri: "ENM_9000227", core: [ smiles: "O=[Ce]=O" ] ],
  "NM-220": [ ],
  "NM-300": [ uri: "ENM_9000235", core: [ smiles: "[Ag]" ] ],
  "NM-400": [ uri: "ENM_9000206", core: [ smiles: "[C]" ] ],
  "NM-401": [ uri: "ENM_9000207", core: [ smiles: "[C]" ] ],
  "NM-402": [ uri: "ENM_9000229", core: [ smiles: "[C]" ] ],
]

def run = "run1" // ************************************************************************** UPDATE *******************************
println "@prefix ex:    <https://egonw.github.io/cmnanotox/${run}/> ."

def inputFile = new File("nanotox/articles.json")
def inputJSON = new JsonSlurper().parseText(inputFile.text)
nmCounter = 0
nmMap = new java.util.HashMap();
discussesDone = new java.util.HashSet();
inputJSON.keySet().each { pmcid ->
  if (inputJSON[pmcid].amiResults) {
    amiResult = inputJSON[pmcid].amiResults
    if (amiResult.jrc) {
      amiResult.jrc.each { nmUse ->
        nmLabel = nmUse.value0
        if (correction[nmLabel]) nmLabel = correction[nmLabel]
        if (!nmMap.containsKey(nmLabel)) {
          nmCounter++
          nmMap.put(nmLabel, "M" + nmCounter)
          nmRes = nmMap.get(nmLabel)
          print "ex:${nmRes} a obo:CHEBI_59999 ; rdfs:label \"${nmLabel}\" "
          coreContent = ""
          if (nmDetails[nmLabel]) {
            if (nmDetails[nmLabel].uri) print "; dcterms:type enm:${nmDetails[nmLabel].uri} "
            if (nmDetails[nmLabel].core && nmDetails[nmLabel].core.smiles) {
              print "; npo:has_part ex:${nmRes}_core "
              coreContent += "ex:${nmRes}_core a npo:NPO_1888 ; sso:CHEMINF_000200  ex:${nmRes}_core_smi .\n"
              coreContent += "ex:${nmRes}_core_smi a sso:CHEMINF_000018 ; sso:SIO_000300  \"${nmDetails[nmLabel].core.smiles}\" .\n"
            }
          }
          println " ."
          if (coreContent != "") print coreContent
        }
        if (!discussesDone.contains("${nmRes}${pmcid}")) {
          println "ex:${nmRes} cito:isDiscussedBy pmc:${pmcid} ."
          discussesDone.add("${nmRes}${pmcid}")
        }
      }
    }
  }
}
