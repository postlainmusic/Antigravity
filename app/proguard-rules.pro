# Antigravity ProGuard & R8 Optimization Rules

# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor Client
-keep class io.ktor.** { *; }

# Jetpack Compose
-keep class androidx.compose.material3.** { *; }
