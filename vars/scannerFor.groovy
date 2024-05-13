# Checkout the Repository
def checkoutCode() {
    checkout scm
}

# Git Repositorey Scanning using Trivy
def repo(String repo) {
  sh 'trivy repository --exit-code 0 --no-progress --severity HIGH,CRITICAL --scanners vuln ${repo} | tee -a trivyrepo.txt'
}

# Filesystem Scanner using Trivy
def fs() {
  sh "trivy fs --scanners vuln . | tee -a trivyfs.txt"
}

# Static Code Analysis with SonarQube
def sast(String envSonarName, String projectName, String projectKey) {

  withSonarQubeEnv(${envSonarName}) {
    sh "/opt/sonar-scanner/bin/sonar-scanner -Dsonar.projectName=${projectName} -Dsonar.projectKey=${projectKey}"
  }
}

# OWASP Dependency-Check Vulnerabilities
def owasp() { 
  dependencyCheck additionalArguments: ''' 
      -o './'
      -s './'
      -f 'ALL' 
      --prettyPrint
  ''', odcInstallation: 'DP_Check'
                
  dependencyCheckPublisher pattern: 'dependency-check-report.xml'
}

# Install NPM Dependencies
def installDeps(String fromDir) {
  sh '''
    cd ${fromDir}
    npm install
  '''
}

# Build Docker Image
def docker_build(String composeProjectName) {
  sh "docker compose -p ${composeProjectName} build --force-rm --no-cache"
}

# Docker Image Scanning using Trivy
def image(String image_name) {
  sh "trivy image --exit-code 0  --severity HIGH,CRITICAL --scanners vuln ${image_name} | tee -a trivyimage.txt"  
}

# Docker Tag Image & Push
def tagPush(String credentialsId, String toolName, String image_name, dockerhub_image_tag){
  withDockerRegistry(credentialsId: ${credentialsId}, toolName: ${toolName}){   
      sh "docker tag ${image_name} ${dockerhub_image_tag}"
      sh 'echo "salafiyAAA" | docker login -u mbaynd --password-stdin'
      sh "docker push ${dockerhub_image_tag}
  }
}

# Deploy to container to Staging
def deployBuild(String image_name) {
  sh "docker rm -f ${image_name} && docker compose -p taskmanager up ${image_name} -d"
}








