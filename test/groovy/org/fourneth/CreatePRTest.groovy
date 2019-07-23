package org.fourneth


import org.kohsuke.github.GHCompare
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@Unroll
class CreatePRTest extends Specification {
    def mockedRequest = mock(PromotionRequest.class)
    def config = new PromotionRequestConfig()

    def "required to merge when identical"() {
        when:
            when(mockedRequest.createPR()).thenCallRealMethod()
            when(mockedRequest.isRequiredToMerge()).thenReturn(isRequiredToMerge)
            mockedRequest.createPR()
        then:
           thrown(exception)
        where:
            isRequiredToMerge << [true]
            exception         << [IllegalStateException]
    }
}