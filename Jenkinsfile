pipeline {
    agent any

    tools {
        maven 'Maven 3.8.8'
    }

    environment {
        DOCKER_CREDENTIALS_ID = 'docker-hub' 
        DOCKER_IMAGE = 'cz2edtee34/my-library-api'
        DOCKER_TAG = 'latest'
        SONARQUBE_ENV = 'SonarQube'
        REMOTE_SERVER = 'ubuntu@localhost'
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

        stage('Code Quality Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                    mvn sonar:sonar \
                        -Dsonar.projectKey=LibraryApi \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.login=xalo \
                        -Dsonar.password=123123
                    '''
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
                    if (!fileExists('target/devopprj-0.0.1-SNAPSHOT.war')) {
                        error "WAR file not found. Build failed."
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    """
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        sh """
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        """
                    }
                }
            }
        }

        stage('Deploy to Remote Server') {
            steps {
                sshagent(['remote-server-ssh']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no $REMOTE_SERVER "
                        sudo -i &&
                        sudo docker stop \$(docker ps -q --filter ancestor=${DOCKER_IMAGE}:${DOCKER_TAG}) || true &&
                        sudo docker rm \$(docker ps -q --filter ancestor=${DOCKER_IMAGE}:${DOCKER_TAG}) || true &&
                        sudo docker pull ${DOCKER_IMAGE}:${DOCKER_TAG} &&
                        sudo docker run -d -p 8080:8080 ${DOCKER_IMAGE}:${DOCKER_TAG}
                    "
                    """
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}