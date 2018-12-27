#!/usr/bin/groovy
/*
This jenkins file will build an image using Alpine linux and Nginx as the web server, 
Make sure to add the jenkins user to the dockers group 
sudo usermod -aG docker jenkins 
sudo service jenkins restart 
Its very important to restart otherwise the service user will not be able to run docker commands
************************************************************************** 
* This pipeline depends on the angular_build pipeline to have run before *
**************************************************************************
*/
pipeline {
	agent any
	environment{
		DOCKER_IMAGE = 'kasc/angular-nginx:latest'
		ANGULAR_BUILD_TAR = '/var/lib/jenkins/workspace/angular_build/k8s-app/html.tar.gz'
		DOCKER_ALPINE_NGINX_ANGULAR_WS = '/var/lib/jenkins/workspace/docker_alpine_nginx_angular/'
	}
	stages {
		stage('delete previous workspace'){
			steps{
				echo "delete previous workspace"
				dir(DOCKER_ALPINE_NGINX_ANGULAR_WS+'dockerFiles') {
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
		stage('copy dist jar from angular build') {
			steps {
			    echo "copy dist jar from angular build"
				dir(DOCKER_ALPINE_NGINX_ANGULAR_WS+'dockerFiles/files') {
					sh "cp "+ANGULAR_BUILD_TAR+" ."
				}
			}
		}
		stage('build docker image') {
			steps {
			    echo "build docker image"
				echo "docker build -t "+DOCKER_IMAGE+" -f nginxDockerFile ."
				dir(DOCKER_ALPINE_NGINX_ANGULAR_WS+'dockerFiles') {
					sh "docker build -t "+DOCKER_IMAGE+" -f nginxDockerFile ."
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
				dir(DOCKER_ALPINE_NGINX_ANGULAR_WS+'dockerFiles') {
					sh "docker run -d --rm --name j-nginx-app -p 8085:80 kasc/angular-nginx:latest"
				}
			}
		}

	}
}
