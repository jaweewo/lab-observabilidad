.PHONY: up down reset status logs build

up:
	docker compose up -d

down:
	docker compose down

reset:
	docker compose down -v && docker compose up -d

status:
	bash scripts/check-lab.sh

logs:
	docker compose logs -f

build:
	docker compose build
