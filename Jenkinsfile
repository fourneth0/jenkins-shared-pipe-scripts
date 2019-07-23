pipeline {

    agent any

    tools {gradle "gradle"}

    stages {
        stage('build') {
            steps { sh 'gradle build' }
        }
        stage('Run integrtion tests') {
            steps { sh 'gradle test -Dtest.profile=integration'}
        }
    }
}
