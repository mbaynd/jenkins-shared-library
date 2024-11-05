// Filesystem Scanner using Trivy defining scanners scanning for secrets
def call(command, format, scanners, severity, outputfile) {
  sh "trivy ${command} --format ${format} --severity ${severity} --scanners ${scanners} --output ${outputfile} ."  
}