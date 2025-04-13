def call(String environ, String project_image, String image_tag) {

    env.KPAY_APP_PROJECT_NAME = "stack-kpay-backend-"+environ

    environ = environ.toLowerCase()
    project = project_image.toLowerCase()
    

    env.KPAY_APP_ENV_LABEL = environ
    
    env.KPAY_REST_APP_SERVICE_NAME = "smartpay-rest"
    env.KPAY_REST_SCHED_APP_SERVICE_NAME = "smartpay-rest"
    env.KPAY_WEB_APP_SERVICE_NAME = "smartpay-web"
    env.KPAY_MIG_APP_SERVICE_NAME = "smartpay-mig"
    env.KPAY_DASHBOARD_APP_SERVICE_NAME = "smartpay-dashboard"


    env.KPAY_DASHBOARD_APP_NETWORK = "kpay-dashboard-" + environ
    env.KPAY_APP_NETWORK = "smartpay-net-" + environ
    env.KPAY_SCHED_APP_NETWORK = "smartpay-net-rest-sched" + environ

    env.KPAY_APP_REPLICAS = 1 
    env.KPAY_APP_LOKI_BASE_URL = "http://10.0.12.211:3100"
    env.KPAY_APP_TAG = image_tag
    
    env.KPAY_ANSIBLE_INVENTORY = "/var/lib/jenkins/ansible_inventories/kpay.ini"
    env.KPAY_ANSIBLE_PLAYBOOK = "/var/lib/jenkins/ansible_inventories/deploy.yaml"

    env.KPAY_BACKEND_DOCKER_COMPOSE_TEMPLATE =  "kpay/backend/deployments/web_rest/docker-compose.orig.yaml"
    env.KPAY_BACKEND_DOCKER_COMPOSE =  "docker-compose-"+project+"-"+environ+".yaml"

    env.KPAY_BACKEND_SCHED_DOCKER_COMPOSE_TEMPLATE =  "kpay/backend/deployments/web_rest/docker-compose-sched.orig.yaml"
    env.KPAY_BACKEND_SCHED_DOCKER_COMPOSE =  "docker-compose-sched"+project+"-"+environ+".yaml"

    env.KPAY_BACKEND_DASHBOARD_DOCKER_COMPOSE_TEMPLATE =  "kpay/backend/deployments/dashboard/docker-compose.orig.yaml"
    env.KPAY_BACKEND_DASHBOARD_DOCKER_COMPOSE =  "docker-compose-dashboard"+project+"-"+environ+".yaml"

    if (environ == "uat" || environ == "dev") {

        environ = "uat"
        env.KPAY_APP_ENV_LABEL = environ
        env.KPAY_AWS_REGION = "us-east-1"
        env.KPAY_AWS_ECR = '688149143527.dkr.ecr.us-east-1.amazonaws.com'

        env.KPAY_REST_APP_IMAGE = "kpay-rest"
        env.KPAY_REST_APP_PORT = "8780"

        env.KPAY_REST_SCHED_APP_IMAGE = "kpay-rest-scheduler"
        env.KPAY_REST_SCHED_APP_PORT = "21790"

        env.KPAY_WEB_APP_IMAGE = "kpay-web"
        env.KPAY_WEB_APP_PORT = "8781"

        env.KPAY_MIG_APP_IMAGE = "smartpay-migration"
        env.KPAY_MIG_APP_PORT = "9781"
    
        env.KPAY_DASHBOARD_APP_IMAGE  = "kpay-dashboard"
        env.KPAY_DASHBOARD_APP_PORT = "9790"
    } 

    if (environ == "prod") {
        
        env.KPAY_AWS_REGION = "eu-west-1"
        env.KPAY_AWS_ECR = '688149143527.dkr.ecr.eu-west-1.amazonaws.com'
     
        env.KPAY_REST_APP_IMAGE = "smartpay-rest"
        env.KPAY_REST_APP_PORT = "8080"

        env.KPAY_REST_SCHED_APP_IMAGE = "smartpay-rest-scheduler"
        env.KPAY_REST_SCHED_APP_PORT = "8890"

        env.KPAY_WEB_APP_IMAGE = "smartpay-web"
        env.KPAY_WEB_APP_PORT = "8081"

        env.KPAY_MIG_APP_IMAGE = "smartpay-migration"
        env.KPAY_MIG_APP_PORT = "9081"

        env.KPAY_DASHBOARD_APP_IMAGE  = "smartpay-dashboard"
        env.KPAY_DASHBOARD_APP_PORT = "9090"


    }

    if (environ == "poc") {

        env.KPAY_APP_ENV_LABEL = "monit"
        env.KPAY_AWS_REGION = "us-west-2"    
        env.KPAY_AWS_ECR = '688149143527.dkr.ecr.us-west-2.amazonaws.com'

        env.KPAY_REST_APP_IMAGE = "kpay-backend/smartpay-rest"
        env.KPAY_REST_APP_PORT = "18780"

        env.KPAY_REST_SCHED_APP_IMAGE = "smartpay-rest-scheduler"
        env.KPAY_REST_SCHED_APP_PORT = "18890"

        env.KPAY_WEB_APP_IMAGE = "smartpay-web"
        env.KPAY_WEB_APP_PORT = "19081"

        env.KPAY_MIG_APP_IMAGE = "smartpay-migration"
        env.KPAY_MIG_APP_PORT = "19781"

        env.KPAY_DASHBOARD_APP_IMAGE  = "smartpay-dashboard"
        env.KPAY_DASHBOARD_APP_PORT = "9890"
                
        //env.KPAY_REST_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
    }
}