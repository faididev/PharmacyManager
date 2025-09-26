#!/bin/bash

echo "=== PharmacyManager Complete Test ==="
echo "This script will monitor the app during login and product creation."
echo ""

# Clear logs
adb logcat -c

echo "Starting comprehensive log monitoring..."
echo "Now please:"
echo "1. Login to the app"
echo "2. Try to create a product"
echo "3. Try to create a category"
echo "4. Watch for any errors or unexpected logouts"
echo ""

# Monitor logs for authentication and product creation
adb logcat | grep -E "(MainActivity|LoginActivity|AddProductFragment|AddCategoryFragment|SessionManager|Token|Authentication|401|Unauthorized|Error|Exception|Success|Product created|Category created)" --color=always
