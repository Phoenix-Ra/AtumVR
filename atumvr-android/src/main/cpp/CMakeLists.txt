# CMake build for the atumvr-android native layer.
#
# Produces a single shared library, libatumvr_native.so, that hosts:
#   - the JNI entry points called from NativeXRBridge.java
#   - (later) the OpenXR instance/session/swapchain/frame-loop driving Quest
#
# Linkage:
#   - libopenxr_loader.so   (Meta OpenXR Mobile loader, consumed via prefab
#                            from the openxr_loader_for_android AAR)
#   - EGL / GLESv3          (NDK system libs, for the GLES graphics binding)
#   - android / log         (NDK system libs, for ANativeActivity + ALog)
#
# The OpenXR loader is OPTIONAL at this stage — the build target only links
# against it if `ATUMVR_WITH_OPENXR_LOADER` is on (set by Gradle once the
# `openxr_loader_for_android` AAR dependency is enabled). This lets the
# native module compile in isolation while step 1 (NDK + CMake plumbing) is
# being validated, before step 2 wires up the real Meta loader.

cmake_minimum_required(VERSION 3.22.1)
project(atumvr_native LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# -----------------------------------------------------------------------------
# Sources
# -----------------------------------------------------------------------------
add_library(atumvr_native SHARED
    atumvr_jni.cpp
)

target_include_directories(atumvr_native PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# -----------------------------------------------------------------------------
# NDK system libraries
# -----------------------------------------------------------------------------
find_library(log-lib     log)
find_library(android-lib android)
find_library(egl-lib     EGL)
find_library(glesv3-lib  GLESv3)

target_link_libraries(atumvr_native
    PRIVATE
        ${log-lib}
        ${android-lib}
        ${egl-lib}
        ${glesv3-lib}
)

# -----------------------------------------------------------------------------
# Optional: Meta OpenXR Mobile loader (consumed via AGP prefab)
# -----------------------------------------------------------------------------
# AGP exposes the loader through CMAKE_FIND_ROOT_PATH when the consuming
# module declares the prefab dependency. Tolerate its absence so step 1 can
# build before step 2 lands the AAR.

option(ATUMVR_WITH_OPENXR_LOADER "Link against the Meta OpenXR Mobile loader" OFF)

if(ATUMVR_WITH_OPENXR_LOADER)
    find_package(OpenXR REQUIRED CONFIG)
    target_link_libraries(atumvr_native PRIVATE OpenXR::openxr_loader)
    target_compile_definitions(atumvr_native PRIVATE ATUMVR_HAS_OPENXR=1)
else()
    target_compile_definitions(atumvr_native PRIVATE ATUMVR_HAS_OPENXR=0)
endif()

# -----------------------------------------------------------------------------
# Hardening / warnings
# -----------------------------------------------------------------------------
target_compile_options(atumvr_native PRIVATE
    -Wall
    -Wextra
    -Wno-unused-parameter
    -fvisibility=hidden        # JNI entry points are explicitly exported
    -fvisibility-inlines-hidden
)
