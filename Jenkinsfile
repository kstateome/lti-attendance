pipeline {
    environment {
        JENKINS_AVATAR_URL = "https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png"
    }
    agent any
    tools {
        maven "Maven 3.3.9"
        jdk "Java 8"
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
            post {
                failure {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance did *not compile* on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                changed {
                    script {
                        if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
                            rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance is now *compiling* on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                        }
                    }
                }
            }
        }
        
        stage('sonar') {
            when {
                branch 'master'
            }
            steps {
                sh "mvn sonar:sonar -P sonar -Dsonar.branch=${env.branch_name}"
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                failure {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance had unit test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                unstable {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance had unit test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                changed {
                    script {
                        if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
                            rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance now has passing unit tests on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                        }
                    }
                }
            }
        }

        stage('Maven Site') {
            when { branch 'master' }
            steps {
                sh 'mvn site-deploy'
            }
            post {
                success {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", channel: 'javabuilds', message: "Successfully generated Maven site documentation for Attendance: https://jenkins.ome.ksu.edu/maven-site/lti-attendance/", rawMessage: true
                }
            }
        }

        stage('Test Deploy') {
            when {
                expression { return env.BRANCH_NAME ==~ /test(\d+)/ }
            }
            steps {
                sh "mvn -X wildfly:undeploy -P lti-${env.BRANCH_NAME}"
                sh "mvn -X wildfly:execute-commands -P lti-${env.BRANCH_NAME}"
                sh "sleep 45"
                sh "mvn -X wildfly:deploy -P lti-${env.BRANCH_NAME} -DskipTests"
            }
            post {
                success {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", channel: 'javajavajava', message: "Successfully deployed Attendance to ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                failure {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", channel: 'javajavajava', message: "Failed to deploy Attendance to ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
            }
        }

        stage('Integration Test') {
            steps {
                sh 'mvn verify -Pintegration '
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
                changed {
                    script {
                        if (currentBuild.currentResult == 'SUCCESS') {
                            if (currentBuild.previousBuild == null || currentBuild.previousBuild.result != 'SUCCESS') {
                                rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance now has passing Integration tests ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                            }
                        }
                    }
                }
                failure {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance had Integration test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                unstable {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance had Integration test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
            }
        }

        stage("Arquillian Tests") {
            steps {
                sh 'mvn verify -Parquillian -Djboss.port.offset=100 -Djboss.port.management=10090'
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
                changed {
                    script {
                        if (currentBuild.currentResult == 'SUCCESS') {
                            if (currentBuild.previousBuild == null || currentBuild.previousBuild.result != 'SUCCESS') {
                                rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance now has passing Arquillian tests ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                            }
                        }
                    }
                }
                failure {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance had Arquillian test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                unstable {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", message: "Attendance had Arquillian test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
    }
}

@NonCPS
def getChangeString(maxMessages) {
    MAX_MSG_LEN = 100
    COMMIT_HASH_DISPLAY_LEN = 7
    def changeString = ""

    def changeLogSets = currentBuild.changeSets


    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length && i + j < maxMessages; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            commitHash = entry.commitId.take(COMMIT_HASH_DISPLAY_LEN)
            changeString += "${commitHash}... - ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString) {
        changeString = " There have not been changes since the last build"
    }
    return changeString
}
