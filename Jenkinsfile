pipeline {
    agent any
    environment {
        // Default to the current branch in order to always deploy test branches
        testDeploy = "${BRANCH_NAME}"

        // Regex expressions to use in order to ignore commits. If the latest commit matches any of these then the build is skipped
        regexIgnore = ".*maven-release-plugin.*;.*skipJenkins.*"

        // Configure for the correct test environments. Option 'none' is added in the stage 'prompt-for-test-deploy'
        testBranches = "test1\ntest2\ntest3"

        // Regex for branches which Jenkins should prompt for test deployment
        promptTestBranchRegex = "merge-.*"

        // Channels for rocket chat notifications
        testDeployPromptChannel = "javajavajava"
        releaseConfirmChannel = "javajavajava"
        buildFailureNotificationChannel = "javabuilds"
        releaseBuiltNotificationChannel = "javajavajava"

        // Avatar to use for Rocket Chat messages
        JENKINS_AVATAR_URL = "https://jenkins.ome.ksu.edu/static/ce7853c9/images/headshot.png"
    }
    tools {
        maven "Maven 3.5"
        jdk "Java 8"
    }

    stages {
        stage('Build') {
            steps {
                script {
                    if (shouldIgnoreCommit(env.regexIgnore.split(';'))) {
                        error "Ignoring commit"
                    }
                }
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
                always {
                    script {
                        if (shouldIgnoreCommit(env.regexIgnore.split(';'))) {
                            currentBuild.result = 'NOT_BUILT'
                        }
                    }
                }
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

        stage('Test Deploy') {
            when {
                expression { return env.testDeploy ==~ /test(\d+)/ }
            }
            steps {
                sh "mvn -X wildfly:undeploy -P lti-${env.BRANCH_NAME}"
                sh "mvn -X wildfly:execute-commands -P lti-${env.BRANCH_NAME}"
                sh "sleep 45"
                sh "mvn -X clean package wildfly:deploy -P lti-${env.BRANCH_NAME} -DskipTests"
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

        stage('Sonar') {
            when {
                branch 'master'
            }
            steps {
                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install'
                sh "mvn sonar:sonar -P sonar -Dsonar.branch=${env.branch_name}"
            }
        }

        stage('Maven Site') {
            when { branch 'maven-site' }
            steps {
                sh 'mvn site-deploy'
            }
            post {
                success {
                    rocketSend avatar: "$JENKINS_AVATAR_URL", channel: 'javabuilds', message: "Successfully generated Maven site documentation for Attendance: https://jenkins.ome.ksu.edu/maven-site/lti-attendance/", rawMessage: true
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

        stage('release-dry-run') {
            agent any
            when {
                branch 'release'
            }
            steps {
                sh 'mvn --batch-mode -DdryRun=true release:clean release:prepare release:perform'
                // This must be run in an agent in order to resolve the version. There is probably a better alternative that we could use in the future
                rocketSend avatar: "${env.JENKINS_AVATAR_URL}", channel: "${env.releaseConfirmChannel}", message: "Release Dry Run of ${JOB_NAME} ${version()} finished. Continue Release? - ${BUILD_URL}console", rawMessage: true
            }
        }

        stage('confirm-release') {
            agent none
            when {
                branch 'release'
            }
            steps {
                input "Click continue to release ${JOB_NAME}"
            }
        }

        stage('release') {
            agent any
            when {
                branch 'release'
            }
            steps {
                sh 'mvn --batch-mode release:clean release:prepare release:perform'
            }

            post {
                success {
                    rocketSend avatar: "${env.JENKINS_AVATAR_URL}", channel: "${env.releaseBuiltNotificationChannel}", message: "Successfully built release  ${version()}\n Build: ${BUILD_URL}", rawMessage: true
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
def version() {
    pom = readMavenPom file: 'pom.xml'
    pom.version
}

def shouldIgnoreCommit(regexIgnoreList) {
    def lastCommit = sh (script: 'git log --pretty=oneline | head -n 1', returnStdout: true)
    // For loop is used because [].each is not serializable
    for (int i = 0; i < regexIgnoreList.size(); i++) {
        if (lastCommit =~ /${regexIgnoreList[i]}/) {
            return true
        }
    }
    return false
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
