#!/bin/bash

# File paths
QUARTZ_PROPERTIES_FILE="src/main/resources/quartz.properties"
APPLICATION_PROPERTIES_FILE="src/main/resources/application.properties"
APPLICATION_LOCAL_PROPERTIES_FILE="src/main/resources/application-local2.properties"
LOG4J2_XML_FILE="src/main/resources/log4j2-spring.xml"
POM_FILE="pom.xml"

# Check if the quartz.properties file exists
if [ ! -f "$QUARTZ_PROPERTIES_FILE" ]; then
  echo "Error: quartz.properties file not found at $QUARTZ_PROPERTIES_FILE"
  exit 1
fi

# Uncomment the required lines by removing the # at the beginning
sed -i.bak 's/^# \(org\.quartz\.dataSource\.quartzDataSource\.URL=.*\)/\1/' "$QUARTZ_PROPERTIES_FILE"
sed -i.bak 's/^# \(org\.quartz\.dataSource\.quartzDataSource\.user=.*\)/\1/' "$QUARTZ_PROPERTIES_FILE"
sed -i.bak 's/^# \(org\.quartz\.dataSource\.quartzDataSource\.password=.*\)/\1/' "$QUARTZ_PROPERTIES_FILE"
sed -i.bak 's/^# \(org\.quartz\.dataSource\.quartzDataSource\.maxPoolSize =.*\)/\1/' "$QUARTZ_PROPERTIES_FILE"
sed -i.bak 's/^\(org\.quartz\.dataSource\.quartzDataSource\.jndiURL=.*\)/# \1/' "$QUARTZ_PROPERTIES_FILE"

# Optional: Remove the backup file created by sed (quartz.properties.bak)
rm -f "${QUARTZ_PROPERTIES_FILE}.bak"

echo "Lines successfully uncommented in $QUARTZ_PROPERTIES_FILE"

# Check if the application-vat.properties file exists
if [ ! -f "$POM_FILE" ]; then
  echo "Error: pom.xml file not found at $POM_FILE"
  exit 1
fi

# Copy application-local2.properties to application.properties
cp "$APPLICATION_LOCAL_PROPERTIES_FILE" "$APPLICATION_PROPERTIES_FILE"

echo "Copied $APPLICATION_LOCAL_PROPERTIES_FILE to $APPLICATION_PROPERTIES_FILE"

if [ ! -f "$LOG4J2_XML_FILE" ]; then
  echo "Error: log4j2.xml file not found at $LOG4J2_XML_FILE"
  exit 1
fi

# Uncomment the required lines in log4j2.xml
sed -i.bak 's#<!-- \(\s*<Property name="connectionString">jdbc:informix-sqli://.*</Property>\) -->#\1#' "$LOG4J2_XML_FILE"
sed -i.bak 's#<!-- \(\s*<Property name="dbdriver">com.informix.jdbc.IfxDriver</Property>\) -->#\1#' "$LOG4J2_XML_FILE"
sed -i.bak 's#<!-- \(\s*<Property name="dbusername">informix</Property>\) -->#\1#' "$LOG4J2_XML_FILE"
sed -i.bak 's#<!-- \(\s*<Property name="dbpassword">SeizeTheDay!</Property>\) -->#\1#' "$LOG4J2_XML_FILE"

# Comment out enable-jndi
sed -i.bak 's/\(<Property name="enable-jndi">true<\/Property>\)/<!-- \1 -->/' "$LOG4J2_XML_FILE"

# Uncomment the <DriverManager> line
sed -i.bak 's#<!-- \(\s*<DriverManager connectionString="\${connectionString}" driverClassName="\${dbdriver}" username="\${dbusername}" password="\${dbpassword}" />\) -->#\1#' "$LOG4J2_XML_FILE"



# Comment out the <DataSource> line
sed -i.bak 's/\(<DataSource jndiName="ssm_jndi" \/>\)/<!-- \1 -->/' "$LOG4J2_XML_FILE"

# Optional: Remove the backup file created by sed (log4j2.xml.bak)
rm -f "${LOG4J2_XML_FILE}.bak"

# Comment out the exclusion tags for tomcat in spring-boot-starter-web
sed -i.bak '/<exclusions>/,/<\/exclusions>/{
    N;N;
    /<exclusion>/,/<\/exclusion>/{
        N;N;
        /<groupId>org.springframework.boot<\/groupId>/{
            N;
            /<artifactId>spring-boot-starter-tomcat<\/artifactId>/{
                s/<exclusion>/<!-- <exclusion>/;
                s/<\/exclusion>/<\/exclusion> -->/;
            }
        }
    }
}' "$POM_FILE"


sed -i.bak '/<exclusions>/,/<\/exclusions>/{
    N;N;
    /<exclusion>/,/<\/exclusion>/{
        N;N;
        /<groupId>org.apache.tomcat.embed<\/groupId>/{
            N;
            /<artifactId>tomcat-embed-el<\/artifactId>/{
                s/<exclusion>/<!-- <exclusion>/;
                s/<\/exclusion>/<\/exclusion> -->/;
            }
        }
    }
}' "$POM_FILE"


# Optional: Remove the backup file created by sed (pom.xml.bak)
rm -f "${POM_FILE}.bak"

echo "Exclusions for spring-boot-starter-tomcat and tomcat-embed-el successfully commented out in $POM_FILE"

