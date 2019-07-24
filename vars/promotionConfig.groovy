import groovy.transform.Field
import org.fourneth.PromotionRequestConfig

@Field PromotionRequestConfig config

/**
 * This step will create PromotionRequestConfig object and hold onto it.
 * Rest of the steps can access the configuration as `promotionConfig.config`.
 * Required params are:
 *  accessTokenId  - a credentialId to access github account.  This credentialId should be configured with jenkins.
 *  approveTokenId - a credentialId to approve github account. This credentialId should be configured with jenkins.
 *  organization   - github organization name
 *  repository     - repository name
 *  source         - source branch from which the PR should initiate
 *  target         - target branch to which the PR should destine
 *
 * @param params
 * @return
 */
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
                config.validate()
            }
        }
    }

}
