#!/usr/bin/groovy
pipeline {
	agent any
	environment{
		GIT_REPO = 'https://github.com/Timbergrove/ophanim-2-app/'
		DOCKER_IMAGE = 'kasc/tg_angular_nginx:latest'
		NODE_APP_HOME = '/var/lib/jenkins/workspace/tg_op_node_app_build'
		NODE_APP_DIST = '/var/lib/jenkins/workspace/tg_op_node_app_build/dist'
		NODE_APP_TAR = '/var/lib/jenkins/workspace/tg_op_node_app_build/html.tar.gz'
		DOCKER_ALPINE_NGINX_ANGULAR_WS = '/var/lib/jenkins/workspace/docker_alpine_nginx_angular/'
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
		stage('checkout') {
			steps {
				echo "checkout in dist"
				// checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'tg_jenkin_uid_pwd', url: 'https://github.com/Timbergrove/ophanim-2-app.git']]])
				dir(NODE_APP_DIST){
					git(
							url: GIT_REPO,
							credentialsId: 'tg_jenkin_uid_pwd',
							branch: "master"
					)
				}
			}
		}
		stage('Build code') {
			steps {
				echo "checkout in dist"
				// checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'tg_jenkin_uid_pwd', url: 'https://github.com/Timbergrove/ophanim-2-app.git']]])
				dir(NODE_APP_DIST){
					sh "npm install"
				}
			}
		}
		stage('tar build') {
			steps {
				echo "checkout in dist"
				// checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'tg_jenkin_uid_pwd', url: 'https://github.com/Timbergrove/ophanim-2-app.git']]])
				dir(NODE_APP_HOME){
					sh "tar -zcvf op_node_app.tar.gz -C dist ."
				}
			}
		}
	}
}
