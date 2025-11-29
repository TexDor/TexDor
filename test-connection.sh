#!/bin/bash
# Test MySQL Connection
echo "Testing MySQL connection..."
echo "Trying to connect to: localhost:3306 as root"

# Try to connect using nc (netcat) to test if port 3306 is open
nc -z localhost 3306
if [ $? -eq 0 ]; then
    echo "✅ MySQL port 3306 is accessible"
else
    echo "❌ MySQL port 3306 is NOT accessible"
    echo "MySQL might not be running or not installed"
    echo ""
    echo "To install MySQL:"
    echo "  brew install mysql"
    echo ""
    echo "To start MySQL:"
    echo "  brew services start mysql"
fi

