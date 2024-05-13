# Checkout the Repository
def checkoutCode() {
    checkout scm
}

# Git Repositorey Scanning using Trivy
def repo(String repo) {
  sh 'trivy repository --exit-code 0 --no-progress --severity HIGH,CRITICAL --scanners vuln ${repo}'
}

# Filesystem Scanner using Trivy
def fs() {
  sh 'trivy fs --scanners vuln .'
}

# Docker Image Scanning using Trivy
def image(String image_name) {
  sh '''
    trivy image --exit-code 0  --severity HIGH,CRITICAL --scanners vuln ${image_name}
  '''
}

