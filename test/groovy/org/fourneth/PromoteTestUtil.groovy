package groovy.org.fourneth

import org.fourneth.PromotionRequest
import org.kohsuke.github.GHCommit
import org.kohsuke.github.GHPullRequest

class PromoteTestUtil {

    PromotionRequest promotionRequest


    GHCommit createASampleCommit(message = "") {
        def baseSha = promotionRequest.source.getSHA1()
        def commit = this.createCommitRequest(baseSha, message)
        updateBranchHead(commit.getSHA1())
        return commit
    }

    GHPullRequest createDuplicatePR() {
        return createAPR();
    }

    PromotionRequest closeExistingPRs() {
        return promotionRequest.closeExistingPRs()
    }
    GHPullRequest createAPR() {
        String source = promotionRequest.source.name
        String target = promotionRequest.target.name
        return promotionRequest.repository.createPullRequest(
                "Promotion Test PR: ${source} -> ${target} | ${new Date()}",
                source,
                target,
                "Test Pr For Testing"
        )
    }

    PromotionRequest waitTillMergeable() {
        while(!promotionRequest.hasAllStatusPassed()) {
            println("wati till mergeable ${new Date()}")
            Thread.sleep(30000)
        }
    }

    private GHCommit createCommitRequest(baseSha, message = "") {
         def blob = promotionRequest.repository.createBlob()
                .textContent("This is a sample text to write from text ${new Date()}")
                .create()

        def tree = promotionRequest.repository.createTree()
                .baseTree(baseSha)
                .shaEntry('README1.md', blob.sha, false)
                .create()

        def commit = promotionRequest.repository.createCommit()
                .committer("jenkins-lit-itest", "itest@itest.com", new Date())
                .message("A sample commit ${message} ${new Date()}")
                .parent(baseSha)
                .tree(tree.sha)
                .create()
        return commit
    }

    private def updateBranchHead(commitSha) {
        def ref = promotionRequest.repository.getRef("heads/${promotionRequest.source.name}")
        ref.updateTo(commitSha)
    }
}
