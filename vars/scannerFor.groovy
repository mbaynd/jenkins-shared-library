// Checkout the Repository
def checkoutCode(String repo, branch) {
    //sh "git clone ${repo} ${projectName} && cd ${projectName}"
    git branch: ${main}, url: ${repo}
}

// Git Repositorey Scanning using Trivy
def repo(String repo) {
  sh "trivy repository --exit-code 0 --no-progress --severity HIGH,CRITICAL --scanners vuln ${repo} | tee -a trivyrepo.txt"
}

// Filesystem Scanner using Trivy
def fs() {
  sh "trivy fs --scanners vuln . | tee -a trivyfs.txt"
}

// Static Code Analysis with SonarQube
def sast(String projectName) {

  withSonarQubeEnv('sonar') {
    sh "/opt/sonar-scanner/bin/sonar-scanner -Dsonar.projectName=${projectName} -Dsonar.projectKey=${projectName}"
  }
}

// Install NPM Dependencies
def installDeps(String fromDir) {
  sh 'pwd'
  sh "cd ${fromDir} && npm install"
}


// OWASP Dependency-Check Vulnerabilities
def owasp() { 
  dependencyCheck additionalArguments: ''' 
      -o './'
      -s './'
      -f 'ALL' 
      --prettyPrint
  ''', odcInstallation: 'DP_Check'
                
  dependencyCheckPublisher pattern: 'dependency-check-report.xml'
}


// Build Docker Image
def docker_build(String projectName) {
  String projectname = projectName.toLowerCase()
  sh "cd ${projectName} && docker compose -p ${projectname} build --force-rm --no-cache"
}

// Docker Image Scanning using Trivy
def image(String image_name) {
  sh "trivy image --exit-code 0  --severity HIGH,CRITICAL --scanners vuln ${image_name} | tee -a trivyimage.txt"  
}

// Docker Tag Image & Push
def tagPush(String image_name, String dockerhub_image_tag){
  withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
      sh "docker tag ${image_name} ${dockerhub_image_tag}"
      sh 'echo "salafiyAAA" | docker login -u mbaynd --password-stdin'
      sh "docker push ${dockerhub_image_tag}"
  }
}

// Deploy to container to Staging
def deployBuild(String projectName, String service) {
  String projectname = projectName.toLowerCase()
  String container_name = projectname + "-" + service
  sh "cd ${projectName} &&  docker rm -f ${container_name} && docker compose -p ${projectname} up ${service} -d"
}

// DAST - Dynamic Application Security Testing
def scanDeployment(String targetURL) {
  sh "docker run -t ghcr.io/zaproxy/zaproxy zap-baseline.py -t ${targetURL}"
}






