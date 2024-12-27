pipeline {
    agent any

    tools {
        maven 'MyMaven'
    }

    environment {
        DOCKER_CREDENTIALS_ID = 'docker-auth-token'
        DOCKER_HUB_REGISTRY = ''
        DOCKER_REPO = 'x4lo/my-library-api'
        DOCKER_TAG = 'latest'

        REMOTE_SERVER = 'ubuntu@localhost'
        // REMOTE_SERVER_SSH = credentials('remote-server-ssh')

        // SONARQUBE_TOKEN = credentials('SonarQube-auth-token')
        SONARQUBE_ENV_NAME   = 'SonarQubeServer'
        // SONARQUBE_URL        = 'http://localhost:9000'
        SONARQUBE_PROJECT_KEY = 'X4Lo_CICD_jenkins_project'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_ENV_NAME) {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=${SONARQUBE_PROJECT_KEY}'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Verify WAR') {
            steps {
                script {
                    if (!fileExists('target/DevopPrj-0.0.1-SNAPSHOT.war')) {
                        error 'WAR file not found. Build failed.'
                    }
                }
            }
        }

        stage('Building Docker Image') {
            steps {
                script {
                    appImage = docker.build("${DOCKER_REPO}:${DOCKER_TAG}", ".")
                }
            }
        }

        stage('Pushing Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry(DOCKER_HUB_REGISTRY, DOCKER_CREDENTIALS_ID) {
                        appImage.push()
                    }
                }
            }
        }

        // stage('Deploy to Remote Server') {
        //     steps {
        //         sshagent([REMOTE_SERVER_SSH]) {
        //             sh """
        //             ssh -o StrictHostKeyChecking=no $REMOTE_SERVER "
        //                 sudo -i &&
        //                 sudo docker stop \$(docker ps -q --filter ancestor=${DOCKER_REPO}:${DOCKER_TAG}) || true &&
        //                 sudo docker rm \$(docker ps -q --filter ancestor=${DOCKER_REPO}:${DOCKER_TAG}) || true &&
        //                 sudo docker pull ${DOCKER_REPO}:${DOCKER_TAG} &&
        //                 sudo docker run -d -p 8080:8080 ${DOCKER_REPO}:${DOCKER_TAG}
        //             "
        //             """
        //         }
        //     }
        // }
    }

    post {
        // always {
        //     junit '**/target/surefire-reports/*.xml'
        // }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}
