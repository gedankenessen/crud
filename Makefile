run-compose: build-compose
	docker-compose up

build-compose: build-docker
	docker-compose build

run-docker: build-docker
	docker run crud

build-docker: build-crud
	docker build . -t crud

build-crud:
	lein uberjar
