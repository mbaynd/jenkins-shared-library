// Checkout the Repository
def checkoutCode(String repo, branch) {
    //sh "git clone ${repo} ${projectName} && cd ${projectName}"
    git branch: ${main}, url: ${repo}
}

// Gitub scanning for secrets in Repository
def scan_secrets(String repo) {
  sh "docker run --rm -t -v \"$PWD:/pwd\" trufflesecurity/trufflehog:latest github --org=trufflesecurity   --repo ${repo}"
}

// Git Repositorey Scanning using Trivy
def repo(String repo) {
  sh "trivy repository --exit-code 0 --no-progress --severity HIGH,CRITICAL --scanners vuln ${repo} | tee -a trivyrepo.txt"
}


// Filesystem Scanner using Trivy
def fs() {
  sh "trivy fs --scanners vuln . | tee -a trivyfs.txt"  
}

// Filesystem Scanner using Trivy defining scanners scanning for secrets
def trivy_scan(command, format, scanners, severity, outputfile) {
  sh "trivy ${command} --format ${format} --severity ${severity} --scanners ${scanners} --output ${outputfile} ."  
}

// Static Code Analysis with SonarQube solution
def sast(String projectName, String credentialsId) {
    def sonarScannerHome= tool 'sonar-scanner'
    withSonarQubeEnv(insatllationName: 'sonarqube', envOnly: true, credentialsId: "${credentialsId}") {
        sh "${sonarScannerHome}/bin/sonar-scanner -Dsonar.sources=. -Dsonar.projectName=${projectName} -Dsonar.projectKey=${projectName} -Dsonar.projectVersion=1.0 -Dsonar.analysis.buildNumber=$BUILD_NUMBER -Dsonar.java.binaries=./target/classes"
    }

    //withSonarQubeEnv("sonar") {
    //  sh "'/opt/sonar-scanner/bin/sonar-scanner' -Dsonar.projectName=${projectName} -Dsonar.projectKey=${projectName}"
    //} 
}

// Install NPM Dependencies
def installDeps(String fromDir) {
  sh 'pwd'
  sh "cd ${fromDir} && npm install"
}


// OWASP Dependency-Check Vulnerabilities
      //--nvdApiKey 'ed43c876-8976-4e9c-aa2a-346aafb569ba' 

      //--updateOnly
      //--nvdApiDelay=5000
      //--nvdApiEndpoint file:///var/lib/jenkins/DependencyCheck/dependency-check/nvd.json
      //--data /var/lib/jenkins/DependencyCheck/dependency-check/data
def owasp() { 

  dependencyCheck additionalArguments: ''' 
      -o './'
      -s './'
      -f 'ALL' 
      --nvdApiKey "d1511e02-8d93-4613-b3d9-c95e493fc332" 
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

// Docker Image Scanning using Trivy
def image_bom_output(String image_name) {
  sh "if [ ! -d target ]; then mkdir target; fi"
  sh "trivy image  --format cyclonedx  --exit-code 0  --severity HIGH,CRITICAL --scanners vuln ${image_name} --output target/trivy_image_sbom.json"  
}


// Docker Image Scanning using Trivy to output file
def image_bom_outputfile(String image_name, String outputfile) {
  sh "if [ ! -d target ]; then mkdir target; fi"
  sh "trivy image  --format cyclonedx  --exit-code 0  --severity HIGH,CRITICAL --scanners vuln ${image_name} --output ${outputfile}"  
}


def dockerHubRepoLogin(String dockerhub_username, String dockerhub_token) {
  sh 'echo ${dockerhub_username} | docker login -u ${dockerhub_token} --password-stdin'
}

def dockerAwsEcrRepoLogin(String aws_ecr,String aws_image_repo, String image_tag) {

  //tags_list = sh(script: "aws ecr list-images --repository-name taskmanager-backend | jq '.imageIds[].imageTag'", returnStdout=true)
  //echo tags_list  
  sh "aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin ${aws_ecr}"
  sh "docker tag  ${aws_image_repo} ${aws_ecr}/${aws_image_repo}:${image_tag}"
  sh "docker push ${aws_ecr}/${aws_image_repo}:${image_tag}"
}

def dockerAwsEcrRepoLoginWithProfile(String aws_ecr,String aws_image_repo, String image_tag, String profile, String region) {

  //tags_list = sh(script: "  ", returnStdout=true)
  //echo tags_list  
  sh "aws ecr get-login-password --region ${region} --profile ${profile} | docker login --username AWS --password-stdin ${aws_ecr}"
  //sh "docker tag  ${aws_image_repo} ${aws_ecr}/${aws_image_repo}:${image_tag}"
  sh "docker push ${aws_ecr}/${aws_image_repo}:${image_tag}"
}

// Docker Tag Image & Push
def tagPush(String image_name, String dockerhub_image_tag){
  /*
  withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
      sh "docker tag ${image_name} ${dockerhub_image_tag}"
      sh 'echo ${dockerhub_passwd} | docker login -u ${dockerhub_user} --password-stdin'
      sh "docker push ${dockerhub_image_tag}"
  }*/

//  withCredentials([string(credentialsId: 'DOCKER_HUB_USER', variable: 'DOCKER_HUB_USER'), string(credentialsId: 'DOCKER_HUB_PWD', variable: 'DOCKER_HUB_PWD')]) {
      sh "docker tag ${image_name} ${dockerhub_image_tag}"
      //sh 'echo ${DOCKER_HUB_PWD} | docker login -u ${DOCKER_HUB_USER} --password-stdin'
      sh "docker push ${dockerhub_image_tag}"
      //sh 'docker scan ${dockerhub_image_tag}'
//  }
}


// Deploy to container to Staging
def deployBuild(String projectName, String service) {
  String projectname = projectName.toLowerCase()
  String container_name = projectname + "-" + service
  sh "cd ${projectName} &&  docker rm -f ${container_name} && docker compose -p ${projectname} up ${service} -d"
}

// DAST - Dynamic Application Security Testing
def scanDeployment(String targetURL) {
  //sh 'docker run -t ghcr.io/zaproxy/zaproxy zap-baseline.py -t \"${targetURL}\" -j -a -r stable-full-scan-report.html'
  sh 'docker run --rm --user root -v $(pwd):/zap/wrk  -t ghcr.io/zaproxy/zaproxy zap-full-scan.py  -t $targetURL -r stable_full_scan_report.html || true'
  //sh 'docker run --user $(id -u):$(id -g) -v $(pwd):/zap/wrk  -t ghcr.io/zaproxy/zaproxy zap-full-scan.py  -t https://app.cashespeces.net -r stable-full-scan-report.html 2> /dev/null; (($? == 2)) && echo "Done" >&2'
}






