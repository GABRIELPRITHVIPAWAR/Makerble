pipeline {
    agent any
    environment {
        registry = "gabrielprithivipawar/app"
        DOCKER_REGISTRY_CREDS = 'dock'
    }
   
    stages {
        stage('Cloning Git') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'git', url: 'https://github.com/GABRIELPRITHVIPAWAR/multicontainer-app-K8S.git']]])     
            }
        }
          
        stage('Building image') {
            steps {
                script {
                    dockerImage = docker.build registry 
                }
            }
        }
   
        stage('Deploy to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKER_REGISTRY_CREDS}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin docker.io"
                    sh "docker push ${registry}"
                }
            }
        }

        stage('K8S Deploy') {
            steps {
                sh 'kubectl apply -f k8s'
            }
        }
    }
}
