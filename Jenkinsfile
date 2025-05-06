pipeline {
    agent any
    environment {
      JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
      PATH      = "${env.JAVA_HOME}/bin:/usr/share/maven/bin:${env.PATH}"
    }

    stages {
        stage('Build & Install') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }
        stage('PMD') {
            steps {
                sh 'mvn pmd:cpd pmd:pmd'
            }
        }
        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        stage('Javadoc') {
            steps {
                sh 'mvn javadoc:javadoc'
            }
        }
        stage('Site') {
            steps {
                sh 'mvn site site:stage -DskipTests'
            }
        }
    }
     post {
        always {
            archiveArtifacts artifacts: '**/target/staging/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
            junit '**/target/surefire-reports/*.xml'
        }
     }
}
