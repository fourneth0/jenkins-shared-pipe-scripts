package org.fourneth

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Unroll

@Unroll
class PromotionRequestConfigTest extends spock.lang.Specification {

    def "invalid construction"() {
        when:
            def config = new PromotionRequestConfig(
                    accessToken: accessToken,
                    approveToken: approveToken,
                    organization: organization,
                    repository: repository,
                    source: source,
                    target: target
            )
            config.validate()
        then:
            thrown(PowerAssertionError.class)
        where:
            accessToken  << [null, "a", ""]
            approveToken << [null, "a", null]
            organization << [null, "a", null]
            repository   << [null, "a", null]
            source       << [null, "a", null]
            target       << [null, "", null]
    }

    def "valid construction"() {
        when:
            def config = new PromotionRequestConfig(
                    accessToken:  "at",
                    approveToken: "appt",
                    organization: "org",
                    repository:   "repo",
                    source:       "source",
                    target:       "target"
            )
            config.validate()
        then:
            config != null
            config.accessToken  == "at"
            config.approveToken == "appt"
            config.organization == "org"
            config.repository   == "repo"
            config.source       == "source"
            config.target       == "target"
    }

}
