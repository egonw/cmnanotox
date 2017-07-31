import groovy.json.JsonSlurper

def inputFile = new File("nanotox/articles.json")
def inputJSON = new JsonSlurper().parseText(inputFile.text)
inputJSON.keySet().each { pmcid ->
  if (inputJSON[pmcid].amiResults) {
    amiResult = inputJSON[pmcid].amiResults
    if (amiResult.jrc) {
      amiResult.jrc.each { nmUse ->
        edgeID = "e" + Math.abs((pmcid + nmUse.value0).hashCode())
        println "{ data: { id: '$pmcid', faveColor: 'red' } }, { data: { id: '${nmUse.value0}', faveColor: 'blue'  } }, { data: { id: '$edgeID', source: '$pmcid', target: '${nmUse.value0}' } },"
      }
      if (amiResult.binomial) {
        amiResult.binomial.each { nmUse ->
          edgeID = "e" + Math.abs((pmcid + nmUse.exact).hashCode())
          println "{ data: { id: '$pmcid', faveColor: 'red' } }, { data: { id: '${nmUse.exact}', faveColor: 'green'  } }, { data: { id: '$edgeID', source: '$pmcid', target: '${nmUse.exact}' } },"
        }
      }
    }
  }
}
