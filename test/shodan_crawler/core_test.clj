(ns shodan-crawler.core-test
  (:require [clojure.test :refer :all]
            [shodan-crawler.core :as core]))

(deftest test-parse-page-results
  (testing "Testing the parser"
    (is (= 10 (count (:ip-nodes (core/parse-page-results
                                 (slurp "test/resources/html.html"))))))))

(deftest test-parse-query-params
  (testing "Testing query params parser"
    (let [{:keys [query page]} (core/parse-query-params
                                (:next-page-url (core/parse-page-results
                                                 (slurp "test/resources/html.html"))))]
      (is (= query "ip+cam"))
      (is (= page "2")))))

(deftest test-get-ip
  (testing  "Get the ip from a ip-node"
    (is (= "204.154.246.221"
           (core/get-ip (first (:ip-nodes (core/parse-page-results (slurp "test/resources/html.html")))))))))

(deftest test-extract-ips
  (testing "Testing extract-ips, it should only extract valid IPv4"
    (let [expected-sec '("204.154.246.221" "14.53.2.24" "79.252.221.109" "109.195.35.82" 
                         "177.124.73.132" "87.121.76.111" "80.74.166.142")
          filtered-ips (core/extract-ips (:ip-nodes (core/parse-page-results (slurp "test/resources/html.html"))))]
      (is (= 7 (count filtered-ips)))
      (is (reduce #(= true %1 %2) (map #(.contains expected-sec %) filtered-ips))))))

(run-tests)
