[![Deploy to Azure Spring Cloud](https://github.com/Azure/azure-spring-initializr/actions/workflows/deploy-to-azure-spring-cloud.yml/badge.svg)](https://github.com/Azure/azure-spring-initializr/actions/workflows/deploy-to-azure-spring-cloud.yml)
[![Java CI with Maven](https://github.com/Azure/azure-spring-initializr/actions/workflows/maven.yml/badge.svg)](https://github.com/Azure/azure-spring-initializr/actions/workflows/maven.yml)
[![CodeQL](https://github.com/Azure/azure-spring-initializr/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/Azure/azure-spring-initializr/actions/workflows/codeql-analysis.yml)

# Azure Spring Initializr

## Development

Currently, we're using [Git Submodules](https://www.git-scm.com/docs/gitmodules) to include [Spring Initializr Backend](https://github.com/spring-io/initializr) and [Spring Initializr Frontend](https://github.com/spring-io/start.spring.io) source code into this project.

You can find how to use git submodules from [here](https://github.blog/2016-02-01-working-with-submodules/)

### Setup local development environment

Once you cloned this repo, you need to update sub modules

```shell
git submodule update --init --recursive
```


## Continuus Deployment

[The GitHub Action](https://github.com/Azure/azure-spring-initializr/blob/main/.github/workflows/deploy-to-azure-spring-cloud.yml) deploys every change to [Azure Spring Cloud](https://azure.microsoft.com/en-us/services/spring-cloud/), the live instance of **Azure Spring Initializr** can be accessed [here](https://azure-spring-initializr-dev-azure-spring-initializr.azuremicroservices.io)