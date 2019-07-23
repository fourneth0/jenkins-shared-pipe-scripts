package org.fourneth.integration

import spock.lang.Specification
import org.fourneth.PromotionRequest
import org.fourneth.PromotionRequestConfig
import org.fourneth.integration.util.PromoteTestUtil

//@ClassRule
//public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode("simulation.json");

class PromotionRequestIntegrationTest extends Specification {

    def env = System.getenv()

    def config = new PromotionRequestConfig(
            accessToken: env['ACCESS_TOKEN'],
            approveToken: env['APPROVE_TOKEN'],
            organization: "fourneth0",
            repository: "tryjenpipe",
            source: "develop",
            target: "staging",
    )

    def promotionRequest = new PromotionRequest(config)

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
