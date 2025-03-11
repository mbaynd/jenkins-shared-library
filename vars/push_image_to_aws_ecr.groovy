def call(String aws_ecr,String aws_image_repo, String image_tag) {

  //tags_list = sh(script: "aws ecr list-images --repository-name taskmanager-backend | jq '.imageIds[].imageTag'", returnStdout=true)
  //echo tags_list  
  sh "aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin ${aws_ecr}"
  sh "docker tag  ${aws_image_repo} ${aws_ecr}/${aws_image_repo}:${image_tag}"
  sh "docker push ${aws_ecr}/${aws_image_repo}:${image_tag}"
}

