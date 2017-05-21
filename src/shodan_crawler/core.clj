(ns shodan-crawler.core
  (:require [clj-http.client :as c]
            [net.cgrand.enlive-html :as html]
            [clojure.string :refer [split]])
  (:gen-class))


(defn login
  "login on Shoda.io and returns a map with the returned page and cookies"
  [username password]
  (let [params {:username username
                :password password
                :grant_type "password"
                :continue "https://account.shodan.io/"
                :login_submit "Log+in"}
        cookies (clj-http.cookies/cookie-store)
        response (c/post "https://account.shodan.io/login"
                         {:insecure? true
                          :form-params params
                          :cookie-store cookies})]
    {:response response
     :cookies cookies}))


(defn parse-page-results [html-str]
  (let [html-nodes (html/html-snippet html-str)
        a-tags (html/select html-nodes [:div.pagination :a])]
    {:ip-nodes (html/select html-nodes [:div.ip])
     :next-page-url (get-in (first a-tags)
                            [:attrs :href])}))


(def get-ip (comp first :content first :content))

(defn fetch-first-page [query cookies]
  (let [resp (clj-http.client/get "https://www.shodan.io/search"
                                  {:insecure? true
                                   :cookie-store cookies
                                   :query-params {"query" 
                                                  query}})]
    {:html (:body resp)
     :cookies cookies}))

(defn parse-query-params [path]
  (let [query-str (second (split path #"\?"))
        [query page] (split query-str #"&")]
    {"query" (second (split query #"="))
     "page" (second (split page #"="))}))

(defn fetch-next-page [next-page-url cookie-store]
  (let [query (parse-query-params next-page-url)
        html-str (clj-http.client/get "https://www.shodan.io"
                                      {:insecure? true
                                       :cookie-store cookie-store
                                       :query-params query})]
    {:html (:body html-str)
     :cookies cookie-store}))

(defn filter-ips [raw-ips]
  (filter #(re-matches #"^((?:\d{1,3})\.){3}(?:\d{1,3})$" %) raw-ips))

(defn extract-ips [ip-nodes]
  (let [raw-ips (map get-ip ip-nodes)]
    (reduce conj [] (filter-ips raw-ips))))

(defn iterate-over-pages [html cookies]
  (let [parsed-html (parse-page-results html)
        ip-nodes (:ip-nodes parsed-html)
        next-url (:next-page-url parsed-html)]
    (loop [_ip-nodes ip-nodes
           _next-url next-url
           _cookies cookies]
      (if (nil? _next-url)
        (extract-ips _ip-nodes)
        (let [next-page (fetch-next-page _next-url cookies)
              _html (:html next-page)
              _cookies (:cookies next-page)
              _parsed_html (parse-page-results _html)]
          (recur (concat _ip-nodes (:ip-nodes _parsed_html))
                 (:next-page-url _parsed_html)
                 _cookies))))))

(defn -main [username password query]
  (let [cookies (:cookies (login username password))
        ret (fetch-first-page query cookies)]
    (clojure.pprint/pprint (iterate-over-pages (:html ret) (:cookies ret)))))

