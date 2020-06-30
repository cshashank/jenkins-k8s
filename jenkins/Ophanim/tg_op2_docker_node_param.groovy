#!/usr/bin/groovy
pipeline {
	agent any
	environment{
		GIT_REPO = 'https://github.com/Timbergrove/ophanim-2-app/'
		DOCKER_IMAGE = "timbergrove/ophanim2_node_app:${git_branch}"
		NODE_APP_HOME = '/var/lib/jenkins/workspace/tg_op_node_app_build'
		NODE_APP_DIST = '/var/lib/jenkins/workspace/tg_op_node_app_build/dist'
		DOCKER_ALPINE_NGINX_ANGULAR_WS = '/var/lib/jenkins/workspace/tg_docker_alpine_nginx_angular/'
		DOCKER_CONFIG_LOCAL = '/home/ubuntu/ophanim2_docker/ophanim-2-docker-config'
		DOCKER_CONFIG_GITHUB = 'https://github.com/Timbergrove/ophanim-2-docker-config'
		//${git_branch} parameter passed from the pipeline
		GIT_BRANCH="${git_branch}"
		TAR_FILE = "op_node_app.tar.gz"
	}

	stages {
		stage('delete previous workspace'){
			steps{
				echo "delete previous workspace"
				dir(NODE_APP_HOME) {
					deleteDir()
				}
			}
		}
		stage('checkout dockerfiles from github') {
			steps {
			    echo "checkout docker files from github"
			    dir(DOCKER_CONFIG_LOCAL){
					git(
							url: DOCKER_CONFIG_GITHUB,
							credentialsId: 'aeb4de6e-4a73-4673-a7e2-b4f61b93dfe8',
							branch: "master"
					)
				}
			}
			
		}
		stage('checkout node build from github') {
			steps {
				echo "checkout in dist from "+GIT_BRANCH
				dir(NODE_APP_DIST){
					git(
							url: GIT_REPO,
							credentialsId: 'aeb4de6e-4a73-4673-a7e2-b4f61b93dfe8',
							branch: GIT_BRANCH
					)
				}
			}
		}
		stage('Build code') {
			steps {
				echo "Build code for node app"
				dir(NODE_APP_DIST){
					sh "npm install"
				}
			}
		}
		stage('tar build') {
			steps {
				echo "Buuild  tar file from the build"
				dir(NODE_APP_HOME){
					sh "tar -zcvf "+TAR_FILE+" -C dist ."
				}
			}
		}
		stage('copy dist tar from node build') {
			steps {
			    echo "copy dist tar from node build"
				dir(NODE_APP_HOME+'/dockerFiles') {
				    sh "cp "+NODE_APP_HOME+"/"+TAR_FILE+" ."
				}
			}
		}
		stage('copy dockerfile from Docker config local') {
			steps {
			    echo "copy dist tar from node build"
				dir(NODE_APP_HOME+'/dockerFiles') {
					sh "cp "+DOCKER_CONFIG_LOCAL+"/dockerFiles/opNodeDockerFile ."
				}
			}
		}
		stage('build docker image') {
			steps {
				echo "build docker image"
				echo "docker build -t "+DOCKER_IMAGE+" -f opNodeDockerFile ."
				dir(NODE_APP_HOME+'/dockerFiles'){
					sh "docker build -t "+DOCKER_IMAGE+" -f opNodeDockerFile ."
				}
			}
		}
		stage('push docker image to repository') {
			steps {
				echo "push docker image to repository"
				dir('/var/lib/jenkins/bin') {
					// sh "./docker_login.sh"
				    withCredentials([usernamePassword(credentialsId: 'ca2f1ead-36a6-4e82-a00a-6a281baeaffb', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
						sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
						sh "docker rmi -f $(docker images -f 'dangling=true' -q)"
						sh "docker push "+DOCKER_IMAGE
					}
				}
			}
		}	
	}
}
