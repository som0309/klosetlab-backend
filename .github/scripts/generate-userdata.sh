#!/bin/bash
set -e

# 인자 받기
ENVIRONMENT=$1
IMAGE_URI=$2
AWS_REGION=$3

# 현재 스크립트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATES_DIR="${SCRIPT_DIR}/../templates"

# 템플릿 파일 읽기
COMPOSE_TEMPLATE=$(cat "${TEMPLATES_DIR}/docker-compose.yml")
ENV_TEMPLATE=$(cat "${TEMPLATES_DIR}/.env")

# User Data 스크립트 생성
cat > user-data.sh <<'EOF'
#!/bin/bash
set -e

# 로깅 설정
exec > >(tee /var/log/user-data.log)
exec 2>&1

echo "=== User Data Script Started at $(date) ==="

# 환경 변수
export AWS_REGION="{{AWS_REGION}}"
export IMAGE_URI="{{IMAGE_URI}}"
export ENVIRONMENT="{{ENVIRONMENT}}"

# 필수 도구 설치
echo "Installing required tools..."
yum update -y
yum install -y docker jq gettext-base

# Docker 설정
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Docker Compose 설치
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 작업 디렉토리
APP_DIR="/home/ec2-user/app"
mkdir -p ${APP_DIR}
cd ${APP_DIR}

# ECR 로그인
echo "Logging into ECR..."
aws ecr get-login-password --region ${AWS_REGION} | \
  docker login --username AWS --password-stdin $(echo ${IMAGE_URI} | cut -d'/' -f1)

# SSM에서 환경 변수 가져오기
echo "Fetching configuration from Parameter Store..."

get_param() {
  aws ssm get-parameter --name "$1" --query "Parameter.Value" --output text --region ${AWS_REGION} 2>/dev/null || echo ""
}

get_secure_param() {
  aws ssm get-parameter --name "$1" --with-decryption --query "Parameter.Value" --output text --region ${AWS_REGION} 2>/dev/null || echo ""
}

# 환경 변수 설정
export MONGODB_HOST=$(get_param "/${ENVIRONMENT}/mongodb/host")
export MONGODB_PORT=$(get_param "/${ENVIRONMENT}/mongodb/port")
export MONGODB_DATABASE=$(get_param "/${ENVIRONMENT}/mongodb/database")
export MONGODB_USERNAME=$(get_secure_param "/${ENVIRONMENT}/mongodb/username")
export MONGODB_PASSWORD=$(get_secure_param "/${ENVIRONMENT}/mongodb/password")

export REDIS_HOST=$(get_param "/${ENVIRONMENT}/redis/host")
export REDIS_PORT=$(get_param "/${ENVIRONMENT}/redis/port")
export REDIS_PASSWORD=$(get_secure_param "/${ENVIRONMENT}/redis/password")

export KAFKA_BOOTSTRAP_SERVERS=$(get_param "/${ENVIRONMENT}/kafka/bootstrap-servers")

export MYSQL_HOST=$(get_param "/${ENVIRONMENT}/mysql/host")
export MYSQL_DATABASE=$(get_param "/${ENVIRONMENT}/mysql/database")
export MYSQL_USERNAME=$(get_secure_param "/${ENVIRONMENT}/mysql/username")
export MYSQL_PASSWORD=$(get_secure_param "/${ENVIRONMENT}/mysql/password")

export FASTAPI_URL=$(get_param "/${ENVIRONMENT}/fastapi/url")

# docker-compose.yml 생성
echo "Creating docker-compose.yml..."
cat > docker-compose.yml <<'COMPOSE_EOF'
{{COMPOSE_TEMPLATE}}
COMPOSE_EOF

# .env 파일 생성
echo "Creating .env file..."
cat > .env <<'ENV_EOF'
{{ENV_TEMPLATE}}
ENV_EOF

# envsubst로 환경 변수 치환
envsubst < .env > .env
rm .env

# 파일 권한
chmod 600 .env
chmod 644 docker-compose.yml

echo "✅ Configuration files created"
ls -lh docker-compose.yml .env

# 배포
echo "Deploying application..."

# 이미지 Pull
echo "Pulling Docker image..."
docker-compose pull

# 컨테이너 시작
echo "Starting containers..."
docker-compose up -d --remove-orphans

# 헬스체크
echo "Waiting for application health..."
for i in {1..30}; do
  if docker exec spring-boot-app wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health 2>/dev/null; then
    echo "✅ Application is healthy (attempt $i/30)"
    
    cat > deployment-info.json <<DEPLOY_EOF
{
  "environment": "${ENVIRONMENT}",
  "deployed_at": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "image_uri": "${IMAGE_URI}",
  "instance_id": "$(ec2-metadata --instance-id | cut -d' ' -f2)"
}
DEPLOY_EOF
    
    echo "=== Deployment successful at $(date) ==="
    exit 0
  fi
  
  echo "Health check attempt $i/30 failed, retrying..."
  sleep 10
done

echo "❌ Health check failed after 30 attempts"
docker-compose logs --tail=50
docker-compose ps
exit 1
EOF

# 템플릿 삽입
sed -i "s|{{COMPOSE_TEMPLATE}}|${COMPOSE_TEMPLATE}|g" user-data.sh
sed -i "s|{{ENV_TEMPLATE}}|${ENV_TEMPLATE}|g" user-data.sh

# 변수 치환
sed -i "s|{{IMAGE_URI}}|${IMAGE_URI}|g" user-data.sh
sed -i "s|{{AWS_REGION}}|${AWS_REGION}|g" user-data.sh
sed -i "s|{{ENVIRONMENT}}|${ENVIRONMENT}|g" user-data.sh

# Base64 인코딩
USER_DATA_BASE64=$(base64 -w 0 user-data.sh)
echo "user_data_base64=${USER_DATA_BASE64}" >> $GITHUB_OUTPUT

echo "✅ User Data script created and encoded"
```