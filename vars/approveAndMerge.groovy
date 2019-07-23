import org.fourneth.*

void call(script, Map parameters = [:]) {
    new PromotionRequest(script.promotionConfig.config).approve()

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil hasAllStatusPassed(script.promotionConfig.config)
    }
    new PromotionRequest(script.promotionConfig.config).merge()

}

def hasAllStatusPassed(config) {
    return new PromotionRequest(script.promotionConfig.config).hasAllStatusPassed()
}