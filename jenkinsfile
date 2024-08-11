def ENV_NAME = getEnvName(env.BRANCH_NAME)
def CONTAINER_NAME = "crm-geo-" + ENV_NAME
def CONTAINER_TAG = getTag(env.BUILD_NUMBER, env.BRANCH_NAME)
def HTTP_PORT = getHTTPPort(env.BRANCH_NAME)
def EMAIL_RECIPIENTS = "mohamed.banhakeia@hotmail.com"

node {
    try {
        // Cette étape consiste à initialiser tous les outils dont on aura besoin
        stage('Initialize') {
            def dockerHome = tool 'dockerlatest'
            def mavenHome = tool 'mavenlatest'
            env.PATH = "${dockerHome}/bin:${mavenHome}/bin:${env.PATH}"
        }

        stage('Checkout') {
            checkout scm
        }

        stage('Build with Tests') {
           sh "mvn clean install"
        }

        stage('SonarQube Analysis') {
            withSonarQubeEnv('localhost_sonarqube') {
                sh "mvn sonar:sonar -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
            }
            timeout(time: 1, unit: 'MINUTES') {
                def qg = waitForQualityGate()
                if (qg.status != 'OK') {
                    error "Pipeline aborted due to quality gate failure! ${qg.status}"
                }
            }
        }

        stage("Image Prune") {
            imagePrune(CONTAINER_NAME)
        }

        stage("Image Build") {
            imageBuild(CONTAINER_NAME, CONTAINER_TAG)
        }

        stage("Push to Docker Registry") {
            withCredentials([usernamePassword(credentialsId: 'dockerhubcredentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                pushToImage(CONTAINER_NAME, CONTAINER_TAG, USERNAME, PASSWORD)
            }
        }
    } catch (e) {
        error "Pipeline failed: ${e.message}"
    }
}

def imagePrune(containerName) {
    try {
        sh "docker image prune -f"
        sh "docker stop ${containerName} || true"
    } catch (ignored) {
        echo "Ignoring error during image prune or container stop."
    }
}

def imageBuild(containerName, tag) {
    sh "docker build -t ${containerName}:${tag} -t ${containerName}:latest --pull --no-cache ."
    echo "Image build complete"
}

def pushToImage(containerName, tag, dockerUser, dockerPassword) {
    sh "docker login -u ${dockerUser} -p ${dockerPassword}"
    sh "docker tag ${containerName}:${tag} ${dockerUser}/${containerName}:${tag}"
    sh "docker push ${dockerUser}/${containerName}:${tag}"
    echo "Image push complete"
}

def runApp(containerName, tag, dockerHubUser, httpPort, envName) {
    sh "docker pull ${dockerHubUser}/${containerName}:${tag}"
    sh "docker run --rm --env SPRING_PROFILES_ACTIVE=${envName} -d -p ${httpPort}:${httpPort} --name ${containerName} ${dockerHubUser}/${containerName}:${tag}"
    echo "Application started on port: ${httpPort} (http)"
}
