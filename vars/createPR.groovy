import org.fourneth.*

void call(Map parameters = [:]) {
    PromotionRequestConfig config = parameters.config
    def request = new PromotionRequest(config)
    request.createPR()
}