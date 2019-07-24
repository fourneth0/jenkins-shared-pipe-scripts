package org.fourneth

import groovy.json.JsonSlurper

/**
 * This is a wrapper around version api of subjected client.
 * This is being used to check whether the expected version is indeed deployed.
 */
class ClientVersionApi {

    /**
     * Version API URL. Response should contains json object.
     */
    String url
    /**
     * version property in api response.
     */
    String propertyName = 'hash'

    /**
     * Read the version property from the given json API.
     *
      * @return value of the given property, or null
     */
    String getVersion() {
        def result = new JsonSlurper().parse(new URL(url))
        return result[propertyName]
    }

    /**
     * Check whether the given version and the deployed version from the API matches.
     *
     * @param expectedVersion
     * @return boolean
     */
    boolean isThisVersionDeployed(String expectedVersion) {
        return (expectedVersion != null
            && this.version != null
            && expectedVersion.contains(this.version))
    }
}
