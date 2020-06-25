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
        DOCKER_CONFIG_GITHUB = 'https://github.com/Timbergrove/ophanim-2-docker-config'
        DOCKER_IMAGE = 'timbergrove/ophanim2_angular_fe:latest'
        DOCKER_CONTAINER = 'j_nginx_app'
		ANGULAR_BUILD_TAR = '/var/lib/jenkins/workspace/d_ophanim2_fe/html.tar.gz'
		DOCKER_ALPINE_NGINX_ANGULAR_WS = '/var/lib/jenkins/workspace/tg_docker_alpine_nginx_angular/'
		DOCKER_CONFIG_LOCAL = '/home/ubuntu/ophanim2_docker/ophanim-2-docker-config'
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
                // checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: '/dockerFiles/']]]], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/Timbergrove/ophanim-2-docker-config']]])
			    dir(DOCKER_CONFIG_LOCAL){
					git(
							url: DOCKER_CONFIG_GITHUB,
							credentialsId: 'aeb4de6e-4a73-4673-a7e2-b4f61b93dfe8',
							branch: "master"
					)
				}
			}
			
		}
		stage('copy dist jar from angular build') {
			steps {
			    echo "copy dist jar from angular build"
				dir(DOCKER_CONFIG_LOCAL+'/dockerFiles/files') {
					sh "cp "+ANGULAR_BUILD_TAR+" ."
				}
			}
		}
		stage('build docker image') {
			steps {
			    echo "build docker image"
				echo "docker build -t "+DOCKER_IMAGE+" -f nginxDockerFile ."
				dir(DOCKER_CONFIG_LOCAL+'/dockerFiles') {
					sh "docker build -t "+DOCKER_IMAGE+" -f nginxDockerFile ."
				}
			}
		}
		stage('push docker image to repository') {
			steps {
				echo "push docker image to repository"
				dir('/var/lib/jenkins/bin') {
				    withCredentials([usernamePassword(credentialsId: 'ca2f1ead-36a6-4e82-a00a-6a281baeaffb', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                        sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
					    sh "docker push timbergrove/ophanim2_angular_fe:latest"
				    }
				}
			}
		}
	}
}
