import org.fourneth.*

/**
 * Approve and merge a PR that exists from given source branch to target branch.
 * This step expect an PR to already exists.
 * Also, this steps assume that there are build status enforcement for PRs
 * This step will wait for all build status to be success before merging the PR
 *
**/
void call(script, Map parameters = [:]) {
    new PromotionRequest(script.promotionConfig.config).approve()

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil {
            hasAllStatusPassed(script.promotionConfig.config)
        }
    }
    new PromotionRequest(script.promotionConfig.config).merge()

}

def hasAllStatusPassed(config) {
    sleep(time: 30, unit: 'SECONDS') // wait a bit before querying API
    return new PromotionRequest(config).hasAllStatusPassed()
}
