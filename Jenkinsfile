// -------------------------
// SAFE Helper: Extract Test Summary (string parsing only)
// -------------------------
def getTestSummary() {
    def summary = [passed: 0, failed: 0, skipped: 0]

    if (!fileExists("target/surefire-reports/testng-results.xml")) {
        return summary
    }

    def content = readFile("target/surefire-reports/testng-results.xml")

    // Extract numbers using regex (sandbox-safe)
    def tests    = (content =~ /total="(\d+)"/)[0][1].toInteger()
    def failures = (content =~ /failed="(\d+)"/)[0][1].toInteger()
    def skipped  = (content =~ /skipped="(\d+)"/)[0][1].toInteger()

    summary.failed  = failures
    summary.skipped = skipped
    summary.passed  = tests - failures - skipped

    return summary
}

// -------------------------
// SAFE Helper: Screenshot Gallery (Windows dir + readFile)
// -------------------------
def buildScreenshotGallery() {
    bat(script: 'dir /b screenshots\\*.png > screenshot_list.txt', returnStatus: true)

    if (!fileExists('screenshot_list.txt')) {
        return "<p>No screenshots found.</p>"
    }

    def list = readFile('screenshot_list.txt').split("\r?\n")
    if (!list || list.size() == 0) {
        return "<p>No screenshots found.</p>"
    }

    def html = "<table><tr>"

    list.each { fileName ->
        if (fileName.trim()) {
            def fileUrl = "${env.BUILD_URL}artifact/screenshots/${fileName}"
            html += """
                <td style='padding:10px; text-align:center;'>
                    <a href='${fileUrl}' target='_blank'>
                        <img src='${fileUrl}' width='200' style='border:1px solid #ccc;'/>
                    </a>
                    <br/>
                    <small>${fileName}</small>
                </td>
            """
        }
    }

    html += "</tr></table>"
    return html
}

// -------------------------
// SAFE Helper: Test Result Chart
// -------------------------
def generateTestChart(summary) {
    return """
    <div style="width: 400px; margin-top:20px;">
        <img src="https://quickchart.io/chart?c={
            type:'bar',
            data:{
                labels:['Passed','Failed','Skipped'],
                datasets:[{
                    label:'Test Results',
                    data:[${summary.passed},${summary.failed},${summary.skipped}],
                    backgroundColor:['#2ECC71','#E74C3C','#F1C40F']
                }]
            },
            options:{
                plugins:{legend:{display:false}},
                scales:{y:{beginAtZero:true}}
            }
        }" style="width:400px;"/>
    </div>
    """
}

// -------------------------
// Main Pipeline
// -------------------------
pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        MAVEN_TOOL = 'Maven-3'
        JDK_TOOL   = 'JDK-21'
        EMAIL_RECIPIENTS = 'usman.basharmal123@gmail.com'
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

        unsuccessful {
            script {
                currentBuild.result = 'UNSTABLE'
            }
        }

        always {
            script {

                // SIMPLE PLAIN TEXT EMAIL TEST
                mail(
                    to: env.EMAIL_RECIPIENTS,
                    from: 'usman.basharmal123@gmail.com',
                    subject: "Jenkins Plain Text Test Email - Build #${env.BUILD_NUMBER}",
                    mimeType: 'text/plain',
                    body: """Hello Usman,

This is a plain text test email sent from your Jenkins pipeline.

If you receive this, Gmail is accepting pipeline emails.

Regards,
Jenkins CI
"""
                )
            }

            echo "Pipeline completed. Plain text email sent."
        }
    }
}
