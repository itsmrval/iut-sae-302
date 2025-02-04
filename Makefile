BACKEND_DIR = ./backend
CLIENT_DIR = ./clients/c

BACKEND_TARGET = server
CLIENT_TARGET = client

DIST_DIR = ./dist

GRADLE = ./gradle/bin/gradle

ANDROID_SDK = ~/Library/Android/sdk/
APK_PATH = ./clients/android/app/build/outputs/apk/debug/app-debug.apk
AVD_NAME = Medium_Phone_API_35

all: create_dist_dir backend client_c client_java


backend:
	$(MAKE) -C $(BACKEND_DIR) 

client_c:
	$(MAKE) -C $(CLIENT_DIR)

client_java:
	$(GRADLE) -p ./clients/java buildAndMoveJar

client_android:
	ANDROID_HOME=$(ANDROID_SDK) $(GRADLE) -p ./clients/android assembleDebug

run_android:
	@if ! $(ANDROID_SDK)/platform-tools/adb get-state 1>/dev/null 2>&1; then \
		echo "Démarrage de l'émulateur $(AVD_NAME)..."; \
		$(ANDROID_SDK)/emulator/emulator -avd $(AVD_NAME) -netdelay none -netspeed full -no-snapshot-load & \
		sleep 20; \
	fi
	$(ANDROID_SDK)/platform-tools/adb wait-for-device
	$(ANDROID_SDK)/platform-tools/adb install -r $(APK_PATH)
	$(ANDROID_SDK)/platform-tools/adb shell monkey -p com.absencemanager 1


clean:
	$(MAKE) -C $(BACKEND_DIR) clean
	$(MAKE) -C $(CLIENT_DIR) clean
	$(GRADLE) -p ./clients/java cleanDistDir
	$(GRADLE) -p ./clients/android clean
	rm -rf ./clients/android/build/

create_dist_dir:
	mkdir -p $(DIST_DIR)

.PHONY: all backend client_c client_java clean
