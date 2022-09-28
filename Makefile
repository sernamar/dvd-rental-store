# ----------------------- #
# Setup                   #
# ----------------------- #
.EXPORT_ALL_VARIABLES:

include .env

# ----------------------- #
# Variables               #
# ----------------------- #

.PHONY: build start stop restart docker-clean ls list-apps logs ps status shell

DOCKER_COMPOSE = docker compose
DOCKER_COMPOSE_FILE = docker-compose.yml

# ----------------------- #
# Managing the stack      #
# ----------------------- #

build:
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) build $(a)

start:
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) up -d $(a)

stop:
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) stop $(a)

restart: stop start

docker-clean:
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) down --remove-orphans

# ----------------------- #
# Querying the stack      #
# ----------------------- #

ls: list-apps

list-apps:
	@$(DOCKER_COMPOSE) config --services

logs:
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) logs --tail=100 -f $(a)

ps: status

status:
	@$(DOCKER_COMPOSE) -f $(DOCKER_COMPOSE_FILE) ps

# ----------------------- #
# Managing data           #
# ----------------------- #

shell:
	docker exec -it $(a) bash
