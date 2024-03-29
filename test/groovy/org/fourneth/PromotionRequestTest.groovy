package org.fourneth

import org.kohsuke.github.GHCommitState
import org.kohsuke.github.GHCommitStatus
import spock.lang.Unroll

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@Unroll
class PromotionRequestTest extends spock.lang.Specification {

    def mockedRequest = mock(PromotionRequest.class)

    def "should not merge when identical"() {
        when:
            when(mockedRequest.createPR()).thenCallRealMethod()
            when(mockedRequest.isRequiredToMerge()).thenReturn(isRequiredToMerge)
            mockedRequest.createPR()
        then:
            thrown(exception)
        where:
            isRequiredToMerge << [false]
            exception         << [IllegalStateException]
    }

    def "test the build status, when no status available"() {
        setup:
        when(mockedRequest.listBuildStatuses()).thenReturn([])
        when(mockedRequest.hasAllStatusPassed()).thenCallRealMethod()
        expect:
        !mockedRequest.hasAllStatusPassed()
    }

    def "When status is pending"(){
        setup:
        def status = mockCommitStatus('context1', GHCommitState.PENDING);
        when(mockedRequest.listBuildStatuses()).thenReturn([ status ])
        when(mockedRequest.hasAllStatusPassed()).thenCallRealMethod()
        expect:
        !mockedRequest.hasAllStatusPassed()
    }

    def "When status is success"(){
        setup:
        def status = mockCommitStatus('context1', GHCommitState.SUCCESS);
        when(mockedRequest.listBuildStatuses()).thenReturn([ status ])
        when(mockedRequest.hasAllStatusPassed()).thenCallRealMethod()
        expect:
        mockedRequest.hasAllStatusPassed()
    }

    def "When status is success with multiple status "(){
        setup:
        def context1Success = mockCommitStatus('context1', GHCommitState.SUCCESS)
        def context2Success = mockCommitStatus('context2', GHCommitState.SUCCESS)
        def context1Pending = mockCommitStatus('context1', GHCommitState.PENDING)
        def context2Pending = mockCommitStatus('context2', GHCommitState.PENDING)
        when(mockedRequest.listBuildStatuses()).thenReturn([
                context1Success,
                context2Success,
                context1Pending,
                context2Pending
        ])
        when(mockedRequest.hasAllStatusPassed()).thenCallRealMethod()
        expect:
        mockedRequest.hasAllStatusPassed()
    }

    def "When status is mixed with multiple status "(){
        setup:
        def context1Success = mockCommitStatus('context1', GHCommitState.SUCCESS)
        def context1Pending = mockCommitStatus('context1', GHCommitState.PENDING)
        def context2Pending = mockCommitStatus('context2', GHCommitState.PENDING)
        when(mockedRequest.listBuildStatuses()).thenReturn([
                context1Success,
                context1Pending,
                context2Pending
        ])
        when(mockedRequest.hasAllStatusPassed()).thenCallRealMethod()
        expect:
        !mockedRequest.hasAllStatusPassed()
    }


    def "required to merge when identical"() {
        when:
        when(mockedRequest.createPR()).thenCallRealMethod()
        when(mockedRequest.isRequiredToMerge()).thenReturn(isRequiredToMerge)
        mockedRequest.createPR()
        then:
        thrown(exception)
        where:
        isRequiredToMerge << [false]
        exception         << [IllegalStateException]
    }

    def "trying to approve when there is no pr"() {
        when:
        when(mockedRequest.approve()).thenCallRealMethod()
        when(mockedRequest.findPullRequest()).thenThrow(IllegalStateException)
        mockedRequest.approve()
        then:
        thrown(IllegalStateException)
    }

    private def mockCommitStatus(String name, GHCommitState state) {
        def commitStatus = mock(GHCommitStatus)
        when(commitStatus.state).thenReturn(state)
        when(commitStatus.context).thenReturn(name)
        return commitStatus
    }



}
