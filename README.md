# jenkins-shared-pipe-scripts

# Introduction 
This is a module to share groovy scripts with Jenkins pipelines. 

This module follows the structure as described in https://jenkins.io/doc/book/pipeline/shared-libraries/

# How to Use
Follow the instruction in https://jenkins.io/doc/book/pipeline/shared-libraries/ to setup this module in your jenkins server

Refer examples in `example` folder on how to use different utilities in this library. 

#Running Tests
1. `gradle test` -> runs unit tests
2. `gradle test -Dtest.profile=integration` will run unit + integration tests. 
    Expose `ACCESS_TOKEN` and `APPROVE_TOKEN` environment variables to run integration tests in local environment.
    Refer Jenkinsfile for more details 
