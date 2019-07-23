import org.fourneth.*

def call(script, parameters = [:]) {
    def sourceVersion = new PromotionRequest(script.promotionConfig.config).sourceVersion

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil {
            isThisVersionDeployed(parameters, sourceVersion)
        }
    }
}

def isThisVersionDeployed(parameters, sourceVersion) {
    return new ClientVersionApi(url: parameters.versionURL).isThisVersionDeployed(sourceVersion)
}