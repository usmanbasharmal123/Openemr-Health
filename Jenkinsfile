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
                bat 'mvn clean test -Dsurefire.suiteXmlFiles=testng.xml'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'reports/**', fingerprint: true
                }
            }
        }

        stage('Publish HTML Report') {
            steps {
                script {
                    publishHTML([
                        reportDir: 'reports',
                        reportFiles: '**/*.html',
                        reportName: 'OpenEMR_Automation_Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ])
                }
            }
        }
    }

    post {

        success {
            script {
                def reportUrl = "${env.BUILD_URL}OpenEMR_Automation_Report/"
                emailext(
                    subject: "SUCCESS: OpenEMR Automation - Build #${env.BUILD_NUMBER}",
                    to: env.EMAIL_RECIPIENTS,
                    body: """
OpenEMR Automation - SUCCESS

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME ?: 'N/A'}

Report:
${reportUrl}
""",
                    mimeType: 'text/plain'
                )
            }
        }

        failure {
            script {
                def reportUrl = "${env.BUILD_URL}OpenEMR_Automation_Report/"
                emailext(
                    subject: "FAILED: OpenEMR Automation - Build #${env.BUILD_NUMBER}",
                    to: env.EMAIL_RECIPIENTS,
                    body: """
OpenEMR Automation - FAILED

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME ?: 'N/A'}

Report:
${reportUrl}
""",
                    mimeType: 'text/plain'
                )
            }
        }

        unstable {
            script {
                def reportUrl = "${env.BUILD_URL}OpenEMR_Automation_Report/"
                emailext(
                    subject: "UNSTABLE: OpenEMR Automation - Build #${env.BUILD_NUMBER}",
                    to: env.EMAIL_RECIPIENTS,
                    body: """
OpenEMR Automation - UNSTABLE

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME ?: 'N/A'}

Report:
${reportUrl}
""",
                    mimeType: 'text/plain'
                )
            }
        }

        always {
            echo "Pipeline completed. Reports archived and email sent."
        }
    }
}
