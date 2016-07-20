LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under,src) \
					src/com/example/screenshotsample/IScreenshotControl.aidl

LOCAL_PACKAGE_NAME := ScreenShotSampleServer

LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
