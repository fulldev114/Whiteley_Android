LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
# Here we give our module name and source file(s)
LOCAL_MODULE := blur
LOCAL_SRC_FILES := blur.cpp
LOCAL_LDLIBS := -llog
LOCAL_LDFLAGS += -ljnigraphics

include $(BUILD_SHARED_LIBRARY)