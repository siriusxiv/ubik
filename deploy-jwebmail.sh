export ANT_ARGS; ANT_ARGS='-lib bootstrap-libs -noclasspath'
~/Downloads/eclipse/plugins/org.apache.ant_1.8.4.v201303080030/bin/ant -buildfile build-dist.xml 
cp ./dist/jwebmail.war ~/Downloads/jboss-as-7.1.1.Final/standalone/deployments/
~/Downloads/jboss-as-7.1.1.Final/bin/standalone.sh
