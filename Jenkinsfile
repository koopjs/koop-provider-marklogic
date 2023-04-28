@Library('shared-libraries') _
pipeline{
  agent {label 'devExpLinuxPool'}
  options {
    checkoutToSubdirectory 'marklogic-koop-provider'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '')
  }
  environment{
    JAVA_HOME_DIR="/home/builder/java/jdk-11.0.2"
    NODE_HOME_DIR="/home/builder/nodeJs/node-v14.15.4-linux-x64"
    GRADLE_DIR   =".gradle"
    DMC_USER     = credentials('MLBUILD_USER')
    DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
  }
  stages{
    stage('tests'){
      steps{
        copyRPM 'Latest','11'
        setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
        gitCheckout 'marklogic-geo-data-services','https://github.com/marklogic/marklogic-geo-data-services.git','develop';
        sh label:'test', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$NODE_HOME_DIR/bin:$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd $WORKSPACE/marklogic-geo-data-services
          ./gradlew -i mlDeploy loadTestData -PmlUsername=admin -PmlPassword=admin || true
          cd $WORKSPACE/marklogic-koop-provider
          npm install
          cd test
          ./gradlew runKoopServers test || true
        '''
        junit '**/build/**/*.xml'
      }
    }
  }
}
