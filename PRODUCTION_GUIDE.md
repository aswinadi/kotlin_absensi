# Android Production Guide

This guide details the steps to prepare and build your Android application for production release.

## 1. Configure Production API URL

In `app/build.gradle.kts`, update the `release` build type with your production API URL:

```kotlin
buildTypes {
    release {
        // ...
        buildConfigField("String", "BASE_URL", "\"https://api.yourdomain.com/api/v1/\"")
    }
}
```

## 2. Add App Icon

1.  Open Android Studio.
2.  In the Project view (left side), right-click on the `app/src/main/res` folder.
3.  Select **New** > **Image Asset**.
4.  In the **Icon Type** dropdown, keep "Launcher Icons (Adaptive and Legacy)".
5.  In the **Source Asset** section:
    *   **Path**: Click the folder icon and select your image file.
6.  Use the **Scaling** slider to adjust the size.
7.  Click **Background Layer** tab to set a background color or image if needed.
8.  Click **Next**, then **Finish**.
    *   This will automatically resize your image into all necessary resolutions (`mipmap-*` folders).

## 3. Generate Release Keystore

You need a cryptographic key to sign your app. **Keep this file secure and never commit it to Git.**

1.  Open Android Studio.
2.  Go to **Build** > **Generate Signed Bundle / APK**.
3.  Select **Android App Bundle** or **APK**.
4.  Click **Next**.
5.  Under **Key store path**, click **Create new...**.
6.  Fill in the details:
    *   **Key store path**: Save it outside your project folder or in a private folder (e.g., `~/keystores/my-app.jks`).
    *   **Password**: Create a strong password.
    *   **Alias**: Name for your key (e.g., `key0`).
    *   **Key Password**: create a strong password (can be same as keystore).
    *   **Validity**: 25 years or more.
    *   **Certificate**: Fill in your name/org details.
7.  Click **OK**.

## 3. Configure Signing in Project

To build signed releases automatically or via command line without committing secrets:

1.  Open or create `local.properties` in the root of your project (this file is git-ignored).
2.  Add your keystore details:

```properties
store.file=C:\\path\\to\\your\\keystore.jks
store.password=your_store_password
key.alias=your_key_alias
key.password=your_key_password
```

3.  Update `app/build.gradle.kts` to read these properties:

```kotlin
import java.io.FileInputStream
import java.util.Properties

// ... inside android { ...

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("local.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                
                storeFile = file(keystoreProperties["store.file"] as String)
                storePassword = keystoreProperties["store.password"] as String
                keyAlias = keystoreProperties["key.alias"] as String
                keyPassword = keystoreProperties["key.password"] as String
            }
        }
    }

    buildTypes {
        release {
            // ...
            signingConfig = signingConfigs.getByName("release")
        }
    }
```

## 4. Build for Production

### Generate Signed Bundle (Recommended for Play Store)
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

### Generate Signed APK (For direct distribution)
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`
