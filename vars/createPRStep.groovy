import org.fourneth.*

/**
 * Create a pr from given source branch to target branch.
 *
 * This steps expects a promotionConfig param with config.
 * @param script
 */
void call(script) {
    def request = new PromotionRequest(script.promotionConfig.config)
    request.createPR()
}