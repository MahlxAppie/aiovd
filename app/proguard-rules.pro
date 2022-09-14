# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile



-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-classobfuscationdictionary encrypt.txt
-packageobfuscationdictionary encrypt.txt
-obfuscationdictionary encrypt.txt

-keep public class com.infusiblecoder.allinonevideodownloader.models.** {
  *;
}
-keep public class com.infusiblecoder.allinonevideodownloader.webservices.api.** {
  *;
}


-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}


-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class kotlin.Metadata { *; }

-keepattributes RuntimeVisibleAnnotations

-dontwarn com.android.org.conscrypt.SSLParametersImpl


-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**

-dontwarn org.jetbrains.annotations.**

### OkHttp.

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform


#new
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep @kotlinx.android.parcel.Parcelize public class *
-dontwarn kotlin.internal.annotations.AvoidUninitializedObjectCopyingCheck

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

#-keep class com.google.gson.** { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**


-dontwarn com.squareup.okhttp.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class android.support.v8.renderscript.** { *; }

-dontwarn jp.co.cyberagent.android.gpuimage.**


-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation






##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------






##### Google #####
# Databinding
-dontwarn android.databinding.**
# Dagger
-dontwarn com.google.errorprone.annotations.**
##################

##### OkHttp, Retrofit #####
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
#################

##### Moshi for JSON #####
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
###########################

##### gRPC #####
-dontwarn android.test.**
-dontwarn com.google.common.**
-dontwarn javax.naming.**
-dontwarn okio.**
-dontwarn org.junit.**
-dontwarn org.mockito.**
-dontwarn sun.reflect.**
# Ignores: can't find referenced class javax.lang.model.element.Modifier
-dontwarn com.google.errorprone.annotations.**
##################

##### Protobuf #####
-keep class * extends com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
####################

##### Crashlytics #####
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
#######################

##### Glide #####
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#################

##### Wasabeef #####
# Protobuf
-keep class jp.wasabeef.data.proto.** { *; }
-keep class jp.wasabeef.data.grpc.** { *; }
##################




# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# The following rules are used to strip any non essential Google Play Services classes and method.

# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

# For old ads classes
-keep public class com.google.ads.**{
   public *;
}

# For mediation
-keepattributes *Annotation*

# Other required classes for Google Play Services
# Read more at http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}



-dontwarn org.apache.commons.**
-dontwarn org.apache.http.client.HttpClient
-dontwarn javax.naming.**
-dontwarn org.ietf.jgss.**


-keep class org.apache.http.** {
    *;
}

-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keepattributes *Annotation*
# in order to provide the most meaningful crash reports, add the following line:
-keepattributes SourceFile,LineNumberTable
# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# Add this global rule
-keepattributes Signature


-keep class br.com.mauker.MsvAuthority
-keepclassmembers class br.com.mauker.** { *; }



-dontwarn unirest.shaded.**
-keep public class unirest.shaded.** {*;}
-keep class unirest.shaded.** {*;}
-keep abstract class unirest.shaded.** {*;}
-keep interface unirest.shaded.** {*;}
-keepclassmembers class unirest.shaded.** { *; }
-keepclasseswithmembernames class unirest.shaded.** { *; }
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
#-keep,allowobfuscation,allowshrinking class unirest.shaded.org.apache.http.nio.conn.SchemeIOSessionStrategy
#-keep,allowobfuscation,allowshrinking class * extends unirest.shaded.org.apache.http.nio.conn.SchemeIOSessionStrategy
#-keep,allowobfuscation,allowshrinking class unirest.shaded.org.apache.http.nio.conn.ssl.SSLIOSessionStrategy




-dontwarn org.apache.**
-keep public class org.apache.** {*;}
-keep class org.apache.** {*;}
-keep abstract class org.apache.** {*;}
-keep interface org.apache.** {*;}
-keepclassmembers class org.apache.** { *; }
-keepclasseswithmembernames class org.apache.** { *; }


-dontwarn com.puppycrawl.tools.**
-keep public class com.puppycrawl.tools.** {*;}
-keep class com.puppycrawl.tools.** {*;}
-keep interface com.puppycrawl.tools.** {*;}
-keepclassmembers class com.puppycrawl.tools.** { *; }
-keepclasseswithmembernames class com.puppycrawl.tools.** { *; }

-dontwarn com.fasterxml.jackson.**
-keep public class com.fasterxml.jackson.** {*;}
-keep class com.fasterxml.jackson.** {*;}
-keep interface com.fasterxml.jackson.** {*;}
-keepclassmembers class com.fasterxml.jackson.** { *; }
-keepclasseswithmembernames class com.fasterxml.jackson.** { *; }

-keep class com.google.common.** { *; }
-keep class android.net.** { *; }
-keep class com.android.internal.http.** { *; }
-keep class org.hamcrest.** { *; }


-dontwarn kong.unirest.**
-keep public class kong.unirest.** {*;}
-keep class kong.unirest.** {*;}
-keep abstract class kong.unirest.** {*;}
-keep interface kong.unirest.** {*;}
-keepclassmembers class kong.unirest.** { *; }
-keepclasseswithmembernames class kong.unirest.** { *; }



-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.**

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}




