# run.ps1 — one-click build + run on the emulator (no Android Studio).
# Usage:  .\run.ps1            (boot emulator if needed, build, install, launch)
#         .\run.ps1 -Avd Name  (use a different AVD)

param(
    [string]$Avd = "Pixel_8_Pro",
    [string]$Package = "nz.ac.ara.bcde223.minimala3skeleton",
    [string]$Activity = ".ui.MainActivity"
)

$ErrorActionPreference = "Stop"

$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$adb = "$sdk\platform-tools\adb.exe"
$emu = "$sdk\emulator\emulator.exe"

# 1. Boot the emulator only if no device is already attached.
$attached = (& $adb devices | Select-String "device$")
if (-not $attached) {
    Write-Host "Booting emulator $Avd ..."
    Start-Process $emu -ArgumentList "-avd", $Avd
    & $adb wait-for-device
    Write-Host "Waiting for boot to finish ..."
    do {
        Start-Sleep -Seconds 2
        $done = (& $adb shell getprop sys.boot_completed 2>$null) -match "1"
    } until ($done)
    Write-Host "Emulator ready."
} else {
    Write-Host "Emulator already running."
}

# 2. Build + install the debug APK.
Write-Host "Building + installing ..."
& "$PSScriptRoot\gradlew.bat" installDebug
if ($LASTEXITCODE -ne 0) { throw "Gradle build failed (exit $LASTEXITCODE)." }

# 3. Wake + unlock the screen (cold-booted emulators sleep -> black screen).
& $adb shell input keyevent KEYCODE_WAKEUP
& $adb shell input keyevent 82

# 4. Launch the app.
Write-Host "Launching $Package ..."
& $adb shell am start -n "$Package/$Activity"
Write-Host "Done."
