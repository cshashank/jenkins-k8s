#!/usr/bin/groovy
pipeline {
	agent any

	stages {

		stage('delete previous workspace'){
			steps{
				echo "delete previous workspace"
				dir('/var/lib/jenkins/workspace/skc-nginx-pipeline/k8s-app') {
					deleteDir()
				}
			}
		}
		stage('checkout') {
			steps {
			    echo "checkout"
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/cshashank/angular-k8s']]])
			}
		}
		stage('install angular-devkit') {
			steps {
			    echo "install angular-devkit"
				dir('/var/lib/jenkins/workspace/skc-nginx-pipeline/k8s-app') {
			        sh "npm install --dev-save @angular-devkit/build-angular"
				}
			}
		}	
		stage('install angular/core') {
			steps {
				echo 'install angular/core'
				dir('/var/lib/jenkins/workspace/skc-nginx-pipeline/k8s-app') {
			        sh "npm install --dev-save @angular/core"
				}
			}
		}	
		stage('build angular app') {
			steps {
				echo 'Building..'
				dir('/var/lib/jenkins/workspace/skc-nginx-pipeline/k8s-app') {
	    	        sh "ng build"
					sh "tar -zcvf dist.tar.gz dist"
				}
			}
		}

		stage('Test') {
			steps {
				echo 'Testing..'
			}
		}

		stage('Deploy') {
			steps {
				echo 'Deploying....'
			}
		}

	}
}
