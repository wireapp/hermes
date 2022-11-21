db:
	docker compose up -d db

run: db
	./gradlew run
