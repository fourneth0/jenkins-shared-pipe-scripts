import org.fourneth.*

def call(script, parameters = [:]) {
    def sourceVersion = new PromotionRequest(script.promotionConfig.config).sourceVersion

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil {
            isThisVersionDeployed(script.promotionConfig.config)
        }
    }
}

def isThisVersionDeployed(config, sourceVersion) {
    return new ClientVersionApi(url: config.versionURL).isThisVersionDeployed(sourceVersion)
}