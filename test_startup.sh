#!/bin/bash

echo "=== PharmacyManager Startup Test ==="
echo "Monitoring app startup and session management..."
echo ""

# Clear logs
adb logcat -c

echo "Starting log monitoring for 30 seconds..."
echo "Launch the app now and watch for startup issues..."
echo ""

# Monitor logs for 30 seconds
timeout 30s adb logcat | grep -E "(MainActivity|SplashActivity|LoginActivity|SessionManager|Token|FATAL|AndroidRuntime|Exception)" --color=always

echo ""
echo "=== Test Complete ==="
