package org.fourneth

import com.sun.xml.internal.xsom.impl.scd.Iterators
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHCommitState
import org.kohsuke.github.GHCommitStatus
import org.kohsuke.github.GHCompare
import org.kohsuke.github.GHOrganization
import org.kohsuke.github.GHPullRequest
import org.kohsuke.github.GHPullRequestReview
import org.kohsuke.github.GHPullRequestReviewEvent
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GHTagObject
import org.kohsuke.github.GitHub

class PromotionRequest {

    private String accessToken
    private String approveToken

    private GitHub client
    private GHOrganization organization
    private GHRepository repository
    private GHBranch source
    private GHBranch target
    private GHPullRequest pullRequest
    private GHPullRequestReview review

    PromotionRequest (String org, String repo, String source, String target, String accessToken, String approveToken) {
        println('creating the client')
        println(accessToken)
        this.client = GitHub.connectUsingOAuth(accessToken)
        // this.organization = this.client.getOrganization(org)
//        this.repository = this.organization.getRepository(repo)
//        this.source = this.repository.getBranch(source)
//        this.target = this.repository.getBranch(target)
//        this.accessToken = accessToken
//        this.approveToken = approveToken
        println('client created')
    }

    PromotionRequest reset() {
        this.source = this.repository.getBranch(this.source.name)
        this.target = this.repository.getBranch(this.target.name)
        this.pullRequest = null
        this.review = null
        return this
    }

    /**
     * Verify weather the there are any changes to be merged to target branch from source branch.
     * @return
     */
    boolean isRequiredToMerge() {
        def comparison = this.repository.getCompare(this.source.name, this.target.name)
        return (comparison.status != GHCompare.Status.identical
            && comparison.status != GHCompare.Status.ahead)
    }

    /**
     * Close any existing PRs raised from source to target branch.
     * @return
     */
    PromotionRequest closeExistingPRs() {
        def existingPRs = this.repository
                .queryPullRequests()
                .base(target.name)
                .head(source.name)
                .list()

        for (pr in existingPRs) {
            pr.close()
        }
        return this
    }


    /**
     * Create PR from source to target. Add given body content to the PR.
     * This will close any existing PRs and will create a new one.
     * @param body
     * @return
     */
    PromotionRequest createPR(String body = "") {
        if (!this.isRequiredToMerge()) {
            throw new IllegalStateException("${target.name} is upto date with ${source.name}")
        }

        closeExistingPRs()

        this.pullRequest = repository.createPullRequest(
                "Auto Promotion: ${source.name} -> ${target.name} | ${new Date()}",
                source.name,
                target.name,
                body
        )
        return this
    }

    /**
     * Approve the PR
     * @param approveToken
     * @param message
     * @return
     */
    PromotionRequest approve(String message = "Approve request for auto-promotion") {
        if (!this.pullRequest) {
            throw new IllegalStateException('Pull request should be created before approving')
        }
        GHPullRequest pr = GitHub.connectUsingOAuth(approveToken)
                .getOrganization(this.repository.ownerName)
                .getRepository(this.repository.name)
                .getPullRequest(this.pullRequest.number)

        this.review = pr.createReview()
            .body(message)
            .event(GHPullRequestReviewEvent.APPROVE)
            .create()
        pullRequest.refresh()
        return this
    }

    PromotionRequest merge() {
        assert this.pullRequest
        this.pullRequest.merge("Promote branch ${source.name} to ${target.name}", this.source.getSHA1())
        pullRequest.refresh()
        refreshTarget()
        return this
    }

    /**
     * Check whether all reported state against the PR head is completed.
     * This will return `false` even if there isn't any build status against the PR.
     * @return
     */
    boolean hasAllStatusPassed() {
        def commitStatuses = listBuildStatuses()
        if (commitStatuses.isEmpty()) {
            return false
        }
        def latestStatuses = getLatestCommitStatus(commitStatuses)
        return latestStatuses.every { GHCommitStatus item ->
            item.state == GHCommitState.SUCCESS
        }
    }

    Iterable<GHCommitStatus> listBuildStatuses() {
        return repository.listCommitStatuses(this.source.getSHA1())
    }

    private Iterable<GHCommitStatus> getLatestCommitStatus(Iterable<GHCommitStatus> commitStatuses) {
        Set uniqueStatuses = []
        return commitStatuses.findAll { item ->
            if (uniqueStatuses.contains(item.context)) {
                return false
            } else {
                uniqueStatuses.add(item.context)
                return true
            }
        }
    }

    private def refreshTarget() {
        target = repository.getBranch(this.target.name)
    }

    GHTagObject createTag(String name, String sha) {
        this.repository.createRef(name, sha)
        return this.repository.getTagObject(sha)
    }

    String getMergeCommitSha() {
        this.pullRequest.mergeCommitSha
    }

}
