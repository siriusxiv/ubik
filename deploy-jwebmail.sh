export ANT_ARGS; ANT_ARGS='-lib bootstrap-libs -noclasspath'
/opt/eclipse/plugins/org.apache.ant_1.8.4.v201303080030/bin/ant -buildfile build-dist.xml 
sudo rm /opt/jboss-as-7.1.1.Final/standalone/deployments/jwebmail.war
sudo cp dist/jwebmail.war /opt/jboss-as-7.1.1.Final/standalone/deployments/
/opt/jboss-as-7.1.1.Final/bin/standalone.sh
