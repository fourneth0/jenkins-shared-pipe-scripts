import groovy.transform.Field
import org.fourneth.PromotionRequestConfig

@Field PromotionRequestConfig config

def init(Map params = [:]) {
    config = new PromotionRequestConfig(
            accessToken:  params.accessToken,
            approveToken: params.approveToken,
            organization: params.organization,
            repository:   params.repository,
            source:       params.source,
            target:       params.target
    )
}
