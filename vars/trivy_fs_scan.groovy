// Filesystem Scanner using Trivy
def call() {
  sh "trivy fs --scanners vuln . | tee -a trivyfs.txt"  
}