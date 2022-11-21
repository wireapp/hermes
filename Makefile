db:
	docker compose up -d db

run: db
	./gradlew run

# docker stuff
docker-run: db
	docker compose up be --build
