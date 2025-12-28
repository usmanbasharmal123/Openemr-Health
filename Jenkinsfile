pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        MAVEN_TOOL = 'Maven-3'
        JDK_TOOL   = 'JDK-21'
        EMAIL_RECIPIENTS = 'your.email@gmail.com'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set up tools') {
            steps {
                script {
                    def jdkHome   = tool name: env.JDK_TOOL, type: 'hudson.model.JDK'
                    def mavenHome = tool name: env.MAVEN_TOOL, type: 'hudson.tasks.Maven$MavenInstallation'
                    env.PATH = "${jdkHome}/bin:${mavenHome}/bin:${env.PATH}"
                }
            }
        }

        stage('Build & Test') {
            steps {
                // Ignore test failures so pipeline continues
                bat 'mvn clean test -Dsurefire.suiteXmlFiles=testng.xml -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'reports/**', fingerprint: true
                    archiveArtifacts artifacts: 'screenshots/**', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'logs/**', allowEmptyArchive: true
                }
            }
        }

        stage('Publish HTML Report') {
            steps {
                script {
                    publishHTML([
                        reportDir: 'reports',
                        reportFiles: '**/*.html',
                        reportName: 'OpenEMR Automation Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ])
                }
            }
        }
    }

    post {

        // Convert test failures into UNSTABLE instead of FAILURE
        unsuccessful {
            script {
                currentBuild.result = 'UNSTABLE'
            }
        }

        always {
            script {
                def reportUrl = "${env.BUILD_URL}OpenEMR_Automation_Report/"
                def status = currentBuild.currentResult

                emailext(
                    to: env.EMAIL_RECIPIENTS,
                    subject: "OpenEMR Automation - Build #${env.BUILD_NUMBER} - ${status}",
                    body: """
Hello Team,

The OpenEMR Automation pipeline has completed.

Status: ${status}
Job: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME ?: 'N/A'}

HTML Report:
${reportUrl}

Regards,
Jenkins CI
""",
                    mimeType: 'text/plain'
                )
            }

            echo "Pipeline completed. Email sent regardless of build result."
        }
    }
}
