package org.fourneth

import org.kohsuke.github.GHCommitState
import org.kohsuke.github.GHCommitStatus
import org.kohsuke.github.GHCompare
import spock.lang.Specification
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Unroll

import static org.mockito.Mockito.*

@Unroll
class IsRequiredToMergeTest extends Specification {
    def mockedRequest = mock(PromotionRequest.class)
    def config = new PromotionRequestConfig()

    def "required to merge when identical"() {
        when:
            when(mockedRequest.isRequiredToMerge()).thenCallRealMethod()
            def compareStatus = mock(GHCompare.class)
            when(compareStatus.status).thenReturn(status)
            when(mockedRequest.compareBranches()).thenReturn(compareStatus)
        then:
            mockedRequest.isRequiredToMerge() == result
        where:
            status << [GHCompare.Status.identical, GHCompare.Status.ahead, GHCompare.Status.behind]
            result << [false, false, true]
    }
}