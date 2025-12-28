// -------------------------
// Helper: Extract Test Summary
// -------------------------
def getTestSummary() {
    def summary = [passed: 0, failed: 0, skipped: 0]
    def reportsDir = new File("${pwd()}/target/surefire-reports")

    if (!reportsDir.exists()) return summary

    reportsDir.eachFileMatch(~/.*\.xml/) { file ->
        def xml = new XmlSlurper().parse(file)
        summary.passed  += xml.@tests.toInteger() - xml.@failures.toInteger() - xml.@errors.toInteger() - xml.@skipped.toInteger()
        summary.failed  += xml.@failures.toInteger() + xml.@errors.toInteger()
        summary.skipped += xml.@skipped.toInteger()
    }

    return summary
}

// -------------------------
// Helper: Screenshot Gallery
// -------------------------
def buildScreenshotGallery() {
    def screenshotDir = "screenshots"
    def workspace = pwd()
    def files = []

    try {
        files = new File("${workspace}/${screenshotDir}").listFiles()
    } catch (Exception e) {
        return "<p>No screenshots found.</p>"
    }

    if (!files || files.size() == 0) {
        return "<p>No screenshots found.</p>"
    }

    def html = "<table><tr>"

    files.each { file ->
        def fileName = file.getName()
        def fileUrl = "${env.BUILD_URL}artifact/${screenshotDir}/${fileName}"

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

    html += "</tr></table>"
    return html
}

// -------------------------
// Helper: Test Result Chart
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
                    subject: "OpenEMR Automation - Build #${env.BUILD_NUMBER} - ${status}",
                    mimeType: 'text/html',
                    body: """
<html>
<body style="font-family: Arial, sans-serif;">

<h2 style="color:#2E86C1;">OpenEMR Automation Test Report</h2>

<p>Hello Team,</p>
<p>The automated test execution has completed. Below is the summary:</p>

<h3 style="color:#117A65;">📊 Test Summary</h3>

<table border="1" cellpadding="6" cellspacing="0" style="border-collapse: collapse;">
<tr><th>Passed</th><td>${summary.passed}</td></tr>
<tr><th>Failed</th><td>${summary.failed}</td></tr>
<tr><th>Skipped</th><td>${summary.skipped}</td></tr>
</table>

<br/>

<h3 style="color:#884EA0;">📈 Test Result Chart</h3>
${chart}

<br/>

<h3 style="color:#117A65;">📄 Clickable HTML Report</h3>
<p>
    <a href="${reportUrl}" style="font-size:16px; color:#1F618D;">
        👉 OpenEMR Automation HTML Report
    </a>
</p>

<br/>

<h3 style="color:#B03A2E;">📸 Screenshots</h3>
${screenshots}

<br/><br/>

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
