# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room entities
-keep class com.tracko.app.data.local.entity.** { *; }

# Keep Retrofit interfaces
-keep,allowobfuscation interface com.tracko.app.data.remote.api.** { *; }

# Keep Gson serialized classes
-keep class com.tracko.app.data.remote.dto.** { *; }

# Keep Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep R8
-keepattributes Signature
-keepattributes *Annotation*

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
