package org.fourneth

import org.fourneth.util.PromoteTestUtil
import org.kohsuke.github.GHIssueState
import spock.lang.Specification

class PromotionRequestIntegrationTest extends Specification {

    def env = System.getenv()

    def promotionRequest = new PromotionRequest(
            "fourneth0",
            "tryjenpipe",
            "develop",
            "staging",
            env['ACCESS_TOKEN'],
    env['APPROVE_TOKEN'])

    def util = new PromoteTestUtil(promotionRequest: promotionRequest)

    def "isRequiredToMerge, when there are changes"() {
        when:
            util.createASampleCommit('first commit')
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
    }

    def "merge pr"() {
        setup:
            util.closeExistingPRs()
            util.createDuplicatePR()
            promotionRequest.createPR("verify merge")
            promotionRequest.approve()
            util.waitTillMergeable()
        when:
            promotionRequest.merge()
        then:
            promotionRequest.target.getSHA1() == promotionRequest.pullRequest.mergeCommitSha
            promotionRequest.pullRequest.state == GHIssueState.CLOSED
    }

}
