properties([
        pipelineTriggers([
                [$class: "GitHubPushTrigger"]
        ])
])

pipeline {
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
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', message: "Attendance did *not compile* on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                changed {
                    script {
                        if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
                            rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', message: "Attendance is now *compiling* on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
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
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                failure {
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', message: "Attendance had unit test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                unstable {
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/}ce7853c9/images/headshot.png', message: "Attendance had unit test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                changed {
                    script {
                        if (currentBuild.result == null || currentBuild.result == 'SUCCESS') {
                            rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', message: "Attendance now has passing unit tests on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                        }
                    }
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
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', channel: 'javajavajava', message: "Successfully deployed Attendance to ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                failure {
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', channel: 'javajavajava', message: "Failed to deploy Attendance to ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
            }
        }

        stage('Integration Test') {
            steps {
                sh 'mvn verify -Pintegration '
            }
        }

        stage("Arquillian Tests") {
            steps {
                sh 'mvn verify -Parquillian -Djboss.port.offset=100 -Djboss.port.management=10090'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                changed {
                    script {
                        if (currentBuild.currentResult == 'SUCCESS') {
                            if (currentBuild.previousBuild == null || currentBuild.previousBuild.result != 'SUCCESS') {
                                rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', message: "Attendance now has passing Arquillian tests ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                            }
                        }
                    }
                }
                failure {
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png', message: "Attendance had Arquillian test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
                unstable {
                    rocketSend avatar: 'https://jenkins.ome.ksu.edu/static/}ce7853c9/images/headshot.png', message: "Attendance had Arquillian test failures on branch ${env.BRANCH_NAME} \nRecent Changes - ${getChangeString(10)}\nBuild: ${BUILD_URL}", rawMessage: true
                }
            }
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
