import org.fourneth.*

void call(script, Map parameters = [:]) {
    echo "${script.promotionConfig.config}"
    echo "${parameters.config}"
    PromotionRequestConfig config = parameters.config
    new PromotionRequest(config).createPR()
}