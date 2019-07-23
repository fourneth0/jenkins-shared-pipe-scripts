package org.fourneth

import groovy.json.JsonSlurper

class ClientVersionApi {

    String url
    String propertyName = 'hash'

    // todo documentation
    String getVersion() {
        def result = new JsonSlurper().parse(new URL(url))
        return result[propertyName]
    }

    boolean isThisVersionDeployed(version) {
        return version.contains(this.version)
    }
}
