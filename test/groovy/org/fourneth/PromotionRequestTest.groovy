package org.fourneth

import org.fourneth.PromoteTestUtil
import org.kohsuke.github.GHIssueState
import spock.lang.Specification

class PromotionRequestTest extends Specification {

    def env = System.getenv()

    def promotionRequest = new PromotionRequest(
            "fourneth0",
            "tryjenpipe",
            "develop",
            "staging",
            env['ACCESS_TOKEN'],
            "rt")
    def util = new PromoteTestUtil(promotionRequest: promotionRequest)

    def "isRequiredToMerge, when there are changes"() {
        when:
            util.createASampleCommit('first commit')
            promotionRequest.reset()
        then:
            promotionRequest.isRequiredToMerge()
    }

    def "create promotion pr"() {
        setup:
            util.closeExistingPRs()
            util.createDuplicatePR()
        when:
            promotionRequest.createPR("Promotion PR")
        then:
            promotionRequest.pullRequest != null
            promotionRequest.pullRequest.base.sha == promotionRequest.target.getSHA1()
            promotionRequest.pullRequest.head.sha == promotionRequest.source.getSHA1()
    }

    def "approve"() {
        when:
            promotionRequest.createPR("Verify approvability")
            promotionRequest.approve(env['APPROVE_TOKEN'])
        then:
            promotionRequest.pullRequest.listReviews().size() == 1
            promotionRequest.pullRequest.state == GHIssueState.OPEN
            promotionRequest.mergeable
    }

    def "merge pr"() {
        setup:
            util.closeExistingPRs()
            util.createDuplicatePR()
            promotionRequest.createPR("verify merge")
            promotionRequest.approve("057af90bbe5ed0a59bf639c27a2e1df89cc3d040")
            util.waitTillMergeable()
        when:
            promotionRequest.merge()
        then:
            promotionRequest.target.getSHA1() == promotionRequest.pullRequest.mergeCommitSha
            promotionRequest.pullRequest.state == GHIssueState.CLOSED
    }

}
