(defproject shodan-crawler "0.1.0-SNAPSHOT"
  :description "A wee crawler for shodan.io"
  :url "http://example.com/FIXME"
  :license {:name "GPLv3"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.3.0"]
                 [enlive "1.1.6"]]
  :main ^:skip-aot shodan-crawler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
