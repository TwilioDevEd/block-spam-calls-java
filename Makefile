.PHONY: install serve

install:
	mvn compile

serve:
	mvn compile jetty:run
