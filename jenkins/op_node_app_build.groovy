#!/usr/bin/groovy
pipeline {
	agent any

	stages {

		stage('checkout') {
			steps {
			    echo "checkout"
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'tg_jenkin_uid_pwd', url: 'https://github.com/Timbergrove/ophanim-2-app.git']]])

			}
		}
	}
}
