#!/usr/bin/env node

/**
 * Antigravity ADB Device Deployer Script
 * Installs the built debug APK onto a connected Android device or emulator via ADB.
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

function run() {
  const workspaceRoot = path.resolve(__dirname, '../..');
  const apkPath = path.join(workspaceRoot, 'app/build/outputs/apk/debug/app-debug.apk');

  console.log('===============================================================');
  console.log('  ANTIGRAVITY ANDROID DEVICE DEPLOYER (ADB)');
  console.log('===============================================================\n');

  // Check ADB availability
  let adbDevices = '';
  try {
    adbDevices = execSync('adb devices', { encoding: 'utf8' });
  } catch (e) {
    console.error('ERROR: ADB (Android Debug Bridge) is not found in PATH.');
    console.log('\nHướng dẫn khắc phục:');
    console.log('1. Cài đặt Android Studio hoặc Android SDK Platform-Tools.');
    console.log('2. Thêm thư mục Platform-Tools vào biến môi trường PATH (ví dụ: %LOCALAPPDATA%\\Android\\Sdk\\platform-tools).');
    console.log('3. Hoặc mở project trực tiếp trong Android Studio và nhấn nút Run ▶.');
    process.exit(1);
  }

  const lines = adbDevices.trim().split('\n').slice(1).filter(l => l.includes('device') && !l.includes('offline'));

  if (lines.length === 0) {
    console.error('Không tìm thấy thiết bị Android hoặc máy ảo nào đang kết nối.');
    console.log('\nHướng dẫn kết nối điện thoại:');
    console.log('1. Bật "Tùy chọn cho nhà phát triển" (Developer Options) trên điện thoại.');
    console.log('2. Bật "Gỡ lỗi USB" (USB Debugging).');
    console.log('3. Cắm cáp USB nối điện thoại với máy tính và chọn "Luôn cho phép từ máy tính này".');
    console.log('4. Chạy lại lệnh: node .agents/scripts/deploy-device.js');
    process.exit(1);
  }

  console.log(`Tìm thấy ${lines.length} thiết bị Android đang kết nối:\n${lines.join('\n')}\n`);

  if (!fs.existsSync(apkPath)) {
    console.log('Chưa tìm thấy file APK đã biên dịch. Đang tự động build APK Debug...');
    const gradlew = path.join(workspaceRoot, process.platform === 'win32' ? 'gradlew.bat' : 'gradlew');
    try {
      execSync(`${gradlew} assembleDebug`, { cwd: workspaceRoot, stdio: 'inherit' });
    } catch (e) {
      console.error('Lỗi khi biên dịch APK. Vui lòng kiểm tra lại cấu hình JDK 17.');
      process.exit(1);
    }
  }

  console.log(`\nĐang cài đặt file APK: ${apkPath} lên thiết bị...`);
  try {
    execSync(`adb install -r "${apkPath}"`, { stdio: 'inherit' });
    console.log('\n>>> CÀI ĐẶT THÀNH CÔNG LÊN THIẾT BỊ ANDROID! <<<');
    console.log('Bạn có thể mở ứng dụng "Antigravity" trên màn hình chính điện thoại để bắt đầu test.');
  } catch (e) {
    console.error('Lỗi khi cài đặt APK qua ADB.');
    process.exit(1);
  }
}

run();
