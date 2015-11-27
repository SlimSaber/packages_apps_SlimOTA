LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

ifneq ($(ANDROID_BUILD_TOP),)
    $(shell cp -u $(ANDROID_BUILD_TOP)/ota_conf $(LOCAL_PATH)/app/src/main/assets/)
endif

LOCAL_MODULE_TAGS := optional
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PACKAGE_NAME := SlimOTA
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res
LOCAL_ASSET_DIR := $(LOCAL_PATH)/app/src/main/assets

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_STATIC_JAVA_LIBRARIES += libcwac

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libcwac:app/libs/cwac-wakeful-1.0.5.jar

include $(BUILD_MULTI_PREBUILT)
