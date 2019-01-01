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
This script also depends on a script to be placed in this directory /var/lib/jenkins/bin
the script is docker_login.sh
docker login --username foo --password bar
*/
pipeline {
	agent any
	environment{
        JENKINS_GIT_REPO = 'https://github.com/Timbergrove/ophanim-2-angular-app/'
		DOCKER_IMAGE = 'kasc/tg_angular_nginx:latest'
        DOCKER_CONTAINER = 'j_nginx_app'
		ANGULAR_BUILD_TAR = '/var/lib/jenkins/workspace/d_ophanim2_fe/html.tar.gz'
		DOCKER_ALPINE_NGINX_ANGULAR_WS = '/var/lib/jenkins/workspace/tg_docker_alpine_nginx_angular/'
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
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: '/dockerFiles/']]]], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/cshashank/jenkins-k8s']]])
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
		stage('Start angular-container for testing on IST port http://ec2-18-212-212-169.compute-1.amazonaws.com:8085') {
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
					sh "docker run -d --rm --name "+DOCKER_CONTAINER+" -p 8085:80 "+DOCKER_IMAGE
				}			}
		}

	}
}
