def call(String environ, String project_image, String image_tag) {

    environ = environ.toLowerCase()
    project = project_image.toLowerCase()
    

    env.KPAY_APP_ENV_LABEL = environ.toLowerCase()
    env.KPAY_APP_PROJECT_NAME = project_image 
    env.KPAY_APP_SERVICE_NAME = project_image
    env.KPAY_APP_IMAGE = project_image
    env.KPAY_APP_HOSTNAME = project_image
    env.KPAY_APP_REPLICAS = 1 
    env.KPAY_APP_LOKI_BASE_URL = "http://10.0.12.211:3100"
    env.KPAY_APP_TAG = image_tag
    
    env.KPAY_ANSIBLE_INVENTORY = "/var/lib/jenkins/ansible_inventories/kpay.ini"
    env.KPAY_ANSIBLE_PLAYBOOK = "/var/lib/jenkins/ansible_inventories/deploy.yaml"


    env.KPAY_APP_NETWORK = project_image + "-net-" + environ

    env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE = 'kpay-coud-app/docker-compose.orig.yaml'

    if (environ == "uat" || environ == "dev") {

        environ = "uat"

        env.KPAY_APP_AWS_REGION = "us-east-1"
        env.KPAY_APP_AWS_ECR = '688149143527.dkr.ecr.us-east-1.amazonaws.com'

        env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
        env.KPAY_APP_SUBNET = "192.168.201.0/24"
        env.KPAY_APP_FRONTEND_PORT = "13313"
        env.KPAY_APP_BACKEND_PORT = "13312"
    } 

    if (environ == "prod") {
        
        env.KPAY_APP_AWS_REGION = "eu-west-1"
        env.KPAY_APP_AWS_ECR = '688149143527.dkr.ecr.eu-west-1.amazonaws.com'

        env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
        env.KPAY_APP_SUBNET = "192.168.211.0/24"
        env.KPAY_APP_FRONTEND_PORT = "3313"
        env.KPAY_APP_BACKEND_PORT = "3312"
    
    }

    if (environ == "poc") {


        env.KPAY_APP_AWS_REGION = "us-west-2"    
        env.KPAY_APP_AWS_ECR = '688149143527.dkr.ecr.us-west-2.amazonaws.com'

        env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
        env.KPAY_APP_SUBNET = "192.168.231.0/24"
        env.KPAY_APP_FRONTEND_PORT = "33113"
        env.KPAY_APP_BACKEND_PORT = "33112"
    }
}