// Native entry points for the AtumVR Android runtime.
//
// Step 1 (this file): only the JNI plumbing is in place — load lib, log a
// banner, return a build-info string up to Java. The real OpenXR work
// (loader init, instance, system, session, swapchain, frame loop) is
// scaffolded in subsequent steps and lives behind ATUMVR_HAS_OPENXR.
//
// Symbols match the Java side in
// me.phoenixra.atumvr.android.nativebridge.NativeXRBridge using the
// Java_<package>_<class>_<method> mangling scheme.

#include <jni.h>
#include <android/log.h>
#include <string>

#define ATUMVR_LOG_TAG "AtumVR-Native"
#define ATUMVR_LOGI(...) __android_log_print(ANDROID_LOG_INFO,  ATUMVR_LOG_TAG, __VA_ARGS__)
#define ATUMVR_LOGW(...) __android_log_print(ANDROID_LOG_WARN,  ATUMVR_LOG_TAG, __VA_ARGS__)
#define ATUMVR_LOGE(...) __android_log_print(ANDROID_LOG_ERROR, ATUMVR_LOG_TAG, __VA_ARGS__)

namespace {

constexpr const char* kBuildVariant =
#ifdef NDEBUG
    "release"
#else
    "debug"
#endif
;

constexpr int kAtumVrNativeAbiVersion = 1;

}  // namespace

extern "C" {

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* /*reserved*/) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        ATUMVR_LOGE("JNI_OnLoad: failed to obtain JNIEnv (need JNI 1.6)");
        return JNI_ERR;
    }
    ATUMVR_LOGI("AtumVR native loaded (variant=%s, abi=%d, openxr=%s)",
                kBuildVariant,
                kAtumVrNativeAbiVersion,
#if ATUMVR_HAS_OPENXR
                "linked"
#else
                "stub"
#endif
    );
    return JNI_VERSION_1_6;
}

JNIEXPORT __attribute__((visibility("default"))) jint JNICALL
Java_me_phoenixra_atumvr_android_nativebridge_NativeXRBridge_nativeAbiVersion(
        JNIEnv* /*env*/, jclass /*clazz*/) {
    return kAtumVrNativeAbiVersion;
}

JNIEXPORT __attribute__((visibility("default"))) jstring JNICALL
Java_me_phoenixra_atumvr_android_nativebridge_NativeXRBridge_nativeBuildInfo(
        JNIEnv* env, jclass /*clazz*/) {
    std::string info = "atumvr_native ";
    info += kBuildVariant;
    info += " (openxr=";
#if ATUMVR_HAS_OPENXR
    info += "linked";
#else
    info += "stub";
#endif
    info += ")";
    return env->NewStringUTF(info.c_str());
}

}  // extern "C"
