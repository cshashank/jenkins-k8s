#!/usr/bin/groovy
/*
This jenkins file will build an image using Alpine linux and Nginx as the web server, 
Make sure to add the jenkins user to the dockers group 
sudo usermod -aG docker jenkins 
sudo service jenkins restart 
Its very important to restart otherwise the service user will not be able to run docker commands
*/
pipeline {
	agent any
	environment{
		DOCKER_IMAGE = 'kasc/angular-nginx:latest'
		CONTAINER_NAME = 'j-nginx-app'
		CONTAINER_PORT = '8085'
		MODE = 'debug'
		K8S_DEPLOYMENT_NAME = 'angular-nginx-ro-deployment'
		K8S_SERVICE_LOADBALANCER_NAME = 'angular-nginx-lb'
		K8S_UAT_WS = '/var/lib/jenkins/workspace/k8s_uat/'
	}
	stages {
        stage('Deploy to UAT ?'){
            steps{
                input "Deploy to UAT ?"
            }
        }		
		stage('checkout') {	
			steps {
			    echo "checkout docker files for alpine-nginx"
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: '/dockerFiles'], [path: '/deployments']]]], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/cshashank/jenkins-k8s/']]])
			}
		}
        stage('Deploy Kubernetes cluster'){
            steps{
			    echo "start container"
				script{
					try{
						sh "kubectl delete deployment "+K8S_DEPLOYMENT_NAME
					}catch(Exception e){
						echo 'exception while deleting a k8s deployment '
					}
				}
				dir(K8S_UAT_WS+'deployments') {
					sh "kubectl create -f "+K8S_DEPLOYMENT_NAME+".yaml"
				}
            }
        }		
        stage('Expose deployment'){
            steps{
			    echo "Expose deployment"
				script{
					try{
						sh "kubectl delete service  "+K8S_SERVICE_LOADBALANCER_NAME
					}catch(Exception e){
						echo 'exception while deleting a k8s deployment '
					}
				}
				sh "kubectl expose deployment "+K8S_DEPLOYMENT_NAME+" --type=\"LoadBalancer\" --name=\""+K8S_SERVICE_LOADBALANCER_NAME+"\""
            }
        }		

	}
}
