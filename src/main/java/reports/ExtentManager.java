package reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	private static ExtentReports extent;

	public static ExtentReports getInstance() {
		if (extent == null) {
			createInstance();
		}
		return extent;
	}

	private static ExtentReports createInstance() {

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String reportPath = "reports/ExtentReport_" + timestamp + ".html";

		ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

		// Theme + Branding
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle("OpenEMR Automation Report");

		spark.config().setReportName(
				"<img src='https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg' height='40'/> "
						+ " OpenEMR Test Execution Summary");

		// Custom CSS
		spark.config().setCss(".badge-primary { background-color: #4CAF50 !important; }"
				+ ".badge-danger { background-color: #F44336 !important; }"
				+ ".badge-warning { background-color: #FFC107 !important; }"
				+ ".nav-wrapper { background-color: #1E1E1E !important; }"
				+ ".brand-logo { font-size: 22px !important; font-weight: bold !important; }"
				+ ".test-name { font-size: 18px !important; font-weight: 600 !important; }"
				+ ".card-panel { border-radius: 10px !important; }" + ".step-details { font-size: 14px !important; }"
				+ ".screenshot img { border: 2px solid #444 !important; border-radius: 6px !important; }"
				+ "body { font-family: 'Segoe UI', sans-serif !important; }");

		extent = new ExtentReports();
		extent.attachReporter(spark);

		// System info
		extent.setSystemInfo("Framework", "Selenium + TestNG");
		extent.setSystemInfo("Author", "Basharmal Safi");
		extent.setSystemInfo("Environment", "Local Machine");

		return extent;
	}
}
