import { test, expect } from "@playwright/test";
import { clerk } from "@clerk/testing/playwright";

test.describe("Home Page Integration Tests", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
    // Sign in using Clerk (adjust strategy and env vars as needed)
    /*
    await clerk.signIn({
      page,
      signInParams: {
        strategy: "password",
        password: process.env.E2E_CLERK_USER_PASSWORD!,
        identifier: process.env.E2E_CLERK_USER_USERNAME!,
      },
    });
    */
  });

  test("When loaded, dorm cards should be visible", async ({ page }) => {
    await expect(page.getByTestId("dormName")).toHaveCount(31);
    await expect(page.getByTestId("dormImage")).toHaveCount(31);
    await expect(page.getByTestId("roomTypesList").first()).toContainText("Double"); 
  });

  test("Filter by dorm or dining hall", async ({ page }) => {
    await expect(page.getByTestId("dormName")).toHaveCount(31);
    await page.getByTestId("diningButton").click();
    await expect(page.getByTestId("diningName")).toHaveCount(6);
    await page.getByTestId("dormButton").click();
    await expect(page.getByTestId("dormImage")).toHaveCount(31);
    await expect(page.getByTestId("roomTypesList").first()).toContainText(
      "Double"
    );
  });

  test("Go to dorm profile", async ({ page }) => {
    await page.getByTestId("dormName").first().click()
    await expect(page.getByTestId("dormName")).toBeVisible();
  });

  test("Click on recommend button and verify page", async ({ page }) => {
    await page.getByTestId("findDormButton").click();
    await expect(page).toHaveURL("http://localhost:5173/recommend");
  });

  test("base recommendation", async ({ page }) => {
    await page.goto("http://localhost:5173/recommend");
    await page.getByTestId("submitButton").click();
    await expect(page.getByTestId("dormName").first()).toHaveText("Hegeman Hall");
  });

  test("specific recommendation", async ({ page }) => {
    await page.goto("http://localhost:5173/recommend");
    await page.getByTestId("submitButton").click();
    await page.getByTestId("yearSelect").selectOption("2026");
    await page.getByTestId("roomTypeSelect").selectOption("Double");
    await page.getByTestId("locationSelect").selectOption("Wriston Quad");
    await page.getByTestId("communitySelect").selectOption("Same-Sex Housing");
    await page.getByTestId("accessibilityCheckbox").check();
    await expect(page.getByTestId("dormName").first()).toHaveText(
      "Buxton House"
    );
  });
});
