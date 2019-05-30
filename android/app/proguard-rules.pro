# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user1\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#IMPORTANT: only run one optimization pass
-optimizationpasses 1
-keep @interface *

#keep annotation for marking classes not to be obfuscated
-keep class ro.vodafone.mcare.android.DontObfuscate
#keep classes marked by DontOffuscate annotation
-keep @ro.vodafone.mcare.android.DontObfuscate class * { *; }

-keeppackagenames ro.vodafone.mcare.android.client.model.**

-keepattributes *Annotation*
-keepattributes Signature,SourceFile,LineNumberTable,InnerClasses,Exceptions,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# keep models
-keep class ro.vodafone.mcare.android.rest.requests.**,ro.vodafone.mcare.android.client.model.** { *; }
# do not obfuscate 3rd party libraries yet
-keep class !ro.vodafone.mcare.android.** { *; }
# keep for navigation
-keep class ro.vodafone.mcare.android.utils.navigation.IntentActionName** { *; }
-keep class * extends android.support.v4.app.Fragment { }

# don't warn missing methods in external libs, as external libs won't be obfuscated
-dontwarn !ro.vodafone.mcare.android.**


-keep enum * {
    public static **[] values(); public static ** valueOf(java.lang.String);
    *;
}

#retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit.http.* <methods>;
 }

-keep class * extends io.realm.RealmObject
-keepnames class * extends io.realm.RealmObject

-keep class * extends java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# remove logging
-assumenosideeffects public class ro.vodafone.mcare.android.utils.D {
    public static void d();
    public static void w();
    public static void v();
    public static void i();
    public static void e();
    public static void d(java.lang.String);
    public static void w(java.lang.String);
    public static void v(java.lang.String);
    public static void i(java.lang.String);
    public static void e(java.lang.String);
 }
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
 }

-ignorewarnings
