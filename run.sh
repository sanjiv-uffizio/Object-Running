#!/bin/bash

# Get the method name from terminal input
echo "First argument is for: Method, Seconds is for: Object file, Third is for: Path file, Fourth is for: Server"

METHOD=$1
OBJECTFILE=$2
PATHFILE=$3
SERVER=$4

if [ -z "$METHOD" ]; then
  echo "❌ Please provide a method name (e.g., add, status, remove, refresh)"
  exit 1
fi

cd "/home/uffizio/eclipse-workspace/ObjectRunning" || exit

# Generate dynamic TestNG XML file
cat <<EOF > testng.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Object Running">
  <test name="Object Running">
   <parameter name="objectfile" value="$OBJECTFILE"></parameter>
       <parameter name="pathfile" value="$PATHFILE"></parameter>
       <parameter name="server" value="$SERVER"></parameter>
       
    <classes>	
      <class name="projects.trakzee.testCases.web.multiobjectRunning.MultiObjectRunning">
     
        <methods>
          <include name="$METHOD"/>
        </methods>
      </class>
    </classes>
  </test>
</suite>
EOF

# Run Maven with the generated TestNG XML
mvn clean test -DsuiteXmlFile=testng.xml
