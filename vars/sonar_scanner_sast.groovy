// Static Code Analysis with SonarQube
def call(String projectName, String sonar_env) {
    withSonarQubeEnv(sonar_env) {
      sh "'/opt/sonar-scanner/bin/sonar-scanner' -Dsonar.projectName=${projectName} -Dsonar.projectKey=${projectName}"
    } 
}