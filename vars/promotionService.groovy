
import org.fourneth.PromotionRequest
import org.fourneth.ClientVersionApi

PromotionRequest request = null
ClientVersionApi clientVersionApi = null


def setup(Map parameters = [:]) {
    request = new PromotionRequest(
            parameters.org,
            parameters.repo,
            parameters.source,
            parameters.target,
            parameters.accessToken,
            parameters.approveToken

    )
    clientVersionApi = new ClientVersionAPI(url: parameters.versionURL, propertyName: parameters.versionPropertyName)

}

def createPR() {
    assert request

    if (!request.isRequiredToMerge()) {
        echo 'No changed to be promoted from source to target'
        currentBuild.result = 'UNSTABLE'
    } else {
        request.createPR()
    }
}

def approveAndMerge(Map parameters = [:]) {
    assert request
    request.approve()

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil request.hasAllStatusPassed()
    }

    request.merge()
}

def waitTillAPIDeployed(Map parameters = [:]) {
    assert clientVersionApi
    assert request

    def timeoutInMin = parameters.timeout ? parameters.timeout : 10
    timeout(time: timeoutInMin, unit: 'MINUTES') {
        waitUntil request.target.version.contains(clientVersionApi.version)
    }
}



