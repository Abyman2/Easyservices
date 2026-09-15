pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
        nodejs 'Node20'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                }
            }
        }

        stage('Start Frontend') {
            steps {
                dir('frontend') {
                    sh 'nohup npm run dev -- --host 0.0.0.0 > ../frontend-vite.log 2>&1 &'
                    sh 'for i in $(seq 1 30); do curl -fsS http://127.0.0.1:5173/ >/dev/null && exit 0; sleep 1; done; cat ../frontend-vite.log; exit 1'
                }
            }
        }

        stage('Build & Test') {
            steps {
                dir('backend') {
                    sh 'mvn clean test'
                }
            }
        }

        stage('Code Coverage Check') {
            steps {
                dir('backend') {
                    sh 'mvn jacoco:check'
                }
            }
        }

        stage('Publish Reports') {
            steps {
                dir('backend') {
                    jacoco execPattern: 'target/jacoco.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java'
                }
                junit testResults: 'backend/target/surefire-reports/*.xml', allowEmptyResults: false
                archiveArtifacts artifacts: 'backend/target/site/jacoco/**,backend/target/surefire-reports/**,frontend-vite.log', allowEmptyArchive: false
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
