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
                def reportUrl = "${env.BUILD_URL}OpenEMR_Automation_Report/"
                def status = currentBuild.currentResult
                def summary = getTestSummary()
                def chart   = generateTestChart(summary)
                def screenshots = buildScreenshotGallery()

                emailext(
                    to: env.EMAIL_RECIPIENTS,
                    from: 'usman.basharmal123@gmail.com',
                    subject: "OpenEMR Automation - Build #${env.BUILD_NUMBER} - ${status}",
                    mimeType: 'text/plain',
                    body: 'This is a plain test email.',
<html>
<body style="font-family: Arial, sans-serif;">

<h2 style="color:#2E86C1;">OpenEMR Automation Test Report</h2>

<h3>📊 Test Summary</h3>
<table border="1" cellpadding="6" cellspacing="0">
<tr><th>Passed</th><td>${summary.passed}</td></tr>
<tr><th>Failed</th><td>${summary.failed}</td></tr>
<tr><th>Skipped</th><td>${summary.skipped}</td></tr>
</table>

<h3>📈 Test Result Chart</h3>
${chart}

<h3>📄 HTML Report</h3>
<a href="${reportUrl}">OpenEMR Automation HTML Report</a>

<h3>📸 Screenshots</h3>
${screenshots}

<p>Regards,<br/>Jenkins CI</p>

</body>
</html>
"""
                )
            }

            echo "Pipeline completed. Email sent with summary, chart, and screenshots."
        }
    }
}
