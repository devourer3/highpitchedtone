# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

set(pathOPENCV /Users/gibeom/dev/OpenCV-android-sdk)
set(pathPROJECT /Users/gibeom/AndroidStudioProjects/High_pitched_tone)
set(pathLIBOPENCV_JAVA ${pathPROJECT}/app/src/main/JniLibs/${ANDROID_ABI}/libopencv_java3.so)

set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${pathOPENCV}/sdk/native/jni/include)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp)

add_library(lib_opencv SHARED IMPORTED)

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              android-lib android
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib
                       ${android-lib}
                       lib_opencv
                       # Links the log library to the target library.
                       # included in the NDK.
                       ${log-lib} )