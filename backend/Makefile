.PHONY: clean run test uberjar

clean:
	clojure -T:build clean

run:
        clojure -M -m dvd-rental-store.server

test:
	clojure -X:test

uberjar: clean
	clojure -T:build uberjar
