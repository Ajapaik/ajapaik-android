Ajapaik
=======

About Ajapaik
-------------

To be written

Contributing
------------

To be written

Building
--------

Make sure you have Android SDK installed and the `android` command executable in your path. You'll also need Apache Ant. Building Ajapaik requires you to have android-17 platform installed (this can be done with the SDK manager bundled with the Android SDK).

First off, generate the `local.properties` files for both the Facebook v3 SDK and the Android app:

```
cd facebook
android update lib-project -p .
cd ..
cd app
android update project -p .
```

Next you'll need a keystore, the `app/keystore.properties` file and the `app/res/values/api_keys.xml` file (not included in this repository).

Sample `keystore.properties`:

```
key.store=<relative-path-to-keystore>
key.store.password=<keystore-password>
key.alias=<key-alias>
key.alias.password=<key-password>
```

Sample `api_keys.xml`:

```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>
<string name="maps_api_key"><!-- google Maps V3 API key --></string>
<string name="fb_app_id"><!-- facebook app id --></string>
<string name="crittercism_id"><!-- crittercism ID --></string>
<string name="flurry_debug_key"><!-- debug key for Flurry --></string>
<string name="flurry_live_key"><!-- live key for Flurry --></string>
</resources>
```

Compile the project (bump the version in `app/AndroidManifest.xml` if need be):

```
cd app
ant clean release
```

Congrats! You now have a working binary at `app/bin/ajapaik-android-release.apk`.

License
-------

MIT License, see the LICENSE file.