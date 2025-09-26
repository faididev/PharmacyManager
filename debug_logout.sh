#!/bin/bash

echo "=== PharmacyManager Logout Debug Script ==="
echo "This script will monitor logs for authentication and session issues."
echo "Press Ctrl+C to stop."
echo ""

# Clear existing logs
adb logcat -c

echo "Starting log monitoring..."
echo "Now try to create a product or category and watch for these patterns:"
echo "- SessionManager logs"
echo "- Token validation logs"
echo "- 401 Unauthorized errors"
echo "- Authentication errors"
echo ""

# Monitor logs for relevant patterns
adb logcat | grep -E "(SessionManager|Token|Authentication|401|Unauthorized|MainActivity|SplashActivity|AddProductFragment|AddCategoryFragment|ProductRepository|CategoryRepository)" --color=always
