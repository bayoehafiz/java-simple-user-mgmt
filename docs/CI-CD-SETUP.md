# CI/CD Setup Guide

This document explains the DevOps/CI-CD pipeline setup for the Java User Management API project.

## 🚀 Overview

Our CI/CD pipeline uses **GitHub Actions** to automate:
- ✅ Testing and code quality checks
- 🏗️ Building and packaging with **Java 17**
- 🔒 Security scanning and JWT secret management
- 🐳 Docker image creation with security hardening
- 🚀 Deployment to staging/production
- 📋 Pull request validation
- 🏷️ Automated releases

## 📁 Pipeline Structure

```
.github/workflows/
├── ci-cd.yml           # Main CI/CD pipeline
├── pr-check.yml        # Pull request validation
└── release.yml         # Release automation
```

## 🔧 Required Secrets Setup

To enable the full CI/CD pipeline, configure these secrets in your GitHub repository:

### 1. Docker Hub Secrets
```
DOCKER_USERNAME = your-dockerhub-username
DOCKER_PASSWORD = your-dockerhub-password-or-token
```

### 2. GitHub Token (Auto-provided)
```
GITHUB_TOKEN = (automatically provided by GitHub Actions)
```

### 3. JWT Security Configuration

For production deployments, ensure JWT secrets are properly configured:

```bash
# Production environment variables (set in deployment environment)
JWT_SECRET = your-secure-random-jwt-secret-key-here
JWT_EXPIRATION = 86400  # 24 hours in seconds
```

**Security Best Practices:**
- Generate strong, random JWT secrets (minimum 32 characters)
- Use different secrets for different environments
- Never commit secrets to version control
- Rotate secrets regularly in production

## 🏗️ Pipeline Workflows

### Main CI/CD Pipeline (`ci-cd.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main`

**Jobs:**
1. **Test** - Runs all 38 tests with Maven
2. **Code Quality** - Checkstyle, SpotBugs analysis
3. **Build** - Creates JAR artifacts
4. **Security Scan** - Trivy vulnerability scanning
5. **Docker Build** - Multi-platform Docker images
6. **Deploy Staging** - Deploys `develop` branch to staging
7. **Deploy Production** - Deploys `main` branch to production
8. **Notification** - Reports deployment status

### Pull Request Validation (`pr-check.yml`)

**Triggers:**
- Pull requests to `main` or `develop`

**Features:**
- Automated test execution
- Code coverage reporting with JaCoCo
- Automated PR comments with results
- Code review checklist generation

### Release Pipeline (`release.yml`)

**Triggers:**
- Git tags matching `v*` pattern (e.g., `v1.0.0`)

**Features:**
- Automated changelog generation
- GitHub release creation
- JAR artifact attachment
- Tagged Docker image builds

## 🔍 Code Quality Tools

### JaCoCo Code Coverage
- **Minimum Coverage**: 80%
- **Reports**: `target/site/jacoco/index.html`
- **Command**: `mvn jacoco:report`

### Checkstyle
- **Standard**: Google Java Style Guide
- **Configuration**: `google_checks.xml`
- **Command**: `mvn checkstyle:check`

### SpotBugs
- **Effort Level**: Max
- **Threshold**: Low
- **Command**: `mvn spotbugs:check`

## 🐳 Docker Integration

### Image Naming
- **Repository**: `your-username/java-user-api`
- **Tags**: 
  - `latest` (main branch)
  - `develop` (develop branch)
  - `v1.0.0` (release tags)
  - `main-sha123456` (commit-based)

### Multi-Platform Builds
- `linux/amd64`
- `linux/arm64`

## 🚀 Deployment Environments

### Staging Environment
- **Trigger**: Push to `develop` branch
- **Environment**: `staging`
- **Image**: `your-username/java-user-api:develop`

### Production Environment
- **Trigger**: Push to `main` branch
- **Environment**: `production`
- **Image**: `your-username/java-user-api:latest`

## 📋 Workflow Usage

### Setting up Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Add the required secrets:

```bash
# Docker Hub credentials
DOCKER_USERNAME: bayoehafiz
DOCKER_PASSWORD: your-dockerhub-token
```

### Creating a Release

1. Create and push a tag:
```bash
git tag v1.0.0
git push origin v1.0.0
```

2. The release workflow will automatically:
   - Run tests
   - Build the application
   - Create a GitHub release
   - Build and push tagged Docker images

### Testing the Pipeline

1. **Push to develop branch**:
```bash
git checkout develop
git add .
git commit -m "feat: add new feature"
git push origin develop
```

2. **Create a pull request**:
   - Open PR from `develop` to `main`
   - Watch automated validation and comments

3. **Merge to main**:
   - Triggers production deployment pipeline

## 🔧 Local Development

### Run Quality Checks Locally

```bash
# Run all tests with coverage
mvn clean test jacoco:report

# Run code quality checks
mvn checkstyle:check
mvn spotbugs:check

# Build Docker image locally
docker build -t java-user-api:local .

# Run locally
docker run -p 8080:8080 java-user-api:local
```

### Test Coverage Report

After running tests with coverage:
- Open `target/site/jacoco/index.html` in your browser
- Review coverage metrics for each class and package

## 🚨 Troubleshooting

### Common Issues

1. **Docker Build Fails**
   - Ensure JAR file exists in `target/` directory
   - Check Docker Hub credentials

2. **Tests Fail in CI**
   - Verify tests pass locally
   - Check for environment-specific issues

3. **Coverage Below Threshold**
   - Add more unit tests
   - Adjust coverage threshold in `pom.xml`

4. **Security Scan Issues**
   - Update dependencies with known vulnerabilities
   - Review Trivy scan results in GitHub Security tab

## 📈 Monitoring and Metrics

### GitHub Actions Dashboard
- View workflow runs: `Actions` tab in GitHub repository
- Monitor success/failure rates
- Review execution times

### Docker Hub
- Track image download statistics
- Monitor image sizes and vulnerabilities

## 🔄 Continuous Improvement

### Pipeline Enhancements
- Add performance testing (JMeter)
- Integrate with SonarQube for detailed code analysis
- Add automated database migrations
- Implement blue-green deployments
- Add Slack/Teams notifications

### Metrics Collection
- Integrate with Prometheus/Grafana
- Add application performance monitoring (APM)
- Track deployment frequency and lead time

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Build Push Action](https://github.com/docker/build-push-action)
- [Maven Build Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
