import org.fourneth.*

void call(script, Map parameters = [:]) {
    def request = new PromotionRequest(script.promotionConfig.config)

    request.approve()

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil request.hasAllStatusPassed()
    }

    request.merge()
}