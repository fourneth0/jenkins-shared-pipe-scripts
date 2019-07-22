package org.fourneth

import spock.lang.Specification;

class ClientVersionApiTest extends Specification {
    def "Get the version from API"() {
        expect:
            new ClientVersionApi(
                    url: "https://jsonplaceholder.typicode.com/todos/1",
                    propertyName: 'title').version != null
    }
}
