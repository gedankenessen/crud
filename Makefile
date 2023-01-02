run-compose: build-compose
	docker-compose up

build-compose: build-docker
	docker-compose build

run-docker: build-docker
	docker run -p 3004:3004 crud

build-docker: build-crud
	docker build . -t crud

build-crud:
	lein uberjar

run:
	java -jar target/default+uberjar/crud-*-standalone.jar
