package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import base.BaseTest;
import reports.ExtentTestManager;

public class ReportTestDemo extends BaseTest {

	@Test(groups = { "Smoke" }, description = "Author: Usman | Simple passing test")
	public void testPass() {
		ExtentTestManager.logStep("Step 1: Starting PASS test");
		ExtentTestManager.logStep("Step 2: Validating true == true");
		Assert.assertTrue(true);
	}

	@Test(groups = { "Regression" }, description = "Author: Usman | Simple failing test")
	public void testFail() {
		ExtentTestManager.logStep("Step 1: Starting FAIL test");
		ExtentTestManager.logStep("Step 2: Forcing failure");
		Assert.fail("Intentional failure for testing report");
	}

	@Test(groups = { "Sanity" }, description = "Author: Usman | Simple skipped test")
	public void testSkip() {
		ExtentTestManager.logStep("Step 1: Starting SKIP test");
		throw new SkipException("Skipping intentionally");
	}
}
