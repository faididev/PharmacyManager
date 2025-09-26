#!/bin/bash

echo "=== PharmacyManager Error Monitoring Script ==="
echo "This script will monitor Android logs for product creation errors"
echo "Press Ctrl+C to stop monitoring"
echo ""

# Clear existing logs
adb logcat -c

echo "Logs cleared. Now reproduce the error by creating a product with image."
echo "Monitoring for errors..."
echo ""

# Monitor logs for relevant error messages
adb logcat | grep -E "(AddProductFragment|ProductRepository|ApiClient|===.*ERROR.*===|Error|Exception|Failed|HTTP [45][0-9][0-9])" --line-buffered
