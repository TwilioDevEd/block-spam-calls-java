install:
	mvn compile

serve:
	. .env
	mvn compile jetty:run
