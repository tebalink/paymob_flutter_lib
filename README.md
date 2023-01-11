# paymob_flutter_lib

This plugin is for [paymob.pk](https://docs.paymob.pk/). It's implementation of the native Paymob SDK of Android and IOS to work on Flutter environment.

## Usage

Add this to your package's `pubspec.yaml` file:

```yaml
dependencies:
  paymob_flutter_lib: ^1.0.3
```

## Android

You have to edit `AndroidManifest.xml` file with following.

```manifest
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
     xmlns:tools="http://schemas.android.com/tools">
    <application
        android:supportsRtl="true"
        tools:replace="android:label,android:supportsRtl">
    </appliaction>
</manifest>
```

Add following statement to `android/settings.gradle` file of your project.

```gradle
apply from: "$flutterSdkPath/.pub-cache/hosted/pub.dartlang.org/paymob_flutter_lib-1.0.3/settings.gradle"
```

## ios

Add this to your `Podfile` file

```podfile
# Uncomment this line to define a global platform for your project
platform :ios, '12.0'
```