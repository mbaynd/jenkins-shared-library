// Git Repositorey Scanning using Trivy
def call(String repo) {
  sh "trivy repository --exit-code 0 --no-progress --severity HIGH,CRITICAL --scanners vuln ${repo} | tee -a trivyrepo.txt"
}