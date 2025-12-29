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
// SAFE Helper: Test Result Chart (not used in email now, but kept)
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

                // -------------------------
                // Build dynamic data
                // -------------------------
                def summary = getTestSummary()
                def reportUrl = "${env.BUILD_URL}OpenEMR_Automation_Report/"
                def buildDuration = currentBuild.durationString.replace('and counting', '')
                def screenshotsHtml = buildScreenshotGallery()

                // -------------------------
                // Extract failed test names + stack traces
                // -------------------------
                def failureRows = ""
                if (summary.failed > 0) {
                    def xml = readFile("target/surefire-reports/testng-results.xml")

                    // Groovy-safe DOTALL regex using (?s)
                    def failedTests = (xml =~ /(?s)<test-method status="FAIL" name="([^"]+)".*?<full-stacktrace>(.*?)<\/full-stacktrace>/)

                    failedTests.each { match ->
                        def testName = match[1]
                        def stack = match[2]
                                .replaceAll("<!\

\[CDATA\

\[", "")
                                .replaceAll("]]>", "")
                                .replaceAll("\n", "<br/>")

                        failureRows += """
                            <tr>
                                <td style='padding:8px; border:1px solid #444;'>${testName}</td>
                                <td style='padding:8px; border:1px solid #444; font-family: monospace; color:#ff6b6b;'>${stack}</td>
                            </tr>
                        """
                    }
                } else {
                    failureRows = """
                        <tr>
                            <td colspan='2' style='padding:8px; border:1px solid #444;'>No failed tests</td>
                        </tr>
                    """
                }

                // -------------------------
                // Collapsible failure list
                // -------------------------
                def failureList = ""
                if (summary.failed > 0) {
                    def xml = readFile("target/surefire-reports/testng-results.xml")
                    def failedTests = (xml =~ /<test-method status="FAIL" name="([^"]+)"/)
                    failedTests.each { match ->
                        failureList += "<li>${match[1]}</li>"
                    }
                } else {
                    failureList = "<li>No failed tests</li>"
                }

                // -------------------------
                // Color-coded badge
                // -------------------------
                def status = currentBuild.currentResult
                def badgeColor = (status == "SUCCESS") ? "#2ECC71" :
                                 (status == "UNSTABLE") ? "#F1C40F" : "#E74C3C"

                def badgeHtml = """
                    <span style="background:${badgeColor}; color:white; padding:6px 12px; 
                                 border-radius:6px; font-weight:bold;">
                        ${status}
                    </span>
                """

                // -------------------------
                // PIE CHART (QuickChart)
                // -------------------------
                def chartUrl = "https://quickchart.io/chart?c={type:'pie',data:{labels:['Passed','Failed','Skipped'],datasets:[{data:[${summary.passed},${summary.failed},${summary.skipped}],backgroundColor:['#2ECC71','#E74C3C','#F1C40F']}]} }"

                // -------------------------
                // SEND EMAIL
                // -------------------------
                mail(
                    to: 'usman.basharmal123@gmail.com',
                    subject: "OpenEMR Automation Report - Build #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: """
<html>
  <body style="font-family: Arial, sans-serif; color:#ddd; background:#1e1e1e; padding:20px;">

    <h2 style="color:#4aa3ff;">OpenEMR Automation Test Report</h2>

    <h3>Status</h3>
    ${badgeHtml}

    <h3>⏱ Build Duration</h3>
    <p>${buildDuration}</p>

    <h3>📊 Test Summary</h3>
    <table border="1" cellpadding="6" cellspacing="0" style="border-collapse: collapse; color:#ddd; border-color:#444;">
      <tr><th>Passed</th><td>${summary.passed}</td></tr>
      <tr><th>Failed</th><td>${summary.failed}</td></tr>
      <tr><th>Skipped</th><td>${summary.skipped}</td></tr>
    </table>

    <h3>🥧 Test Result Pie Chart</h3>
    <img src="${chartUrl}" width="350"/>

    <h3>❗ Failed Tests (Collapsible)</h3>
    <details style="margin-top:10px;">
      <summary style="cursor:pointer; font-size:16px; color:#ff6b6b;">
        Click to expand failure list
      </summary>
      <ul>
        ${failureList}
      </ul>
    </details>

    <h3>🧨 Failed Test Stack Traces</h3>
    <table cellpadding="6" cellspacing="0" style="border-collapse: collapse; width:100%; color:#ddd; border:1px solid #444;">
        <tr style="background:#333;">
            <th style="padding:8px; border:1px solid #444;">Test Name</th>
            <th style="padding:8px; border:1px solid #444;">Stack Trace</th>
        </tr>
        ${failureRows}
    </table>

    <h3>📄 HTML Report</h3>
    <p>
      <a href="${reportUrl}" style="color:#4aa3ff;">Click here to view the full HTML report</a>
    </p>

    <h3>📜 Logs</h3>
    <p>
      <a href="${env.BUILD_URL}console" style="color:#4aa3ff;">View Jenkins Console Log</a>
    </p>

    <h3>📸 Screenshot Thumbnails</h3>
    ${screenshotsHtml}

    <br/>
    <p>Regards,<br/>Jenkins CI</p>

  </body>
</html>
"""
                )
            }

            echo "Pipeline completed. Enterprise HTML email sent."
        }
    }
}
