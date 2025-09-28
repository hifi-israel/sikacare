# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Google Credential Manager classes
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

# Keep Google ID classes
-keep class com.google.android.libraries.identity.googleid.** { *; }

# Keep Supabase classes
-keep class io.github.jan.supabase.** { *; }
