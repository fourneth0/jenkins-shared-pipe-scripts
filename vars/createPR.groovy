import org.fourneth.*

void call(script) {
    echo "Starting to create PR ${script.promotionConfig.config}"
    def request = new PromotionRequest(script.promotionConfig.config)
    echo "Going to create"
    request.createPR()
    echo "PR Created"
}