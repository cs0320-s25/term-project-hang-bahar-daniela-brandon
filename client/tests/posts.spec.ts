import { test, expect } from "@playwright/test";
import { clerk } from "@clerk/testing/playwright";

test.describe("Home Page Integration Tests", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
    // Sign in using Clerk (adjust strategy and env vars as needed)
    await clerk.signIn({
      page,
      signInParams: {
        strategy: "password",
        password: process.env.E2E_CLERK_USER_PASSWORD!,
        identifier: process.env.E2E_CLERK_USER_USERNAME!,
      },
    });
  });

  test("There should be reviews present", async ({ page }) => {
		await page.goto("http://localhost:5173/reviews");
    await expect(page.getByTestId("dormName").first()).toBeVisible();
  });

  test("make a post and verify that it exists", async ({ page }) => {
    await page.goto("http://localhost:5173/make-post");
    await page.locator("[name='title']").fill("Playwright Test Title");
    await page.locator("[name='location']").selectOption("Barbour Hall");
    await page.locator("[name='content']").fill("Playwright Test Content");
    
    await page.getByTestId("submitButton").click();
    await expect(page.getByText("Playwright Test Content")).toBeVisible();
    await expect(page.getByText("Playwright Test Title")).toBeVisible();
  });

  test("missing add post params", async ({ page }) => {
    await page.goto("http://localhost:5173/make-post");

    await page.getByTestId("submitButton").click();
    page.on("dialog", async (alert) => {
      const text = alert.message();
      expect(text).toContain("Please enter a title");
      await alert.accept();
    });
  });
});
