import org.fourneth.*

void call(Map parameters = [:]) {
    PromotionRequestConfig config = parameters.config
    def request = new PromotionRequest(config)

    request.approve()

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil request.hasAllStatusPassed()
    }

    request.merge()
}