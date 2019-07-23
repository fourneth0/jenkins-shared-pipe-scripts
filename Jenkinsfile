pipeline {

    agent any

    environment {
        ACCESS_TOKEN = credentials('git-access-token')
        APPROVE_TOKEN = credentials('git-pr-review-token')
    }

    tools {gradle "gradle"}

    stages {
        stage('build') {
            steps { sh 'gradle build' }
        }
        stage('Run integration tests') {
            steps {
                sh 'gradle test -Dtest.profile=integration -i'
            }
        }
    }
}
