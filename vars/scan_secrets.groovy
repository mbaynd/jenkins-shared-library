// Gitub scanning for secrets in Repository
def call(String repo) {
  sh "docker run --rm -t -v \"$PWD:/pwd\" trufflesecurity/trufflehog:latest github --org=trufflesecurity   --repo ${repo}"
  
}