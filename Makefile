run_postgres_db:
	docker run --name authPostgresDB -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=users -d postgres

create_docker_image:
	mvn spring-boot:build-image
