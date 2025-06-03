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

# Added:
# When building after adding buildTypes -> release, the task :app:minifyDebugWithR8 outputed a file
# app/build/outputs/mapping/debug/missing_rules.txt
# requesting to add the following rules

-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.ElementKind
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.SimpleTypeVisitor8
-dontwarn org.slf4j.impl.StaticLoggerBinder

# to make it work with firebase https://stackoverflow.com/questions/60719791/firebase-firestore-variable-name-changed
-keepparameternames
-keepattributes MethodParameters
-keepattributes Signature

-keepclassmembers class p4ulor.obj.detector.data.domains.firebase.** {
  *;
}

-keepnames class p4ulor.obj.detector.data.domains.firebase.** {
  *;
}

-keepclassmembers class p4ulor.obj.detector.data.sources.cloud.firebase.** {
  *;
}

-keepnames class p4ulor.obj.detector.data.sources.cloud.firebase.** {
  *;
}

## for firebase

-keepclassmembers class com.google.firebase.** {
  *;
}

-keepnames class com.google.firebase.** {
  *;
}

# for mediapipe

-keepclassmembers class com.google.mediapipe.** {
  *;
}

-keepnames class com.google.mediapipe.** {
  *;
}

-dontwarn com.google.mediapipe.proto.CalculatorProfileProto$CalculatorProfile
-dontwarn com.google.mediapipe.proto.GraphTemplateProto$CalculatorGraphTemplate
