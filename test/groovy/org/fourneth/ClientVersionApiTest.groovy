package org.fourneth

import spock.lang.Specification

class ClientVersionApiTest extends Specification {
    def "Get the version from API"() {
        expect:
        new ClientVersionApi(
                url: "https://jsonplaceholder.typicode.com/todos/1",
                propertyName: 'title').version != null
    }

    def "isThisVersionDeployed: invalid property and arg"() {
        when:
        def api = new ClientVersionApi(
                url: "https://jsonplaceholder.typicode.com/todos/1",
                propertyName: propertyName)
        then:
        api != null
        api.isThisVersionDeployed(expectedVersion) == result

        where:
        propertyName    << ['id', 'id',   'id', 'noid', 'noid']
        expectedVersion << ['1',  '2',    null,  null,   '2'  ]
        result          << [true, false,  false, false,  false]
    }

}
