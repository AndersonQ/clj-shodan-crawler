# shodan-crawler

A wee crawler for shodan.io

## Motivation
Play around with clojure :)

## Requirements

1) Java JDK version 6 or later
2) [Leiningen](https://leiningen.org/)

## Usage

```
    $ lein run username password query
```

## TODO:

 - Fix and improve the tests 

### Bugs

 - It isn't fetching the second page. It seems there is something wrong with the cookies, it's like the second request isn't authenticated

## License

Copyright © 2017 Anderson Queiroz

Distributed under the GNU General Public License either version 3.0 or any later version.
