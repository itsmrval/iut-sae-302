BACKEND_DIR = ./backend
CLIENT_DIR = ./clients/c

BACKEND_TARGET = server
CLIENT_TARGET = client

DIST_DIR = ./dist

GRADLE = ./gradle/bin/gradle

ANDROID_SDK = ~/Library/Android/sdk/
APK_PATH = ./clients/android/app/build/outputs/apk/debug/app-debug.apk
AVD_NAME = Medium_Phone_API_35

all: backend client_c client_java client_android

backend:
	$(MAKE) -C $(BACKEND_DIR) 

client_c:
	$(MAKE) -C $(CLIENT_DIR)

client_java:
	$(GRADLE) -p ./clients/java buildAndMoveJar

client_android:
	ANDROID_HOME=$(ANDROID_SDK) $(GRADLE) -p ./clients/android assembleDebug
	$(MAKE) move_apk

run_android:
	@if ! $(ANDROID_SDK)/platform-tools/adb get-state 1>/dev/null 2>&1; then \
		$(ANDROID_SDK)/emulator/emulator -avd $(AVD_NAME) -netdelay none -netspeed full -no-snapshot-load & \
		sleep 20; \
	fi
	$(ANDROID_SDK)/platform-tools/adb wait-for-device
	$(ANDROID_SDK)/platform-tools/adb install -r $(DIST_DIR)/client_android/app.apk
	$(ANDROID_SDK)/platform-tools/adb shell monkey -p com.absencemanager 1

clean:
	$(MAKE) -C $(BACKEND_DIR) clean
	$(MAKE) -C $(CLIENT_DIR) clean
	$(GRADLE) -p ./clients/java cleanDistDir
	$(GRADLE) -p ./clients/android clean
	rm -rf $(DIST_DIR)/

move_apk:
	@if [ -f "$(APK_PATH)" ]; then \
		mkdir -p $(DIST_DIR)/client_android/; \
		mv $(APK_PATH) $(DIST_DIR)/client_android/app.apk; \
		echo "APK successfully moved."; \
	else \
		echo "APK not found at $(APK_PATH)"; \
	fi
	rm -rf ./clients/android/build/
	#rm -rf ./clients/android/app/build/


.PHONY: all backend client_c client_java client_android run_android clean move_apk
