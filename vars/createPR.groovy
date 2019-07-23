import org.fourneth.*

void call(script) {
    echo "Starting to create PR"
    def request = new PromotionRequest(script.promotionConfig.config)
    request.createPR()
    echo "PR Created"
}