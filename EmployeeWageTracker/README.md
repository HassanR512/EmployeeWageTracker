# Employee Work & Wage Tracker

A simple, modern, fully offline Android app for tracking employees, their daily
work/units, and wages. Built with Kotlin, Jetpack Compose (Material 3), and a
local Room (SQLite) database — no login, no internet required, and all data
persists on the device between sessions.

## What's included

- **Dashboard** — app title, search bar, "+ Add Employee" button, employee
  cards (tap to open, inline edit/delete).
- **Employee detail screen** — daily work records (date, days/units worked,
  rate, auto-calculated amount earned, optional notes), a Payments tab for
  recording amounts actually paid out, a month filter, and a monthly summary
  card showing total days worked, entries, total earned, total paid, and the
  remaining balance.
- Add / edit / delete for employees, daily records, and payments.
- Date picker for entering dates (no manual typing).
- All data stored locally with Room; nothing leaves the device.

## ⚠️ About the APK — please read

I can't compile an installable `.apk` inside this chat environment — building
an Android app requires the Android SDK and Google's Maven repository, which
this sandbox doesn't have network access to. What you have instead is the
**complete, ready-to-build Android Studio project**. Turning it into an APK
takes about 5–10 minutes using either option below.

### Option A — Build in Android Studio (recommended, easiest)

1. Install [Android Studio](https://developer.android.com/studio) (free) if
   you don't have it.
2. Unzip this project and choose **File → Open** in Android Studio, then
   select the `EmployeeWageTracker` folder.
3. Let Gradle sync finish (Android Studio will auto-generate the missing
   `gradle-wrapper.jar` the first time — this is normal).
4. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. When it finishes, click the **locate** link in the notification, or find
   the file at `app/build/outputs/apk/debug/app-debug.apk`.
6. Copy that `.apk` to your phone (USB, email, Drive, etc.) and open it to
   install. You'll need to allow "install from unknown sources" the first
   time — Android will prompt you automatically.

### Option B — Build for free with GitHub Actions (no local install needed)

A ready-made workflow is included at `.github/workflows/build-apk.yml`.

1. Create a new **public or private** GitHub repository and push this
   project to it.
2. GitHub will automatically run the "Build Debug APK" workflow (or trigger
   it manually from the **Actions** tab → *Build Debug APK* → *Run workflow*).
3. When it finishes (a few minutes), open the workflow run and download the
   **employee-wage-tracker-debug-apk** artifact — that's your `.apk`.
4. Transfer it to your phone and install as above.

### A note on "release" vs "debug" APKs

Both options above produce a **debug APK**, which installs and runs exactly
like a normal app — perfect for personal use. If you later want to publish
it on the Play Store, you'd generate a signed **release** build instead
(Android Studio: **Build → Generate Signed Bundle / APK**), which needs a
signing key you create yourself.

## Project structure

```
app/src/main/java/com/wagetracker/app/
├── data/            Room entities (Employee, DailyRecord, Payment), DAOs, database
├── repository/       Single repository wrapping the DAOs
├── viewmodel/         MainViewModel (dashboard/search) and EmployeeDetailViewModel
│                     (records, payments, month filter, monthly summary)
├── navigation/        Compose Navigation graph (Dashboard ↔ Employee detail)
├── ui/theme/          Colors, typography, Material 3 theme
├── ui/components/     Reusable dialogs and cards (add/edit employee, add/edit
│                     record, add payment, date picker, employee card, etc.)
└── ui/screens/        DashboardScreen, EmployeeDetailScreen
```

## Customizing

- **Currency label**: search for `"Rs."` in the `ui/components` and
  `ui/screens` folders and replace with your currency symbol.
- **App name / colors**: `res/values/strings.xml` and
  `ui/theme/Color.kt`.
- **Minimum Android version**: currently set to Android 8.0 (API 26) in
  `app/build.gradle.kts`, which covers the vast majority of active devices.
