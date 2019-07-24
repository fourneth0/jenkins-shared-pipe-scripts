package org.fourneth

import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHCommitState
import org.kohsuke.github.GHCommitStatus
import org.kohsuke.github.GHCompare
import org.kohsuke.github.GHOrganization
import org.kohsuke.github.GHPullRequest
import org.kohsuke.github.GHPullRequestReviewEvent
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub

/**
 * This is a wrapper around github client to extract the necessary functionalities
 */
class PromotionRequest {

    PromotionRequestConfig config

    private GitHub client
    private GHOrganization organization
    private GHRepository repository
    private GHBranch source
    private GHBranch target
    private GHPullRequest pullRequest

    PromotionRequest (PromotionRequestConfig config) {
        this.config = config
        this.client = GitHub.connectUsingOAuth(config.accessToken)

        this.organization = this.client.getOrganization(config.organization)
        this.repository = this.organization.getRepository(config.repository)
        this.source = this.repository.getBranch(config.source)
        this.target = this.repository.getBranch(config.target)
    }

    /**
     * Verify weather the there are any changes to be merged to target branch from source branch.
     * @return
     */
    boolean isRequiredToMerge() {
        def comparison = compareBranches()
        return (comparison.status != GHCompare.Status.identical
            && comparison.status != GHCompare.Status.ahead)
    }



    /**
     * Create PR from source to target. Add given body content to the PR.
     * This will close any existing PRs and will create a new one.
     * @param body
     * @return
     */
    PromotionRequest createPR() {
        String body = "Auto promotion: Creating PR"

        if (!this.isRequiredToMerge()) {
            throw new IllegalStateException("{target.name} is upto date with {source.name}")
        }

        closeExistingPR()

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
    PromotionRequest approve() {
        String message = "Auto approving pull request"
        this.pullRequest = findPullRequest()
        GHPullRequest pr = GitHub.connectUsingOAuth(this.config.approveToken)
                .getOrganization(this.repository.ownerName)
                .getRepository(this.repository.name)
                .getPullRequest(this.pullRequest.number)

        if (!pr) {
            throw new IllegalStateException("cannot find the PR to approve")
        }

        pr.createReview()
            .body(message)
            .event(GHPullRequestReviewEvent.APPROVE)
            .create()
        pullRequest.refresh()
        return this
    }


    /**
     * Merge the PR raised from source --> target.
     * @return
     * @throws IllegalStateException - if there is no PR
     */
    PromotionRequest merge() {
        this.pullRequest = findPullRequest()
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
        if (commitStatuses.size() == 0) {
            return false
        }
        def latestStatuses = getLatestCommitStatus(commitStatuses)
        return latestStatuses.every { GHCommitStatus item ->
            item.state == GHCommitState.SUCCESS
        }
    }

    String getSourceVersion() {
        return this.source.getSHA1()
    }

    Iterable<GHCommitStatus> listBuildStatuses() {
        return repository.listCommitStatuses(this.source.getSHA1())
    }

    GHPullRequest findPullRequest() {
        def prs = repository.queryPullRequests()
                .base(target.name)
                .head(source.name)
                .list()
        if (prs.size() == 0) {
            throw new IllegalStateException("Couldn't find a pr from ${source.name} to ${target.name} ")
        }
        return prs.first()
    }

    /**
     * Close any existing PR raised from source to target branch.
     * @return true if closed
     */
    boolean closeExistingPR() {
        try {
            def existingPR = findPullRequest()
            existingPR.close()
            return true
        } catch (IllegalStateException e) {
            return false
        }

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

    GHCompare compareBranches() {
        return this.repository.getCompare(this.source.name, this.target.name)
    }
    private def refreshTarget() {
        target = repository.getBranch(this.target.name)
    }

}
