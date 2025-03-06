def call(String environ, String project_image, String image_tag) {

    environ = environ.toLowerCase()
    project = project_image.toLowerCase()
    

    env.KPAY_APP_ENV_LABEL = environ
    
    env.KPAY_REST_APP_SERVICE_NAME = "smartpay-rest-"+environ
    env.KPAY_WEB_APP_SERVICE_NAME = "smartpay-web-"+environ
    env.KPAY_MIG_APP_SERVICE_NAME = "smartpay-mig-"+environ

    env.KPAY_APP_PROJECT_NAME = project+"-"+environ

    env.KPAY_APP_NETWORK = project + "-net-" + environ

    env.KPAY_APP_REPLICAS = 1 
    env.KPAY_APP_LOKI_BASE_URL = "http://10.0.12.211:3100"
    env.KPAY_APP_TAG = image_tag
    
    env.KPAY_ANSIBLE_INVENTORY = "/var/lib/jenkins/ansible_inventories/kpay.ini"
    env.KPAY_ANSIBLE_PLAYBOOK = "/var/lib/jenkins/ansible_inventories/deploy.yaml"

    env.KPAY_BACKEND_DOCKER_COMPOSE_TEMPLATE =  "kpay/backend/docker-compose.orig.yaml"
    env.KPAY_BACKEND_DOCKER_COMPOSE =  "docker-compose-"+project+"-"+environ+".yaml"

    if (environ == "uat" || environ == "dev") {

        environ = "uat"
        env.KPAY_AWS_REGION = "us-east-1"
        env.KPAY_AWS_ECR = '688149143527.dkr.ecr.us-east-1.amazonaws.com'

        env.KPAY_REST_APP_IMAGE = "kpay-rest"
        env.KPAY_REST_SCHEDULER_APP_IMAGE = "smartpay-rest"
        env.KPAY_REST_APP_SUBNET = "192.168.191.0/24"
        env.KPAY_REST_APP_PORT = "18780"

        env.KPAY_WEB_APP_IMAGE = "kpay-web"
        env.KPAY_WEB_APP_SUBNET = "192.168.191.0/24"
        env.KPAY_WEB_APP_PORT = "18780"

        env.KPAY_MIG_APP_IMAGE = "migration-app"
        env.KPAY_MIG_APP_SUBNET = "192.168.191.0/24"
        env.KPAY_MIG_APP_PORT = "19780"

        //env.KPAY_REST_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
    
    } 

    if (environ == "prod") {
        
        env.KPAY_AWS_REGION = "eu-west-1"
        env.KPAY_AWS_ECR = '688149143527.dkr.ecr.eu-west-1.amazonaws.com'
     
        env.KPAY_REST_APP_IMAGE = "smartpay-rest"
        env.KPAY_REST_SCHEDULER_APP_IMAGE = "smartpay-rest-scheduler"
        env.KPAY_REST_APP_SUBNET = "192.168.191.0/24"
        env.KPAY_REST_APP_PORT = "18780"

        env.KPAY_REST_APP_IMAGE = "smartpay-web"
        env.KPAY_REST_APP_SUBNET = "192.168.192.0/24"
        env.KPAY_REST_APP_PORT = "18780"

        env.KPAY_WEB_APP_IMAGE = "smartpay-migration"
        env.KPAY_WEB_APP_SUBNET = "192.168.193.0/24"
        env.KPAY_WEB_APP_PORT = "19081"

     
        //env.KPAY_REST_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
    
    }

    if (environ == "poc") {

        env.KPAY_APP_ENV_LABEL = "monit"
        env.KPAY_AWS_REGION = "us-west-2"    
        env.KPAY_AWS_ECR = '688149143527.dkr.ecr.us-west-2.amazonaws.com'
     

        env.KPAY_REST_APP_IMAGE = "kpay-backend/smartpay-rest"
        env.KPAY_REST_SCHEDULER_APP_IMAGE = "smartpay-rest-scheduler"
        env.KPAY_REST_APP_SUBNET = "192.168.191.0/24"
        env.KPAY_REST_APP_PORT = "18780"

        env.KPAY_REST_APP_IMAGE = "kpay-backend/smartpay-web"
        env.KPAY_REST_APP_SUBNET = "192.168.192.0/24"
        env.KPAY_REST_APP_PORT = "18780"

        env.KPAY_WEB_APP_IMAGE = "smartpay-migration"
        env.KPAY_WEB_APP_SUBNET = "192.168.193.0/24"
        env.KPAY_WEB_APP_PORT = "19081"

                
        env.KPAY_REST_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
    }
}