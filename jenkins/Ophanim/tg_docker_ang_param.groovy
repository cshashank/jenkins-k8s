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
				echo "delete previous workspace"$docker_tag" & "$branch
				dir(DOCKER_ALPINE_NGINX_ANGULAR_WS+'dockerFiles') {
					deleteDir()
				}
			}
		}
	}
}
