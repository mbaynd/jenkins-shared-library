
def call(String region, String aws_ecr, String repo) {

    sh "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${aws_ecr}"
    def images = sh(
        script: """
            aws ecr describe-images --repository-name ${repo} --region ${region} | \
            jq -r '.imageDetails[] | select(.imageTags != null) | [.imagePushedAt, .imageTags[]] | @tsv' | \
            sort -r | \
            awk '{print \$2}' | \
            grep commit
        """,
        returnStdout: true
    ).trim()
    return images ? images.split('\n') : []
}
