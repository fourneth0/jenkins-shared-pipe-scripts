import org.fourneth.*

void call(script, Map parameters = [:]) {
    new PromotionRequest(script.promotionConfig.config).createPR()
}