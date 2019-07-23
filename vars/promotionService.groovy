import groovy.transform.Field
import org.fourneth.PromotionRequest
import org.fourneth.ClientVersionApi

@Field PromotionRequest  request


def checking() {
    echo "Here......."
}

def setup(Map parameters = [:]) {

    def request = new PromotionRequest(
            parameters.org,
            parameters.repo,
            parameters.source,
            parameters.target,
            parameters.accessToken,
            parameters.approveToken
    )


}

def createPR(script, Map parameters = [:]) {
    echo 'ready to create pr'
    def request = new PromotionRequest(
            parameters.org,
            parameters.repo,
            parameters.source,
            parameters.target,
            parameters.accessToken,
            parameters.approveToken
    )

    echo 'creating PR'
    println request
    println request.requiredToMerge

    if (!request.requiredToMerge) {
        echo 'No changed to be promoted from source to target'
        currentBuild.result = 'UNSTABLE'
    } else {
        request.createPR()
    }

    this.request = request
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



