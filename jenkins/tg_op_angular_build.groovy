#!/usr/bin/groovy
pipeline {
	agent any
	environment{
        GIT_REPO = 'https://github.com/Timbergrove/ophanim-2-angular-app/'
		DOCKER_IMAGE = 'kasc/tg_angular_nginx:latest'
        ANGULAR_APP_HOME = '/var/lib/jenkins/workspace/d_ophanim2_fe'
		ANGULAR_BUILD_TAR = '/var/lib/jenkins/workspace/d_ophanim2_fe/ophanim-ui/html.tar.gz'
		DOCKER_ALPINE_NGINX_ANGULAR_WS = '/var/lib/jenkins/workspace/docker_alpine_nginx_angular/'
	}

	stages {
		stage('delete previous workspace'){
			steps{
				echo "delete previous workspace"
				dir(ANGULAR_APP_HOME) {
					deleteDir()
				}
			}
		}
		stage('checkout') {
			steps {
			    echo "checkout"
			    git(
                        url: GIT_REPO,
                        credentialsId: 'tg_jenkin_uid_pwd',
                        branch: "qa"
                )
			}
		}
    	stage('install angular-devkit') {
			steps {
			    echo "install angular-devkit"
				dir(ANGULAR_APP_HOME) {
			        sh "npm install --dev-save @angular-devkit/build-angular"
				}
			}
		}	
		stage('install angular/core, platform-browser-dynamic and tslib') {
			steps {
				echo 'install angular/core, platform-browser-dynamic and tslib'
				dir(ANGULAR_APP_HOME) {
			        sh "npm install --dev-save @angular/core"
                    sh "npm i @angular/platform-browser-dynamic"
                    sh "npm i tslib"
				}
			}
		}	
		stage('build angular app') {
			steps {
				echo 'Building..'
				dir(ANGULAR_APP_HOME) {
	    	        sh "ng build"
				}
			}
		}
		stage('tar build for docker') {
			steps {
				echo 'Tar build for docker'
				dir(ANGULAR_APP_HOME) {
					sh "tar -zcvf html.tar.gz -C dist/ophanim-ui ."
				}
			}
		}
    }
}