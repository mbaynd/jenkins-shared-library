def call(String environ, String project_image, String image_tag) {

    // Define custom build Message
    def shortCommit = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()

    currentBuild.displayName = "${env.ENV} - #${BUILD_NUMBER}"
    currentBuild.description = "${env.ENV}: [ User: ${env.BUILD_USER} - Commit: ${shortCommit} -  Message: ${commitMessage} ]"

    
    // Set Environnement variables for building projects
    environ = environ.toLowerCase()
    project = project_image.toLowerCase()
    

    env.KPAY_APP_ENV_LABEL = environ
    
    env.KPAY_APP_SERVICE_NAME = project+"-"+environ
    env.KPAY_APP_PROJECT_NAME = project+"-"+environ
    env.KPAY_APP_HOSTNAME = project+"-"+environ
    env.KPAY_APP_NETWORK = project + "-net-" + environ

    env.KPAY_APP_REPLICAS = 1 
    env.KPAY_APP_LOKI_BASE_URL = "http://10.0.12.211:3100"
    env.KPAY_APP_TAG = image_tag
    

    env.KPAY_ANSIBLE_INVENTORY = "/var/lib/jenkins/ansible_inventories/kpay.ini"
    env.KPAY_ANSIBLE_PLAYBOOK = "/var/lib/jenkins/ansible_inventories/deploy.yaml"

    env.KPAY_COMPOSE_FILE  = "docker-compose-"+project+"-"+environ+".yaml"

    if (environ == "uat" || environ == "dev") {

        environ = "uat"
        env.KPAY_APP_AWS_REGION = "us-east-1"
        env.KPAY_APP_AWS_ECR = '688149143527.dkr.ecr.us-east-1.amazonaws.com'


        switch (project) {
            
            case ~/.*coud.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/coud/deployments/docker-compose.orig.yaml"

                env.KPAY_APP_IMAGE = "kpay-coud-app"
                env.KPAY_APP_SUBNET = "192.168.191.0/24"
                env.KPAY_APP_FRONTEND_PORT = "3313"
                env.KPAY_APP_BACKEND_PORT = "3312"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*cms.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/cms/deployments/docker-compose.orig.yaml"

                env.KPAY_APP_IMAGE = "kpay-cms-bakend"
                env.KPAY_APP_SUBNET = "192.168.192.0/24"
                env.KPAY_APP_FRONTEND_PORT = "3303"
                env.KPAY_APP_BACKEND_PORT = "3302"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*gateway.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/gateway/deployments/docker-compose.orig.yaml"

                env.KPAY_APP_IMAGE = "kpay-gateway-app" 
                env.KPAY_APP_SCHED_IMAGE ="kpay-gateway-app-sched"

                env.KPAY_APP_SUBNET = "192.168.193.0/24"
                env.KPAY_APP_HAS_AUDIO = false // default: false
                env.KPAY_APP_ISSCHEDULER = false // default: false
                env.KPAY_APP_SMSPROVIDER = "ORANGE" // default: ORANGE (EXPRESSO)

                env.KPAY_APP_FRONTEND_PORT = "8393"
                env.KPAY_APP_SCHED_FRONTEND_PORT = "18393"

                env.KPAY_APP_BACKEND_PORT = "8392"
                env.KPAY_APP_SCHED_BACKEND_PORT = "18392"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*biz.*/:

                env.KPAY_APP_REPO_URL = "https://github.com/quickpaysn/kpay-biz-cicd"
                env.KPAY_APP_REPO_BRANCH = "main"

                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/biz/deployments/docker-compose.orig.yaml"

                env.KPAY_APP_SUBNET = "192.168.194.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23343"
                env.KPAY_APP_BACKEND_PORT = "23342"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*api.*/:

                env.KPAY_APP_SUBNET = "192.168.195.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23353"
                env.KPAY_APP_BACKEND_PORT = "23352"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            default:
                println "No matching substring found."
        }
    
    } 

    if (environ == "prod") {
        
        env.KPAY_APP_AWS_REGION = "eu-west-1"
        env.KPAY_APP_AWS_ECR = '688149143527.dkr.ecr.eu-west-1.amazonaws.com'

        switch (project) {
            
            case ~/.*coud.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/coud/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_IMAGE = "kpay-coud-app"
                env.KPAY_APP_SUBNET = "192.168.191.0/24"
                env.KPAY_APP_FRONTEND_PORT = "3413"
                env.KPAY_APP_BACKEND_PORT = "3412"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*cms.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/cms/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_IMAGE = "kpay-cms-bakend"
                env.KPAY_APP_SUBNET = "192.168.192.0/24"
                env.KPAY_APP_FRONTEND_PORT = "3403"
                env.KPAY_APP_BACKEND_PORT = "3402"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*gateway.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/gateway/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_IMAGE = "kpay-gateway-app"
                env.KPAY_APP_SUBNET = "192.168.193.0/24"
                env.KPAY_APP_HAS_AUDIO = false // default: false
                env.KPAY_APP_ISSCHEDULER = false // default: false
                env.KPAY_APP_SMSPROVIDER = "ORANGE" // default: ORANGE (EXPRESSO)
                env.KPAY_APP_FRONTEND_PORT = "23323"
                env.KPAY_APP_BACKEND_PORT = "23322"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*biz.*/:

                env.KPAY_APP_REPO_URL = "https://github.com/quickpaysn/kpay-biz-cicd"
                env.KPAY_APP_REPO_BRANCH = "main"

                env.KPAY_APP_FRONTEND_IMAGE = "kpay-biz-frontend"
                env.KPAY_APP_BACKEND_IMAGE = "kpay-biz-backend" 

                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/biz/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_SUBNET = "192.168.194.0/24"
                env.KPAY_APP_FRONTEND_PORT = "24343"
                env.KPAY_APP_BACKEND_PORT = "24342"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*api.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/api/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_SUBNET = "192.168.195.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23353"
                env.KPAY_APP_BACKEND_PORT = "23352"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            default:
                println "No matching app found."
        }
    
    }

    if (environ == "poc") {

        env.KPAY_APP_ENV_LABEL = "monit"
        env.KPAY_APP_AWS_REGION = "us-west-2"    
        env.KPAY_APP_AWS_ECR = '688149143527.dkr.ecr.us-west-2.amazonaws.com'

        switch (project) {
            
            case ~/.*coud.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/coud/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_IMAGE = "kpay-coud-app"
                env.KPAY_APP_SUBNET = "192.168.191.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23313"
                env.KPAY_APP_BACKEND_PORT = "23312"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*cms.*/:
                env.KPAY_APP_IMAGE = "kpay-cms-bakend"
                env.KPAY_APP_SUBNET = "192.168.192.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23323"
                env.KPAY_APP_BACKEND_PORT = "23322"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*gateway.*/:
                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/gateway/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_IMAGE = "kpay-gateway-app"
                env.KPAY_APP_SUBNET = "192.168.193.0/24"
                env.KPAY_APP_HAS_AUDIO = false // default: false
                env.KPAY_APP_ISSCHEDULER = false // default: false
                env.KPAY_APP_SMSPROVIDER = "ORANGE" // default: ORANGE (EXPRESSO)
                env.KPAY_APP_FRONTEND_PORT = "25323"
                env.KPAY_APP_BACKEND_PORT = "25322"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*biz.*/:

                env.KPAY_APP_REPO_URL = "https://github.com/quickpaysn/kpay-biz-cicd"
                env.KPAY_APP_REPO_BRANCH = "main"

                env.KPAY_APP_FRONTEND_IMAGE = "kpay-biz-frontend"
                env.KPAY_APP_BACKEND_IMAGE = "kpay-biz-backend" 

                env.KPAY_APP_DOCKER_COMPOSE_TEMPLATE =  "kpay/biz/deployments/docker-compose.orig.yaml"
                env.KPAY_APP_SUBNET = "192.168.194.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23343"
                env.KPAY_APP_BACKEND_PORT = "23342"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            case ~/.*api.*/:
                env.KPAY_APP_SUBNET = "192.168.195.0/24"
                env.KPAY_APP_FRONTEND_PORT = "23353"
                env.KPAY_APP_BACKEND_PORT = "23352"
                env.KPAY_APP_HEALTHCHECK_URL = "http://localhost:3000/v1/doc"
                break

            default:
                println "No matching substring found."
        }
    }
}