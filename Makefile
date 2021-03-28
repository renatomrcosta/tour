SERVER_APP_NAME=tour-server
HEROKU_DYNO=web

.PHONY: server-docker-build
server-docker-build:
	./gradlew :$(SERVER_APP_NAME):jibDockerBuild

.PHONY: server-push-latest
server-push-latest: server-docker-build
	docker tag tour-server:latest registry.heroku.com/$(SERVER_APP_NAME)/$(HEROKU_DYNO) \
	&& docker push registry.heroku.com/$(SERVER_APP_NAME)/$(HEROKU_DYNO)

.PHONY: server-deploy-to-heroku
server-deploy-to-heroku: server-push-latest
	heroku container:release $(HEROKU_DYNO)
