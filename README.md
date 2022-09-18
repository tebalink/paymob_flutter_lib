# paymob_flutter_lib

This plugin is for [paymob.pk](https://docs.paymob.pk/). It's implementation of the native Paymob SDK of Android and IOS to work on Flutter environment.

> iOS is currently under development.

## Usage

Add this to your package's `pubspec.yaml` file:

```yaml
dependencies:
  paymob_flutter_lib: ^1.0.0
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