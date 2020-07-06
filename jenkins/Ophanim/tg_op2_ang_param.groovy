#!/usr/bin/groovy
pipeline {
	agent any
	environment{
        GIT_REPO = 'https://github.com/Timbergrove/ophanim-2-angular-app/'
		DOCKER_IMAGE = 'kasc/tg_angular_nginx:latest'
        ANGULAR_APP_HOME = '/var/lib/jenkins/workspace/d_ophanim2_fe'
		ANGULAR_APP_DIST = '/var/lib/jenkins/workspace/d_ophanim2_fe/dist'
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
		stage('checkout angular files from github') {
			steps {
			    echo "checkout ${git_branch}"
			    dir(ANGULAR_APP_HOME){
			    	git(
						url: GIT_REPO,
						credentialsId: 'aeb4de6e-4a73-4673-a7e2-b4f61b93dfe8',
						branch: "${git_branch}"
					)
			    }
			}
		}
		stage('npm install') {
			steps {
				echo 'install angular/core, platform-browser-dynamic and tslib'
				dir(ANGULAR_APP_HOME) {
					sh "npm install"
				}
			}
		}	
		stage('build angular app') {
			steps {
				echo 'Building..'
				dir(ANGULAR_APP_HOME) {
	    	        sh "npm run build:docker"
				}
			}
		}
		stage('tar build for docker') {
			steps {
				echo 'Tar build for docker'
				dir(ANGULAR_APP_HOME) {
					sh "tar -zcvf html.tar.gz -C dist/ophanim-ui/ ."
				}
			}
		}
    }
}