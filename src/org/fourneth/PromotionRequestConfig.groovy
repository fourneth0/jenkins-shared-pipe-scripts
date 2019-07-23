package org.fourneth

class PromotionRequestConfig {

    String accessToken
    String approveToken
    String organization
    String repository
    String source
    String target

    def validate() {
        assertString accessToken
        assertString approveToken
        assertString organization
        assertString repository
        assertString source
        assertString target
    }

    private boolean assertString(param) {
        assert param
        assert !param.trim().isEmpty()
    }

}
