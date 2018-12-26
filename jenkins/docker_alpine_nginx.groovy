#!/usr/bin/groovy
/*
This jenkins file will build an image using Alpine linux and Nginx as the web server, 
Make sure to add the jenkins user to the dockers group 
sudo usermod -aG docker jenkins 
sudo service jenkins restart 
Its very important to restart otherwise the service user will not be able to run docker commands
*/
pipeline {
	agent any
	environment{
		DOCKER_IMAGE = 'kasc/angular-nginx:latest'
	}
	stages {
		stage('delete previous workspace'){
			steps{
				echo "delete previous workspace"
				dir('/var/lib/jenkins/workspace/Docker_alpine_nginx/dockerFiles') {
					deleteDir()
				}
			}
		}
		stage('checkout') {
			steps {
			    echo "checkout docker files for alpine-nginx"
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: '/dockerFiles/'], [path: '']]]], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/cshashank/jenkins-k8s/']]])
			}
		}
		stage('coppy dist jar from angular build') {
			steps {
			    echo "coppy dist jar from angular build"
				dir('/var/lib/jenkins/workspace/Docker_alpine_nginx/dockerFiles/files') {
					sh "cp /var/lib/jenkins/workspace/skc-nginx-pipeline/k8s-app/html.tar.gz ."
				}
			}
		}
		stage('build docker image') {
			steps {
			    echo "build docker image"
				dir('/var/lib/jenkins/workspace/Docker_alpine_nginx/dockerFiles') {
					sh "docker build -t "+${DOCKER_IMAGE}+" -f nginxDockerFile ."
				}
			}
		}
		stage('push docker image to repository') {
			steps {
			    echo "push docker image to repository"
				dir('/var/lib/jenkins/bin') {
					sh "./docker_login.sh"
					sh " docker push kasc/angular-nginx:latest"
				}
			}
		}
		stage('Start angular-container for testing on IST port http://35.230.144.184:8085') {
			steps {
			    echo "start container"
				script{
					try{
						sh "docker stop j-nginx-app"
					}catch(Exception e){
						echo 'exception while stopping container'
					}
				}
				dir('/var/lib/jenkins/workspace/Docker_alpine_nginx/dockerFiles') {
					sh "docker run -d --rm --name j-nginx-app -p 8085:80 kasc/angular-nginx:latest"
				}
			}
		}
        stage('Deploy to UAT ?'){
            steps{
                input "Deploy to UAT ?"
            }
        }		

	}
}
