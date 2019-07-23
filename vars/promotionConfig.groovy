import groovy.transform.Field
import org.fourneth.PromotionRequestConfig

@Field PromotionRequestConfig config

def init(Map params = [:]) {
    withCredentials([string(credentialsId: params.accessTokenId, variable: 'accessToken')]) {
        withCredentials([string(credentialsId: params.approveTokenId, variable: 'approveToken')]) {
            script {
                config = new PromotionRequestConfig(
                        accessToken:  accessToken,
                        approveToken: approveToken,
                        organization: params.organization,
                        repository:   params.repository,
                        source:       params.source,
                        target:       params.target
                )
            }
        }
    }


}
