package org.fourneth

import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHCommitState
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

    PromotionRequest (org, repo, source, target, accessToken, approveToken) {
        this.client = GitHub.connectUsingOAuth(accessToken)
        this.organization = this.client.getOrganization(org)
        this.repository = this.organization.getRepository(repo)
        this.source = this.repository.getBranch(source)
        this.target = this.repository.getBranch(target)
        this.accessToken = accessToken
        this.approveToken = approveToken
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
        def comparison = repository.getCompare(source.name, target.name)
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
            throw new IllegalStateException("${target.name} is upto date with ${source.name}");
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
    PromotionRequest approve(String approveToken, String message = "Approve request for auto-promotion") {
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
        if (!this.pullRequest) {
            throw new IllegalStateException('A PR should be raised before merging')
        }
        if (!isMergeable()) {
            throw new IllegalStateException('PR is not mergeable');
        }
        this.pullRequest.merge("Promote branch ${source.name} to ${target.name}", this.source.getSHA1())
        pullRequest.refresh()
        target = repository.getBranch(this.target.name)
        return this
    }

    boolean hasAllStatusPassed() {
        def commitStatuses = repository.listCommitStatuses(this.source.getSHA1())
        if (commitStatuses.isEmpty()) {
            return false
        }
        Set uniqueStatuses = []
        def allPassed = true
        for (s in commitStatuses) {
            if (uniqueStatuses.contains(s.context)) {
                continue
            }
            uniqueStatuses.add(s.context)
            allPassed = allPassed && s.state == GHCommitState.SUCCESS
        }
        return allPassed
    }

    GHTagObject createTag(String name, String sha) {
        this.repository.createRef(name, sha)
        return this.repository.getTagObject(sha)
    }

    String getMergeCommitSha() {
        this.pullRequest.mergeCommitSha
    }

    boolean isMergeable() {
        pullRequest.refresh()
        return pullRequest.mergeable
    }


    // Read only properties
    GitHub getClient() {
        return this.client
    }

    GHOrganization getOrganization() {
        return organization
    }

    GHRepository getRepository() {
        return this.repository
    }

    GHBranch getSource() {
        return source
    }

    GHBranch getTarget() {
        return target
    }

    GHPullRequest getPullRequest() {
        return pullRequest
    }

}